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
    public String findById(String id) {
        return commentService.findById(id).stream()
                .map(CommentConverter::commentToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }

    @ShellMethod(value = "Find comments by Bookid ", key = "fcbkid")
    public List<Comment> findAllByBookId(String bookId) {
        return new ArrayList<>(commentService.findByBookId(bookId));
    }

    @ShellMethod(value = "Insert comments ", key = "cins")
    Comment insert(String commentText, String bookid) {
        return commentService.insert(commentText, bookid);
    }

    @ShellMethod(value = "Update comments ", key = "cupd")
    Comment update(String id, String commentText, String bookid) {
        return commentService.update(id, commentText, bookid);
    }

    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void updateBook(String id) {
        commentService.deleteById(id);
    }
}
