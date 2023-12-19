package ru.otus.hw04.dao;

import ru.otus.hw04.domain.Question;

import java.util.List;

public interface QuestionDao {
    List<Question> findAll();
}
