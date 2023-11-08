package ru.otus.hw03.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw03.domain.Answer;
import ru.otus.hw03.dao.QuestionDao;
import ru.otus.hw03.domain.Student;
import ru.otus.hw03.domain.TestResult;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
//        ioService.printLine("");
//        ioService.printLineLocalized("TestService.answer.the.questions");
//        ioService.printLine("");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        for (var question: questions) {
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
