package ru.otus.hw03;

import org.springframework.context.ApplicationContext;
import ru.otus.hw03.service.TestRunnerService;

public class Application {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
        ApplicationContext context = null;
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();

    }
}