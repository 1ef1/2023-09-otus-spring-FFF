package ru.otus.hw02.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw02.config.TestFileNameProvider;
import ru.otus.hw02.dao.CsvQuestionDao;
import ru.otus.hw02.domain.Student;
import ru.otus.hw02.domain.TestResult;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {


    @Mock
    private TestFileNameProvider testFileNameProvider;


    @Mock
    private CsvQuestionDao csvQuestionDao;

    @Mock
    private IOService ioService;

    @Mock
    private Student student;

    @Test
    void executeTestFor() {
        when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");
        csvQuestionDao = new CsvQuestionDao(testFileNameProvider);
        when(ioService.readIntForRangeWithPrompt(1, 3, "your answer", "error input number question")).thenReturn(1);
        TestServiceImpl testService = new TestServiceImpl(ioService, csvQuestionDao);
        TestResult testResult = testService.executeTestFor(student);
        Assertions.assertEquals(3, testResult.getRightAnswersCount());
    }
}
