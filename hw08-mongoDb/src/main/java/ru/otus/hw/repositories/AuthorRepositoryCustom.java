package ru.otus.hw.repositories;

import ru.otus.hw.models.Author;

public interface AuthorRepositoryCustom {
    Author update(String id, String fullName);
}