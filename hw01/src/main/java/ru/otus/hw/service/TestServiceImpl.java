package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.CsvQuestionDao;
import ru.otus.hw.domain.Answer;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        // Получить вопросы из дао и вывести их с вариантами ответов
        CsvQuestionDao csvQuestionDao = new CsvQuestionDao(() -> "null");
        csvQuestionDao.findAll().forEach(e -> {
            ioService.printLine(e.text());
            e.answers().stream().map(Answer::text).forEach(ioService::printLine);
        });
    }
}
