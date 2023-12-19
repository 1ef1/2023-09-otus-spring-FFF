package ru.otus.hw04.service;

import ru.otus.hw04.domain.Student;
import ru.otus.hw04.domain.TestResult;

public interface TestService {
    TestResult executeTestFor(Student student);
}
