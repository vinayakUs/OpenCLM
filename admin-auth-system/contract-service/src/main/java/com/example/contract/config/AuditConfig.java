package com.example.contract.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class AuditConfig {
    @Bean
    public DateTimeProvider auditingDateTimeProvider(){
        return ()-> Optional.of(OffsetDateTime.now());
    }

    @Bean
    public AuditorAware<UUID> auditorProvider(){
     return  ()->
         Optional.ofNullable(SecurityContextHolder.getContext())
                 .map(securityContext -> securityContext.getAuthentication())
                 .filter(authentication -> {return authentication.isAuthenticated();})
                 .map(authentication -> authentication.getPrincipal())
                 .filter(principle->principle instanceof Jwt)
                 .map(principle->UUID.fromString(((Jwt) principle).getSubject()));

    }
}
