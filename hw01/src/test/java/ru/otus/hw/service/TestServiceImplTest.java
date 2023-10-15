package ru.otus.hw.service;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

public class TestServiceImplTest {

    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    private TestServiceImpl testService;

    @Test
    public void executeTest_shouldPrintQuestionsAndAnswers() {
        // Arrange

        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);

        String textQuestion = "Question1";
        Answer answer1 = new Answer("Answer1",true);
        Answer answer2 = new Answer("Answer2",false);
        Answer answer3 = new Answer("Answer3",false);
        List<Answer> answerList= new ArrayList<>();
        answerList.add(answer1);
        answerList.add(answer2);
        answerList.add(answer3);
        Question question = new Question(textQuestion,answerList);


        when(questionDao.findAll()).thenReturn(Collections.singletonList(question));

        // Act
        testService.executeTest();

        // Assert
        verify(ioService).printLine("");
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printLine("Question1");
        verify(ioService).printLine("Answer1");
        verify(ioService).printLine("Answer2");
        verify(ioService).printLine("Answer3");
    }
}