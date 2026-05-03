package com.pjotr.trivia;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import java.time.Duration;
import java.util.concurrent.TimeoutException;
import java.util.Map;

import reactor.core.publisher.Mono;

@Component
public class TriviaHandler {
    
    private final WebClient webClient;
    
    public TriviaHandler() {
        this.webClient = WebClient.create("https://opentdb.com:443");
    }

    public Mono<ServerResponse> fetchQuestions(ServerRequest request) {
        return this.webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api.php")
                .queryParam("amount", 1)
                .build())
            .retrieve()
            .bodyToMono(TriviaResponse.class)
            .timeout(Duration.ofSeconds(5))
            .flatMap(triviaResponse ->
                ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(triviaResponse))
            .onErrorResume(WebClientResponseException.class, e -> 
                ServerResponse.status(e.getStatusCode())
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("status", e.getStatusCode().value())))
            .onErrorResume(TimeoutException.class, e -> 
                ServerResponse.status(HttpStatus.GATEWAY_TIMEOUT)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("status", 504, "error", "timeout")))
            .onErrorResume(Exception.class, e -> 
                ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("status", 503, "error", "service_unavailable")));
    }
}