package com.example.bff.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @GetMapping("/user")
    public Map<String, Object> getUser(@AuthenticationPrincipal OidcUser principal , @RegisteredOAuth2AuthorizedClient("bff-client") OAuth2AuthorizedClient client) {
        Map<String, Object> userDetails = new HashMap<>();
        if (principal != null) {
            userDetails.put("name", principal.getFullName());
            userDetails.put("email", principal.getEmail());
            userDetails.put("attributes", principal.getAttributes());
            userDetails.put("jwt", client.getAccessToken().getTokenValue());
        }
        return userDetails;
    }
}
