package ru.otus.hw02.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import ru.otus.hw02.dao.CsvQuestionDao;
import ru.otus.hw02.service.ResultServiceImpl;
import ru.otus.hw02.service.StreamsIOService;
import ru.otus.hw02.service.StudentServiceImpl;
import ru.otus.hw02.service.TestServiceImpl;
import ru.otus.hw02.service.TestRunnerServiceImpl;

@Configuration
@ComponentScan(basePackages = "ru.otus.hw02")
@PropertySource("classpath:application.properties")
@Data
public class AppConfig implements TestConfig, TestFileNameProvider {
    @Value("${rightAnswersCountToPass}")
    private int rightAnswersCountToPass;

    @Value("${testFileName}")
    private String testFileName;

    @Bean
    public CsvQuestionDao csvQuestionDao() {
        return new CsvQuestionDao(appConfig());
    }

    @Bean
    public StreamsIOService ioService() {
        return new StreamsIOService(System.out, System.in);
    }

    @Bean
    public TestServiceImpl testServiceImpl() {
        return new TestServiceImpl(ioService(), csvQuestionDao());
    }

    @Bean
    public StudentServiceImpl studentService() {
        return new StudentServiceImpl(ioService());
    }

    @Bean
    public ResultServiceImpl resultService() {
        return new ResultServiceImpl(appConfig(), ioService());
    }

    @Bean
    public TestRunnerServiceImpl testRunnerServiceImpl() {
        return new TestRunnerServiceImpl(testServiceImpl(), studentService(), resultService());
    }

    @Bean(name = "myAppConfig")
    public AppConfig appConfig() {
        AppConfig config = new AppConfig();
        config.setRightAnswersCountToPass(rightAnswersCountToPass);
        config.setTestFileName(testFileName);
        return config;
    }
}
