package ru.otus.hw03;

//import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.otus.hw03.config.AppConfig;
import ru.otus.hw03.service.TestRunnerService;

public class Application {
    public static void main(String[] args) {

        //Создать контекст Spring Boot приложения
//        ApplicationContext context =  new ApplicationContext(AppConfig.class);
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        var testRunnerService = context.getBean(TestRunnerService.class);
        testRunnerService.run();

    }
}