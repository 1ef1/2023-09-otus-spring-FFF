package ru.otus.hw02;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.hw02.config.AppConfig;
import ru.otus.hw02.service.TestRunnerService;

//@ComponentScan(basePackages = "ru.otus.hw02")
public class Application {
    public static void main(String[] args) {

        //Создать контекст на основе Annotation/Java конфигурирования
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
    }
}