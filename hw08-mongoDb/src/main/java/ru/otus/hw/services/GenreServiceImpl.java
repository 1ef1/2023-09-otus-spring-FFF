package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    private final MongoTemplate mongoTemplate;

    @Override
    public List<Genre> findAll() {
        return genreRepository.findAll();
    }

    @Override
    public Genre insert(String name) {
        var genre = new Genre(null, name);
        return genreRepository.save(genre);
    }

    @Override
    public Genre update(String id, String name) {
        return genreRepository.update(id, name);
    }
}
