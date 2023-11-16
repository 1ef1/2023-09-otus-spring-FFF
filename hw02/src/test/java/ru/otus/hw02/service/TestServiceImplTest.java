package ru.otus.hw02.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw02.dao.CsvQuestionDao;
import ru.otus.hw02.domain.Answer;
import ru.otus.hw02.domain.Question;
import ru.otus.hw02.domain.Student;
import ru.otus.hw02.domain.TestResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {
    @Mock
    private CsvQuestionDao csvQuestionDao;

    @Mock
    private IOService ioService;

    @Test
    void executeTestFor() {
        when(ioService.readIntForRangeWithPrompt(1, 3, "your answer", "error input number question")).thenReturn(2);
        String textQuestion = "Question1";
        Answer answer1 = new Answer("Answer1", false);
        Answer answer2 = new Answer("Answer2", true);
        Answer answer3 = new Answer("Answer3", false);
        List<Answer> answerList = new ArrayList<>();
        answerList.add(answer1);
        answerList.add(answer2);
        answerList.add(answer3);
        Question question = new Question(textQuestion, answerList);
        List<Question> questionList = new ArrayList();
        questionList.add(question);
        when(csvQuestionDao.findAll()).thenReturn(questionList);
        TestServiceImpl testService = new TestServiceImpl(ioService, csvQuestionDao);
        TestResult testResult = testService.executeTestFor(new Student("firstNameStudent", "lastNameStudent"));
        Assertions.assertEquals(1, testResult.getRightAnswersCount());
    }
}
