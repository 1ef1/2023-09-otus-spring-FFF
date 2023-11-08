package ru.otus.hw02.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
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

    public CsvQuestionDao csvQuestionDao() {
        TestFileNameProvider fileNameProvider = () -> testFileName;
        return new CsvQuestionDao(fileNameProvider);
    }

    public StreamsIOService ioService() {
        return new StreamsIOService(System.out, System.in);
    }

    public TestServiceImpl testServiceImpl() {
        return new TestServiceImpl(ioService(), csvQuestionDao());
    }

    public StudentServiceImpl studentService() {
        return new StudentServiceImpl(ioService());
    }

    public ResultServiceImpl resultService() {

        TestConfig testConfig = () -> rightAnswersCountToPass;
        return new ResultServiceImpl(testConfig, ioService());
    }

    public TestRunnerServiceImpl testRunnerServiceImpl() {
        return new TestRunnerServiceImpl(testServiceImpl(), studentService(), resultService());
    }
}
