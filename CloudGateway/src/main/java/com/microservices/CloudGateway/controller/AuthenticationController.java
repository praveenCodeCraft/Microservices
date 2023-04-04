package com.microservices.CloudGateway.controller;

import com.microservices.CloudGateway.model.AuthenticationResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.aspectj.weaver.ArrayReferenceType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.PartialResultException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/authenticate")
@Log4j2
public class AuthenticationController {

    @GetMapping("/login")
    public ResponseEntity<AuthenticationResponse> login( @AuthenticationPrincipal OidcUser oidcUser ,
                                                        Model model , @RegisteredOAuth2AuthorizedClient("okta") OAuth2AuthorizedClient client){
        log.info("inside the login mehtod {}" , oidcUser );
        log.info("email id is {}"  , oidcUser.getEmail());
            AuthenticationResponse authenticationResponse = AuthenticationResponse.builder()
                    .userId(oidcUser.getEmail())
                    .accessToken(client.getAccessToken().getTokenValue())
                    .refreshToken(client.getRefreshToken().getTokenValue())
                    .expiresAt(client.getAccessToken().getExpiresAt().getEpochSecond())
                    .authorityList(oidcUser.getAuthorities()
                            .stream()
                            .map(GrantedAuthority::getAuthority)
                            .collect(Collectors.toList()))
                    .build();

            return new ResponseEntity<>(authenticationResponse, HttpStatus.OK);
    }

}
