// single responsibility/  Kukagua signature/replay TU

package com.dagytech.simreg.security;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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
 * API REQUEST SIGNATURES + REPLAY ATTACK PREVENTION.

 */
@Component
public class HmacSignatureFilter extends OncePerRequestFilter {

    private final String signingSecret;
    private final long replayWindowMillis;
    private final SecurityAuditService securityAuditService;

    // "Kumbukumbu" ya muda ya nonce zilizoshatumika - inazuia matumizi ya pili
    private final ConcurrentHashMap<String, Long> usedNonces = new ConcurrentHashMap<>();

    private static final Set<String> PROTECTED_PATHS = Set.of(
            "/verify-nin", "/verify-biometric", "/start-registration",
            "/approve-registration", "/register-sim"
    );

    @Autowired
    public HmacSignatureFilter(@Value("${app.security.signing-secret}") String signingSecret,
                                @Value("${app.security.replay-window-seconds}") long replayWindowSeconds,
                                SecurityAuditService securityAuditService) {
        this.signingSecret = signingSecret;
        this.replayWindowMillis = replayWindowSeconds * 1000;
        this.securityAuditService = securityAuditService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest rawRequest, @NonNull HttpServletResponse response,
                                     @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = rawRequest.getRequestURI();
        if (!PROTECTED_PATHS.contains(path)) {
            filterChain.doFilter(rawRequest, response);
            return;
        }

        // Tunafunga (wrap) request na wrapper YETU WENYEWE inayoruhusu kusoma
        // mwili (body) MARA NYINGI - tofauti na stream ya kawaida ya Java
        // inayosomwa mara moja tu (angalia maelezo kwenye RepeatableReadRequestWrapper.java)
        RepeatableReadRequestWrapper request = new RepeatableReadRequestWrapper(rawRequest);
        String body = request.getCachedBodyAsString();

        String timestampHeader = request.getHeader("X-Timestamp");
        String nonce = request.getHeader("X-Nonce");
        String signature = request.getHeader("X-Signature");

        if (timestampHeader == null || nonce == null || signature == null) {
            reject(response, "SIGNATURE_MISSING", path, request.getRemoteAddr(), "Header za usalama hazipo");
            return;
        }

        long timestamp;
        try {
            timestamp = Long.parseLong(timestampHeader);
        } catch (NumberFormatException e) {
            reject(response, "SIGNATURE_INVALID", path, request.getRemoteAddr(), "X-Timestamp si namba sahihi");
            return;
        }

        // ---- 1) Kagua "umri" wa request (replay window) ----
        long now = System.currentTimeMillis();
        if (Math.abs(now - timestamp) > replayWindowMillis) {
            reject(response, "REPLAY_DETECTED", path, request.getRemoteAddr(),
                    "Request ni ya zamani sana au saa ya kifaa si sahihi");
            return;
        }

        // ---- 2) Kagua nonce haijatumika kabla (replay ndani ya dirisha la muda) ----
        cleanupExpiredNonces(now);
        if (usedNonces.putIfAbsent(nonce, now) != null) {
            reject(response, "REPLAY_DETECTED", path, request.getRemoteAddr(),
                    "Nonce hii tayari imetumika - hii ni 'replay' ya request ya awali");
            return;
        }

        // ---- 3) Hesabu signature yetu wenyewe, linganisha na iliyotumwa ----
        String expectedSignature = computeHmac(request.getMethod(), path, timestampHeader, nonce, body);
        if (!expectedSignature.equalsIgnoreCase(signature)) {
            reject(response, "SIGNATURE_INVALID", path, request.getRemoteAddr(),
                    "Signature haifanani - request huenda imebadilishwa njiani");
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String computeHmac(String method, String path, String timestamp, String nonce, String body) {
        try {
            String payload = method + "|" + path + "|" + timestamp + "|" + nonce + "|" + body;
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(signingSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Imeshindwa kuhesabu HMAC signature", e);
        }
    }

    private void cleanupExpiredNonces(long now) {
        usedNonces.entrySet().removeIf(entry -> (now - entry.getValue()) > replayWindowMillis);
    }

    private void reject(HttpServletResponse response, String eventType, String path, String ip, String details)
            throws IOException {
        securityAuditService.log(eventType, path, ip, details);
        response.setContentType("application/json");
        response.setStatus(401);
        response.getWriter().write("{\"error\":\"Ombi halikupitisha ukaguzi wa usalama (signature/replay check)\"}");
    }
}
