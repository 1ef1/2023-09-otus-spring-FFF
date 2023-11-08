package ru.otus.hw03.config;

//import lombok.Getter;
import lombok.Getter;
import lombok.Setter;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import ru.otus.hw03.dao.CsvQuestionDao;
import ru.otus.hw03.service.ResultServiceImpl;
import ru.otus.hw03.service.StreamsIOService;
import ru.otus.hw03.service.StudentServiceImpl;
import ru.otus.hw03.service.TestServiceImpl;
import ru.otus.hw03.service.TestRunnerServiceImpl;
import ru.otus.hw03.service.LocalizedMessagesService;
import ru.otus.hw03.service.LocalizedIOService;
import ru.otus.hw03.service.LocalizedMessagesServiceImpl;
import ru.otus.hw03.service.LocalizedIOServiceImpl;

import java.util.Locale;
import java.util.Map;

@Setter
@Configuration
@ComponentScan(basePackages = "ru.otus.hw03")
@ConfigurationProperties(prefix = "test")
@Data
// Использовать @ConfigurationProperties.
// Сейчас класс соответствует файлу настроек. Чтобы они сюда отобразились нужно только правильно разместить аннотации
public class AppConfig implements TestConfig, TestFileNameProvider, LocaleConfig {

    private int rightAnswersCountToPass;

    private String testFileName;

    @Getter
    private Locale locale;

    private Map<String, String> fileNameByLocaleTag;

    public void setLocale(String locale) {
        this.locale = Locale.forLanguageTag(locale);
    }



    @Bean
    public CsvQuestionDao csvQuestionDao() {
        return new CsvQuestionDao(appConfig());
    }

//    @Bean
//    public StreamsIOService ioService() {
//        return new StreamsIOService(System.out, System.in);
//    }
    @Bean
    public StreamsIOService ioService() {
        return new StreamsIOService(System.out, System.in);
    }

    @Bean
    public LocalizedMessagesService localizedMessagesService() {
        return new LocalizedMessagesServiceImpl(() -> new Locale("en-US"));
    }

    @Bean
    public LocalizedIOService localizedIOService() {
        return new LocalizedIOServiceImpl(localizedMessagesService(), ioService());
    }

    @Bean
    public TestServiceImpl testServiceImpl() {
        return new TestServiceImpl(localizedIOService(), csvQuestionDao());
    }

    @Bean
    public StudentServiceImpl studentService() {
        return new StudentServiceImpl(localizedIOService());
    }

    @Bean
    public ResultServiceImpl resultService() {
        return new ResultServiceImpl(appConfig(), localizedIOService());
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
        config.setLocale(String.valueOf(locale));
        return config;
    }
}
