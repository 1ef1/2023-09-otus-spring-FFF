package ru.otus.hw04.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import ru.otus.hw04.config.AppConfig;
import ru.otus.hw04.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@SpringBootTest
class CsvQuestionDaoIntegrationTest {

    @DynamicPropertySource
    static void shellProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.shell.interactive.enabled", () -> "false");
    }

    @MockBean
    private AppConfig appConfig;

    @Autowired
    private CsvQuestionDao csvQuestionDao; //

    private static final int EXPECTED_QUESTION_COUNT = 6;



    @Test
    void shouldFindAllQuestions() {
        when(appConfig.getTestFileName()).thenReturn("questions.csv");
        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(EXPECTED_QUESTION_COUNT, questions.size());
    }
}