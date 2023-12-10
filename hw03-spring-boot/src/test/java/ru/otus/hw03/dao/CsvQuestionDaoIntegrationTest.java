package ru.otus.hw03.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw03.config.TestFileNameProvider;
import ru.otus.hw03.domain.Question;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class CsvQuestionDaoIntegrationTest {

    @Mock
    private TestFileNameProvider testFileNameProvider;

    private static final int EXPECTED_QUESTION_COUNT = 6;
    @Test
    void shouldFindAllQuestions() {
        Mockito.when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
        List<Question> questions = csvQuestionDao.findAll();
        assertNotNull(questions);
        assertEquals(EXPECTED_QUESTION_COUNT, questions.size());
    }
}