package ru.otus.hw02.dao;

import org.junit.jupiter.api.Test;
import ru.otus.hw02.config.TestFileNameProvider;
import ru.otus.hw02.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CsvQuestionDaoIntegrationTest {
    private static final int EXPECTED_QUESTION_COUNT = 6;

    @Test
    void shouldFindAllQuestions() {
        TestFileNameProvider testFileNameProvider = () -> "questions.csv";

        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(EXPECTED_QUESTION_COUNT, questions.size());
    }
}