// single responsibility/  Kukagua kikomo cha maombi TU


package com.dagytech.simreg.security;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
 * RATE LIMITING - kila IP inaruhusiwa kufanya maombi mangapi kwa dakika moja
 * kwenye endpoints nyeti (usajili). Hii inazuia:
 * - Mtu "kujaribu-jaribu" NIN nyingi kwa haraka (brute force)
 * - "Bot" kutuma maombi maelfu kwa sekunde (denial of service - DoS)
 *
 * Tunatumia "sliding window" rahisi: tunahifadhi muda wa kila ombi la IP hiyo
 * ndani ya dakika iliyopita; likizidi kikomo, tunakataa.
 */
@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private final int limitPerMinute;
    private final SecurityAuditService securityAuditService;
    private final ConcurrentHashMap<String, List<Long>> requestLog = new ConcurrentHashMap<>();

    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/verify-nin", "/verify-biometric", "/start-registration",
            "/approve-registration", "/register-sim"
    );

    @Autowired
    public RateLimitFilter(@Value("${app.security.rate-limit-per-minute}") int limitPerMinute,
                            SecurityAuditService securityAuditService) {
        this.limitPerMinute = limitPerMinute;
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

        String ip = request.getRemoteAddr();
        long now = System.currentTimeMillis();
        long oneMinuteAgo = now - 60_000;

        List<Long> timestamps = requestLog.computeIfAbsent(ip, k -> new java.util.concurrent.CopyOnWriteArrayList<>());
        timestamps.removeIf(t -> t < oneMinuteAgo);

        if (timestamps.size() >= limitPerMinute) {
            securityAuditService.log("RATE_LIMITED", path, ip,
                    "Umezidi kikomo cha maombi (" + limitPerMinute + "/dakika)");
            response.setContentType("application/json");
            response.setStatus(429);
            response.getWriter().write("{\"error\":\"Maombi mengi mno - jaribu tena baada ya dakika chache\"}");
            return;
        }

        timestamps.add(now);
        filterChain.doFilter(request, response);
    }
}
