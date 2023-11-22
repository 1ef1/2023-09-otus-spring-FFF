package ru.otus.hw02.service;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.otus.hw02.dao.QuestionDao;
import ru.otus.hw02.domain.Answer;
import ru.otus.hw02.domain.Question;
import ru.otus.hw02.domain.Student;
import ru.otus.hw02.domain.TestResult;

@Service
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
            var isAnswerValid = asksAQuestionAndGetsAnAnswer(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean asksAQuestionAndGetsAnAnswer(@NotNull Question question) {
        ioService.printLine(question.text());
        int answerValidNum = 0;
        int countAnswer = 0;
        for (Answer answer : question.answers()) {
            ioService.printLine(++countAnswer + ": " + answer.text());
            if (answer.isCorrect()) {
                answerValidNum = countAnswer;
            }
        }
        int sudentAnswerNum =
                ioService.readIntForRangeWithPrompt(1, countAnswer,"your answer","error input number question");
        return answerValidNum == sudentAnswerNum;
    }
}
