package ru.otus.hw.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final BookRepository repository;

    @GetMapping("/")
    public String listPage(Model model) {
        List<Book> books = repository.findAll();
        model.addAttribute("books", books);
        return "booksList.html";
    }

    @GetMapping("/edit")
    public String editPage(@RequestParam("id") long id, Model model) {
        Book book = repository.findById(id).orElseThrow(null);
        model.addAttribute("book", book);
        return "edit";
    }

    @PostMapping("/edit")
    public String savePerson(Book book) {
        repository.save(book);
        return "redirect:/";
    }
}
