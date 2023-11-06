package ru.otus.hw02.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw02.dao.QuestionDao;
import ru.otus.hw02.domain.Answer;
import ru.otus.hw02.domain.Student;
import ru.otus.hw02.domain.TestResult;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question : questions) {
            var isAnswerValid = false; // Задать вопрос, получить ответ
            ioService.printLine(question.text());
            int answerValidNum = 0;
            int countAnswer = 0;
            for (Answer answer : question.answers()) {
                ioService.printLine(++countAnswer + ": " + answer.text());
                if (answer.isCorrect()) {
                    answerValidNum = countAnswer;
                }
            }
            String yourAnswer = ioService.readStringWithPrompt("your answer");
            if (Integer.parseInt(yourAnswer) == answerValidNum) {
                isAnswerValid = true;
            }
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }
}
