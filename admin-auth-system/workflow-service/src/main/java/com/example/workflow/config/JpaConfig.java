package com.example.workflow.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Configuration
public class JpaConfig {

    @Bean
    public DateTimeProvider auditingDateTimeProvider(){
        return ()-> Optional.of(OffsetDateTime.now());
    }

    @Bean
    public AuditorAware<UUID> auditorProvider(){
        return ()->{

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth==null || !auth.isAuthenticated()){
                return Optional.empty();
            }

            if(auth.getPrincipal() instanceof Jwt jwt){
                return Optional.of(UUID.fromString(jwt.getSubject()));
            }

            return Optional.empty();

        };

    }

}
