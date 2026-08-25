package com.dagytech.simreg.controller;

import com.dagytech.simreg.dto.LoginRequest;
import com.dagytech.simreg.dto.LoginResponse;
import com.dagytech.simreg.model.StaffUser;
import com.dagytech.simreg.repository.StaffUserRepository;
import com.dagytech.simreg.security.JwtService;
import com.dagytech.simreg.service.SecurityAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * "Access point" ya PILI ya mfumo: Agent/Admin - hii inatumia JWT (login ya
 * kawaida ya username+password), tofauti kabisa na mteja (mobile app) anayetumia
 * API Key + HMAC signature (angalia ApiKeyFilter/HmacSignatureFilter).
 */
@RestController
@RequestMapping("/staff")
public class StaffAuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final StaffUserRepository staffUserRepository;
    private final SecurityAuditService securityAuditService;

    @Value("${app.security.jwt-expiry-minutes}")
    private long jwtExpiryMinutes;

    @Autowired
    public StaffAuthController(AuthenticationManager authenticationManager, JwtService jwtService,
                                StaffUserRepository staffUserRepository, SecurityAuditService securityAuditService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.staffUserRepository = staffUserRepository;
        this.securityAuditService = securityAuditService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req, jakarta.servlet.http.HttpServletRequest httpReq) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
        } catch (BadCredentialsException e) {
            securityAuditService.log("LOGIN_FAILED", "/staff/login", httpReq.getRemoteAddr(),
                    "Jaribio la login lisilofanikiwa: " + req.getUsername());
            return ResponseEntity.status(401).body(Map.of("error", "Username au password si sahihi"));
        }

        StaffUser staff = staffUserRepository.findById(req.getUsername()).orElseThrow();
        String token = jwtService.generateToken(staff.getUsername(), staff.getRole());

        securityAuditService.log("LOGIN_SUCCESS", "/staff/login", httpReq.getRemoteAddr(),
                "Login ya mafanikio: " + staff.getUsername() + " (" + staff.getRole() + ")");

        return ResponseEntity.ok(new LoginResponse(token, staff.getUsername(), staff.getRole(), jwtExpiryMinutes));
    }
}
