package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.services.AuthorService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class AuthorCommands {

    private final AuthorService authorService;

    private final AuthorConverter authorConverter;

    @ShellMethod(value = "Find all authors", key = "aa")
    public String findAllAuthors() {
        return authorService.findAll().stream()
                .map(authorConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Authors insert", key = "ai")
    public String insertAuthor(String fullName) {
        var savedBook = authorService.insert(fullName);
        return authorConverter.authorToString(savedBook);
    }

    @ShellMethod(value = "Authors update", key = "au")
    public String updateAuthor(String id, String fullName) {
        var savedBook = authorService.update(id, fullName);
        return authorConverter.authorToString(savedBook);
    }
}
