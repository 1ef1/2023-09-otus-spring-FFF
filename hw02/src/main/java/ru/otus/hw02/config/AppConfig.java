package ru.otus.hw02.config;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class AppConfig implements TestConfig, TestFileNameProvider {
    @Value("${rightAnswersCountToPass}")
    private int rightAnswersCountToPass;

    @Value("${testFileName}")
    private String testFileName;
}
