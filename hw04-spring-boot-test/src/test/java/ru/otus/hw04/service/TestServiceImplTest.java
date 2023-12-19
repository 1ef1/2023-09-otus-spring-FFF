package ru.otus.hw04.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.otus.hw04.dao.CsvQuestionDao;
import ru.otus.hw04.domain.Answer;
import ru.otus.hw04.domain.Question;
import ru.otus.hw04.domain.Student;
import ru.otus.hw04.domain.TestResult;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
public class TestServiceImplTest {

    @MockBean
    private CsvQuestionDao csvQuestionDao;

    @MockBean
    private LocalizedIOService ioService;

    @Autowired
    private TestServiceImpl testService;

    @Test
    void executeTestFor() {

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
        when(ioService.readIntForRangeWithPromptLocalized(1, 3, "TestService.answer.the.answer",
                "TestService.answer.the.error")).thenReturn(2);
        when(csvQuestionDao.findAll()).thenReturn(questionList);
        TestResult testResult = testService.executeTestFor(new Student("firstNameStudent", "lastNameStudent"));
        Assertions.assertEquals(1, testResult.getRightAnswersCount());
    }
}