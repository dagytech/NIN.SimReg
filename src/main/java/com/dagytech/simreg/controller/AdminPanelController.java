package com.dagytech.simreg.controller;

import com.dagytech.simreg.dto.LoginRequest;
import com.dagytech.simreg.dto.SystemStatsResponse;
import com.dagytech.simreg.repository.*;
import com.dagytech.simreg.service.SecurityAuditService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * "ACCESS POINT" 3 - Admin Web Dashboard (STATEFUL / session-based).
 *
 * Tofauti na /staff/login (JWT - stateless), hapa tunatumia SESSION halisi:
 * baada ya login, server inahifadhi "SecurityContext" ndani ya HttpSession
 * (upande wa server), na browser inapokea "JSESSIONID" cookie. Kila request
 * ijayo, server inaangalia session yake yenyewe kujua "huyu ni nani" - HAIHITAJI
 * kusoma/kuthibitisha token yoyote tena.
 *
 * FAIDA kwa admin panel: tukitaka "kumfukuza" mtumiaji papo hapo (logout ya
 * kulazimishwa - mfano mfanyakazi ameachishwa kazi), tunafuta tu session yake
 * upande wa server - ufikiaji unaisha MARA MOJA. Kwa JWT (stateless), token
 * iliyokwishatolewa inabaki "halali" mpaka muda wake uishe wenyewe, isipokuwa
 * tuwe na "blocklist" ya ziada - stateful inarahisisha hili.
 */
@RestController
@RequestMapping("/admin-panel")
public class AdminPanelController {

    private final AuthenticationManager authenticationManager;
    private final SecurityAuditService securityAuditService;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    private final CustomerRepository customerRepository;
    private final SimRegistrationRepository simRegistrationRepository;
    private final SecurityAuditLogRepository securityAuditLogRepository;

    @Autowired
    public AdminPanelController(AuthenticationManager authenticationManager, SecurityAuditService securityAuditService,
                                 CustomerRepository customerRepository,
                                 SimRegistrationRepository simRegistrationRepository,
                                 SecurityAuditLogRepository securityAuditLogRepository) {
        this.authenticationManager = authenticationManager;
        this.securityAuditService = securityAuditService;
        this.customerRepository = customerRepository;
        this.simRegistrationRepository = simRegistrationRepository;
        this.securityAuditLogRepository = securityAuditLogRepository;
    }

    // POST /admin-panel/login - inatengeneza SESSION (siyo JWT)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request, HttpServletResponse response) {
        Authentication authRequest = new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());
        Authentication authResult;
        try {
            authResult = authenticationManager.authenticate(authRequest);
        } catch (BadCredentialsException e) {
            securityAuditService.log("LOGIN_FAILED", "/admin-panel/login", request.getRemoteAddr(),
                    "Session login imeshindwa: " + req.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "Username au password si sahihi"));
        }

        // Hapa ndipo "STATE" inapotengenezwa - tunahifadhi SecurityContext
        // kwenye HttpSession, na Spring inatuma JSESSIONID cookie kwa browser
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
        securityContextRepository.saveContext(context, request, response);

        securityAuditService.log("SESSION_LOGIN_SUCCESS", "/admin-panel/login", request.getRemoteAddr(),
                "Session imeanzishwa kwa: " + req.getUsername());

        return ResponseEntity.ok(Map.of(
                "message", "Umeingia (session imeanzishwa)",
                "username", req.getUsername(),
                "sessionId", request.getSession().getId()
        ));
    }

    // POST /admin-panel/logout - inafuta session MARA MOJA (stateful advantage)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication() != null
                ? SecurityContextHolder.getContext().getAuthentication().getName() : "unknown";

        request.getSession().invalidate();          // <- hii ndiyo "nguvu" ya stateful
        SecurityContextHolder.clearContext();

        securityAuditService.log("SESSION_LOGOUT", "/admin-panel/logout", request.getRemoteAddr(),
                "Session imefutwa kwa: " + username);

        return ResponseEntity.ok(Map.of("message", "Umetoka (session imefutwa kabisa)"));
    }

    // GET /admin-panel/dashboard - inalindwa na SESSION (angalia SecurityConfig)
    @GetMapping("/dashboard")
    public ResponseEntity<SystemStatsResponse> dashboard() {
        long totalCustomers = customerRepository.count();
        long totalReg = simRegistrationRepository.count();
        long completed = simRegistrationRepository.findAll().stream()
                .filter(r -> "COMPLETED".equals(r.getStatus())).count();
        long pending = totalReg - completed;
        long securityEvents = securityAuditLogRepository.findTop50ByOrderByOccurredAtDesc().size();

        return ResponseEntity.ok(new SystemStatsResponse(totalCustomers, totalReg, completed, pending, securityEvents));
    }
}
