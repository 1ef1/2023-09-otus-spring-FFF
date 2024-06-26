package ru.otus.hw.services;

import ru.otus.hw.models.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAll();

    Genre insert(String name);

    Genre update(String id, String name);
}
