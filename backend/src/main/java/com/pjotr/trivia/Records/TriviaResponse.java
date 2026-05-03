package com.pjotr.trivia;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record TriviaResponse(
    @JsonProperty("response_code") int responseCode,
    List<Questions> results
) {
    public List<Questions> getResults() {
        return results;
    }
}