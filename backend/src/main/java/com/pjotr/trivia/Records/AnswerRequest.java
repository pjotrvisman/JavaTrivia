package com.pjotr.trivia;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public record AnswerRequest(
    String provided_answer
) {
    public String getProvided_answer() {
        return provided_answer;
    }
}