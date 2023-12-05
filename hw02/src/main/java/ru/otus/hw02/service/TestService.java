package ru.otus.hw02.service;

import ru.otus.hw02.domain.Student;
import ru.otus.hw02.domain.TestResult;

public interface TestService {
    TestResult executeTestFor(Student student);
}
