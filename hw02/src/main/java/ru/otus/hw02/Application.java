package ru.otus.hw02;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import ru.otus.hw02.service.TestRunnerService;

@ComponentScan(basePackages = "ru.otus.hw02")
@PropertySource("classpath:application.properties")
public class Application {
    public static void main(String[] args) {

        //Создать контекст на основе Annotation/Java конфигурирования
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();
    }
}