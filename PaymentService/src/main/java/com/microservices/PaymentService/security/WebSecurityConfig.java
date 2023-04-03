package com.microservices.PaymentService.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests(authorizedReqeust -> authorizedReqeust
                            .antMatchers("/payment/**")
                                    .hasAuthority("SCOPE_intenal")
                                    .anyRequest()
                                    .authenticated())
                    .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
            return http.build();
    }
}
