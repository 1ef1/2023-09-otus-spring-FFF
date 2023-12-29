package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    @ShellMethod(value = "Find by id comment", key = "fc")
    public String findById(long id) {
        return commentService.findById(id).stream()
                .map(CommentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comments by Bookid ", key = "fcbkid")
    public List<Comment> findAllByBookId(long bookId) {
        return new ArrayList<>(commentService.findAllByBookId(bookId));
    }

    @ShellMethod(value = "Insert comment", key = "cins")
    public String insertBook(long id, long bookId, String commentText) {
        var savedBook = commentService.insert(id, bookId, commentText);
        return CommentConverter.commentToString(savedBook);
    }

    @ShellMethod(value = "Update comment", key = "cupd")
    public String updateBook(long id, long bookId, String commentText) {
        var savedBook = commentService.update(id, bookId, commentText);
        return CommentConverter.commentToString(savedBook);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void updateBook(long id) {
        commentService.deleteById(id);
    }
}
