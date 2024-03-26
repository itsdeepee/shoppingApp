package org.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;


@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    /**
     * Configures security for the WebFlux application.
     * Disables CSRF protection.
     * Authorizes access to "/eureka/**" without authentication.
     * Requires authentication for any other endpoints.
     * Configures OAuth 2.0 resource server with JWT token support.
     *
     * @param serverHttpSecurity ServerHttpSecurity instance to configure security
     * @return SecurityWebFilterChain configured security filter chain
     */
    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity){
        serverHttpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchange->exchange
                        .pathMatchers("/eureka/**")
                        .permitAll()
                        .anyExchange().authenticated())
                .oauth2ResourceServer(spec->spec.jwt(Customizer.withDefaults()));

        return serverHttpSecurity.build();

    }

}
