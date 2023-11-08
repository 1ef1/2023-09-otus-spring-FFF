package ru.otus.hw02.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw02.config.AppConfig;
import ru.otus.hw02.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest(classes = AppConfig.class)
class CsvQuestionDaoIntegrationTest {
    @Autowired
    private CsvQuestionDao csvQuestionDao;

    private static final int EXPECTED_QUESTION_COUNT = 6;

    @Test
    void shouldFindAllQuestions() {
        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(EXPECTED_QUESTION_COUNT, questions.size());
    }
}