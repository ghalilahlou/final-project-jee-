package com.ghali.ecommerce.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuration de sécurité avec Keycloak OAuth2/OIDC
 * Inspirée du projet sécurité original
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            
            .authorizeExchange(exchange -> exchange
                // Endpoints publics
                .pathMatchers("/actuator/health", "/actuator/info").permitAll()
                .pathMatchers("/api/chatbot/**").permitAll()
                .pathMatchers(HttpMethod.GET, "/api/products/**").permitAll()
                
                // Endpoints pour les clients authentifiés
                .pathMatchers("/api/orders/**").hasAnyRole("CUSTOMER", "ADMIN")
                .pathMatchers("/api/customers/me/**").hasAnyRole("CUSTOMER", "ADMIN")
                .pathMatchers("/api/invoices/customer/**").hasAnyRole("CUSTOMER", "ADMIN")
                .pathMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .pathMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")
                
                // Endpoints admin uniquement
                .pathMatchers("/api/invoices/**").hasRole("ADMIN")
                .pathMatchers("/api/customers/search/**").hasRole("ADMIN")
                .pathMatchers("/api/customers/top/**").hasRole("ADMIN")
                .pathMatchers("/api/customers/{id}").hasRole("ADMIN")
                .pathMatchers("/api/users/**").hasRole("ADMIN")
                .pathMatchers("/api/notifications/**").hasRole("ADMIN")
                
                // Tout le reste nécessite une authentification
                .anyExchange().authenticated()
            )
            
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(new KeycloakJwtAuthenticationConverter()))
            );

        return http.build();
    }
}
