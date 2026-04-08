package com.example.job.portal.Naukri4U.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        CookieCsrfTokenRepository repo = CookieCsrfTokenRepository.withHttpOnlyFalse();

        repo.setCookiePath("/");
        repo.setCookieName("XSRF-TOKEN");   // 🔥 IMPORTANT

        http
                .cors(withDefaults())

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/csrf-token",
                                "/health",
                                "/login/**",
                                "/user/register",
                                "/v1/session/keep-alive",
                                "/v1/session/info",
                                "/v1/session/logout",
                                "/admin/register",
                                "/v1/register/company",
                                "/admin/pendingApprovals/get/all",
                                "/admin/approveRecruiter/id/**",
                                "/v1/job/add",
                                "/v1/job/info",
                                "/v1/job/info/all",
                                "/candidate/session",
                                "/v1/job/info/criteria1",
                                "/v1/job/info/criteria2",
                                "/auth/getOTP",
                                "/v1/auth/validate",
                                "/recruiter/job",
                                "/candidate/job/apply",
                                "/admin/dpCloud/session",
                                "/login/v1/dpCloud/auth",
                                "/admin/dpCloud/getOTP"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .logout(logout -> logout
                        .logoutUrl("/v1/rest-sts/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // 🔥 IMPORTANT

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }
}