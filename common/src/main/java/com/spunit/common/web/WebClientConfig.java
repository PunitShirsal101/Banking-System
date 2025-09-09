package com.spunit.common.web;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Configuration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
public class WebClientConfig {

    @Bean
    @ConditionalOnMissingBean
    public WebClient.Builder webClientBuilder() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofMillis(500))
                .compress(true);
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(jwtRelayFilter());
    }

    private static ExchangeFilterFunction jwtRelayFilter() {
        return (request, next) -> ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> next.exchange(attachJwt(request, auth)))
                .switchIfEmpty(next.exchange(request));
    }

    private static org.springframework.web.reactive.function.client.ClientRequest attachJwt(
            org.springframework.web.reactive.function.client.ClientRequest request,
            Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwt) {
            return org.springframework.web.reactive.function.client.ClientRequest.from(request)
                    .headers(h -> h.setBearerAuth(jwt.getToken().getTokenValue()))
                    .build();
        }
        return request;
    }
}
