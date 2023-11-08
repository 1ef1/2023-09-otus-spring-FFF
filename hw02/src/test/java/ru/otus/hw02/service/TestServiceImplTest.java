package ru.otus.hw02.service;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw02.dao.QuestionDao;
import ru.otus.hw02.domain.Student;
import ru.otus.hw02.domain.TestResult;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {

    @Mock
    private Student student;

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;
    @InjectMocks
    private TestServiceImpl testService;

    @Test
    void executeTestFor() {
        TestResult testResult = testService.executeTestFor(student);
        Assert.assertEquals(testResult.getAnsweredQuestions().size(), testResult.getRightAnswersCount());
    }
}
