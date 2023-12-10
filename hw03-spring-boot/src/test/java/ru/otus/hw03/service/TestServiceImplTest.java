package ru.otus.hw03.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw03.dao.CsvQuestionDao;
import ru.otus.hw03.domain.Answer;
import ru.otus.hw03.domain.Question;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TestServiceImplTest {
    @Mock
    private CsvQuestionDao csvQuestionDao;

    @Mock
    private LocalizedIOService ioService;

    @Test
    void executeTestFor() {
        when(ioService.readIntForRangeWithPromptLocalized(1, 3, "TestService.answer.the.answer",
                "TestService.answer.the.error")).thenReturn(2);
        String textQuestion = "Question1";
        Answer answer1 = new Answer("Answer1", false);
        Answer answer2 = new Answer("Answer2", true);
        Answer answer3 = new Answer("Answer3", false);
        List<Answer> answerList = new ArrayList<>();
        answerList.add(answer1);
        answerList.add(answer2);
        answerList.add(answer3);
        Question question = new Question(textQuestion, answerList);
        List<Question> questionList = new ArrayList<>();
        questionList.add(question);
        when(csvQuestionDao.findAll()).thenReturn(questionList);
        TestServiceImpl testService = new TestServiceImpl(ioService, csvQuestionDao);
        TestResult testResult = testService.executeTestFor(new Student("firstNameStudent", "lastNameStudent"));
        Assertions.assertEquals(1, testResult.getRightAnswersCount());
    }
}
