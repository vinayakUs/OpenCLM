package com.example.bff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http,
                        ClientRegistrationRepository clientRegistrationRepository,
                        com.example.bff.service.CustomOidcUserService customOidcUserService) throws Exception {
                OidcClientInitiatedLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedLogoutSuccessHandler(
                                clientRegistrationRepository);
                logoutSuccessHandler.setPostLogoutRedirectUri("http://localhost:4200/login?logout");

                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/api/user").permitAll()
                                                .anyRequest().authenticated())
                                .oauth2Login(oauth2 -> oauth2
                                                .userInfoEndpoint(userInfo -> userInfo
                                                                .oidcUserService(customOidcUserService))
                                                .authorizationEndpoint(authorization -> authorization
                                                                .baseUri("/api/oauth2/authorization"))
                                                .redirectionEndpoint(redirection -> redirection
                                                                .baseUri("/api/login/oauth2/code/*"))
                                                .defaultSuccessUrl("http://localhost:4200/home", true))
                                .logout(logout -> logout
                                                .logoutUrl("/api/logout")
                                                .logoutSuccessHandler(logoutSuccessHandler)
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("BFFSESSIONID")
                                                .permitAll());

                return http.build();
        }
}
