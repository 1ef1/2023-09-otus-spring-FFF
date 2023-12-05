package ru.otus.hw02.domain;

import java.util.List;

public record Question(String text, List<Answer> answers) {
}
