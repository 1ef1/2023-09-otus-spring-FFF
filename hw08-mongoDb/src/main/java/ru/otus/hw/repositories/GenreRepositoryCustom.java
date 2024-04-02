package ru.otus.hw.repositories;

import ru.otus.hw.models.Genre;

public interface GenreRepositoryCustom {
    Genre update(String id, String name);
}
