package ru.otus.hw03.config;

//import lombok.Getter;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
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
    //    @Value("${rightAnswersCountToPass}")
    private int rightAnswersCountToPass;

    //    @Value("${testFileName}")
    private String testFileName;

    @Getter
    private Locale locale;

    private Map<String, String> fileNameByLocaleTag;

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages"); // имя базового имени ресурсного пакета без расширения файла
        messageSource.setDefaultEncoding("ISO-8859-1"); // кодировка файлов ресурсов

        return messageSource;
    }

    public void setLocale(String locale) {
        this.locale = Locale.forLanguageTag(locale);
    }


    public CsvQuestionDao csvQuestionDao() {
//        String testFileName111;
//        if (locale.getDisplayLanguage().equals("en-US")) {
//            testFileName111 = "questions.csv";
//        } else {
//            testFileName111 = "questions_ru.csv";
//        }
//        this.testFileName = testFileName111;
        TestFileNameProvider fileNameProvider = () -> fileNameByLocaleTag.get("questions.csv");
        return new CsvQuestionDao(fileNameProvider);
    }

    public StreamsIOService ioService() {
        return new StreamsIOService(System.out, System.in);
    }

    @Bean
    public LocalizedMessagesService localizedMessagesService() {
        return new LocalizedMessagesServiceImpl(() -> new Locale("en-US"), messageSource());
    }

    public LocalizedIOService localizedIOService() {
        return new LocalizedIOServiceImpl(localizedMessagesService(), ioService());
    }


    public TestServiceImpl testServiceImpl() {
        return new TestServiceImpl(localizedIOService(), csvQuestionDao());
    }

    public StudentServiceImpl studentService() {
        return new StudentServiceImpl(localizedIOService());
    }

    public ResultServiceImpl resultService() {
        TestConfig testConfig = () -> rightAnswersCountToPass;
        return new ResultServiceImpl(testConfig, localizedIOService());
    }

    public TestRunnerServiceImpl testRunnerServiceImpl() {
        return new TestRunnerServiceImpl(testServiceImpl(), studentService(), resultService());
    }
}
