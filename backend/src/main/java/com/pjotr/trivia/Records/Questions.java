package com.pjotr.trivia;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public record Questions(
    String type,
    String difficulty,
    String category,
    String question,
    String correct_answer,
    List<String> incorrect_answers
) {
    @JsonProperty("answers")
    public List<String> getAnswers() {
        var answers = new ArrayList<String>(incorrect_answers);
        answers.add(correct_answer);
        java.util.Collections.shuffle(answers);
        return answers;
    }

    public String getCorrect_answer() {
        return null;
    }

    public List<String> getIncorrect_answers() {
        return null;
    }
}