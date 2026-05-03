package com.pjotr.trivia;

import java.util.List;

public record Questions(
    String type,
    String difficulty,
    String category,
    String question,
    String correct_answer,
    List<String> incorrect_answers
) {}