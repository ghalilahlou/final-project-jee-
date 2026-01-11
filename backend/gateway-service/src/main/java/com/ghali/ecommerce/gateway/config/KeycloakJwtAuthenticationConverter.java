package com.ghali.ecommerce.gateway.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convertisseur JWT Keycloak pour extraire les rôles
 * Inspiré du projet sécurité - conversion des realm_access roles
 */
public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        AbstractAuthenticationToken authenticationToken = new JwtAuthenticationToken(jwt, authorities);
        return Mono.just(authenticationToken);
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        // Extraction des rôles depuis realm_access
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");

        if (realmAccess != null && realmAccess.containsKey("roles")) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");

            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        }

        // Extraction depuis resource_access si disponible
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess != null) {
            return resourceAccess.values().stream()
                    .filter(Map.class::isInstance)
                    .map(Map.class::cast)
                    .filter(map -> map.containsKey("roles"))
                    .flatMap(map -> {
                        @SuppressWarnings("unchecked")
                        List<String> roles = (List<String>) map.get("roles");
                        return roles.stream();
                    })
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
