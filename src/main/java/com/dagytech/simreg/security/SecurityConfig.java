package com.dagytech.simreg.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * MUUNDO WA USALAMA WA MFUMO (Security Architecture):
 *
 * "ACCESS POINT" 1 - Mteja (Mobile App):
 *   /verify-nin, /verify-biometric, /start-registration, /approve-registration,
 *   /register-sim - hazihitaji "login" (mteja bado hajajulikana), lakini
 *   ZINALINDWA na: RateLimitFilter -> ApiKeyFilter -> HmacSignatureFilter
 *   (tabaka 3 za ulinzi kabla ya kufika kwenye controller kabisa).
 *
 * "ACCESS POINT" 2 - Staff (Agent/Admin):
 *   /staff/login (JWT login), /admin/**, /audit-trail/** - zinahitaji
 *   "Authorization: Bearer <JWT>" - inathibitishwa na JwtAuthenticationFilter.
 *
 * STATELESS: hakuna "session" inayohifadhiwa upande wa server - kila request
 * lazima ilete uthibitisho wake yenyewe (JWT au API Key+Signature).
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ApiKeyFilter apiKeyFilter;
    private final HmacSignatureFilter hmacSignatureFilter;
    private final RateLimitFilter rateLimitFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, ApiKeyFilter apiKeyFilter,
                           HmacSignatureFilter hmacSignatureFilter, RateLimitFilter rateLimitFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.apiKeyFilter = apiKeyFilter;
        this.hmacSignatureFilter = hmacSignatureFilter;
        this.rateLimitFilter = rateLimitFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(CustomUserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    // =====================================================================
    // FILTER CHAIN 1 (STATEFUL) - Admin Web Panel - /admin-panel/**
    // Session ya kawaida (IF_REQUIRED) - inatengeneza JSESSIONID cookie.
    // Order(1) - inaangaliwa KWANZA kwa sababu ina securityMatcher maalum.
    // =====================================================================
    @Bean
    @Order(1)
    public SecurityFilterChain sessionFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin-panel/**")
            .csrf(csrf -> csrf.disable())
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin-panel/login").permitAll()
                .requestMatchers("/admin-panel/**").hasAnyRole("ADMIN", "AGENT")
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\":\"Huna session halali - ingia (/admin-panel/login) kwanza\"}");
                })
            );
        return http.build();
    }

    // =====================================================================
    // FILTER CHAIN 2 (STATELESS) - Mobile App (mteja) + Staff JWT (/staff/**)
    // Hii ndiyo ile ya awali - haina session yoyote.
    // Order(2) - inashughulikia kila kitu kingine kisichofanana na /admin-panel/**
    // =====================================================================
    @Bean
    @Order(2)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())

            // STATELESS - hakuna session ya server, kila request inajithibitisha yenyewe
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            .authorizeHttpRequests(auth -> auth
                // Ukurasa wa mbele (UI) - wazi kwa wote
                .requestMatchers("/", "/index.html", "/manifest.json", "/sw.js", "/icons/**").permitAll()

                // Staff login - wazi (huu ndiyo mlango wa kuomba JWT)
                .requestMatchers("/staff/login").permitAll()

                // Mtiririko wa mteja - HAUHITAJI Spring Security auth (unalindwa
                // na ApiKeyFilter + HmacSignatureFilter + RateLimitFilter badala yake)
                .requestMatchers("/verify-nin", "/verify-biometric", "/start-registration",
                        "/approve-registration", "/register-sim", "/registration-status/**",
                        "/customer-sims/**").permitAll()

                // Admin/Agent tu - JWT inahitajika
                .requestMatchers("/admin/**").hasAnyRole("ADMIN", "AGENT")
                .requestMatchers("/audit-trail/**", "/security-audit/**").hasAnyRole("ADMIN", "AGENT")

                .anyRequest().authenticated()
            )

            // Tabaka za ulinzi kwa "access point" ya mteja - zinaendeshwa KABLA
            // ya Spring Security's default authentication filter
            .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(hmacSignatureFilter, UsernamePasswordAuthenticationFilter.class)

            // JWT kwa "access point" ya staff
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\":\"Huna ruhusa - ingia kwanza (login)\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json");
                    response.setStatus(403);
                    response.getWriter().write("{\"error\":\"Hauruhusiwi kufanya kitendo hiki\"}");
                })
            );

        return http.build();
    }
}
