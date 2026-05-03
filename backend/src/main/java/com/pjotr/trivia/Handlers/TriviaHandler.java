package com.pjotr.trivia;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component
public class TriviaHandler {
    
    private final WebClient webClient;
    
    public TriviaHandler() {
        this.webClient = WebClient.create("https://opentdb.com:443");
    }

    public Mono<ServerResponse> fetchQuestions(ServerRequest request) {
        Mono<TriviaResponse> test = this.webClient.get()
            .uri(uriBuilder -> uriBuilder
                .path("/api.php")
                .queryParam("amount", 1)
                .build())
            .retrieve()
            .bodyToMono(TriviaResponse.class);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
            .body(test, TriviaResponse.class);
    }
}