// single responsibility/  Kukagua API Key TU

package com.dagytech.simreg.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.dagytech.simreg.service.SecurityAuditService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * API KEY YA TULI (static token) - "before registration".
 *
 * Wazo: Mobile app halisi (iliyojengwa na sisi) ina hii key IMEFICHWA ndani
 * yake (imesimbwa/hardcoded kwenye code ya app). Mtu yeyote anayejaribu
 * kupiga hizi endpoints moja kwa moja (curl, Postman, script mbaya) BILA
 * kujua hii key - anazuiwa papo hapo, kabla hata data yake haijafika kwenye
 * mantiki ya biashara (business logic).
 *
 * Hii SIYO authentication ya mtumiaji (mteja bado hajathibitishwa NIN/OTP) -
 * ni "device/app trust" tu: "hii app ndiyo app yetu halisi, siyo mgeni".
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

    private final String validApiKey;
    private final SecurityAuditService securityAuditService;

    // Njia (paths) zinazolindwa na API Key - mtiririko wa usajili wa SIM
    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/verify-nin", "/verify-biometric", "/start-registration",
            "/approve-registration", "/register-sim"
    );

    @Autowired
    public ApiKeyFilter(@Value("${app.security.api-key}") String validApiKey,
                         SecurityAuditService securityAuditService) {
        this.validApiKey = validApiKey;
        this.securityAuditService = securityAuditService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (!PROTECTED_PATHS.contains(path)) {
            filterChain.doFilter(request, response);
            return;
        }

        String providedKey = request.getHeader("X-API-Key");

        if (providedKey == null || !providedKey.equals(validApiKey)) {
            securityAuditService.log("API_KEY_REJECTED", path, request.getRemoteAddr(),
                    "API Key haipo au si sahihi");
            response.setContentType("application/json");
            response.setStatus(401);
            response.getWriter().write("{\"error\":\"Huna ruhusa ya kufikia huduma hii (invalid app credentials)\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
