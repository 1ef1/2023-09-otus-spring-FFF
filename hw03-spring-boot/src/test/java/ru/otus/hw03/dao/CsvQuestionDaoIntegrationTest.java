package ru.otus.hw03.dao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw03.config.AppConfig;
import ru.otus.hw03.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
class CsvQuestionDaoIntegrationTest {

    @MockBean
    private AppConfig appConfig;

    private static final int EXPECTED_QUESTION_COUNT = 6;

    @Test
    void shouldFindAllQuestions() {
        when(appConfig.getTestFileName()).thenReturn("questions.csv");

        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(appConfig);

        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(EXPECTED_QUESTION_COUNT, questions.size());
    }
}