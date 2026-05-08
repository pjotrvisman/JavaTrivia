package com.pjotr.trivia;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration(proxyBeanMethods = false)
public class TriviaRouter {

  @Bean
  public RouterFunction<ServerResponse> route(TriviaHandler triviaHandler) {

    return RouterFunctions.route(GET("/questions")
            .and(accept(MediaType.APPLICATION_JSON)), triviaHandler::fetchQuestions)
        .andRoute(POST("/checkanswers")
            .and(accept(MediaType.APPLICATION_JSON)), triviaHandler::checkAnswers);
  }

  @Bean
  public CorsWebFilter corsWebFilter() {
    CorsConfiguration corsConfig = new CorsConfiguration();
    corsConfig.addAllowedOrigin("http://localhost:3000");
    corsConfig.addAllowedMethod("*");
    corsConfig.addAllowedHeader("*");
    corsConfig.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfig);

    return new CorsWebFilter(source);
  }
}