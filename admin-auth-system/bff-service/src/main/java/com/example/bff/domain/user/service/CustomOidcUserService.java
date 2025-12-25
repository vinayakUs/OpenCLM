package com.example.bff.domain.user.service;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomOidcUserService extends OidcUserService {

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attributes = new HashMap<>(oidcUser.getAttributes());

        // Ensure email is populated (fallback to sub if missing)
        if (attributes.get("email") == null) {
            attributes.put("email", attributes.get("sub"));
        }

        // Ensure name is populated (fallback to sub if missing)
        if (attributes.get("name") == null) {
            attributes.put("name", attributes.get("sub"));
        }

        // Create OidcUserInfo with the modified attributes
        OidcUserInfo userInfo = new OidcUserInfo(attributes);

        return new DefaultOidcUser(
                oidcUser.getAuthorities(),
                oidcUser.getIdToken(),
                userInfo,
                "sub" // Name attribute key
        );
    }
}


