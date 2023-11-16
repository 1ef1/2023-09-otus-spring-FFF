package ru.otus.hw02.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw02.config.TestFileNameProvider;
import ru.otus.hw02.dao.CsvQuestionDao;
import ru.otus.hw02.domain.Student;
import ru.otus.hw02.domain.TestResult;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {


    @Mock
    private TestFileNameProvider testFileNameProvider;


    @InjectMocks
    private CsvQuestionDao csvQuestionDao;

    @Mock
    private IOService ioService;

    @Test
    void executeTestFor()  {
        when(testFileNameProvider.getTestFileName()).thenReturn("questions.csv");
        when(ioService.readIntForRangeWithPrompt(1, 3, "your answer", "error input number question")).thenReturn(1);
        TestServiceImpl testService = new TestServiceImpl(ioService, csvQuestionDao);
        TestResult testResult = testService.executeTestFor(new Student("firstNameStudent","lastNameStudent"));
        Assertions.assertEquals(3, testResult.getRightAnswersCount());
    }
}
