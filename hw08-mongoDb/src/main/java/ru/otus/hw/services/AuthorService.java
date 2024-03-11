 package ru.otus.hw.services;

import ru.otus.hw.models.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();

    Author insert(String fullName);

    Author update(String id,String fullName);
}
