package org.vg.books.controllers;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.vg.books.dto.BookDto;
import org.vg.books.entities.AuthorEntity;
import org.vg.books.entities.BookEntity;
import org.vg.books.entities.GenreEntity;
import org.vg.books.repositories.AuthorRepository;
import org.vg.books.repositories.BookOrderRepository;
import org.vg.books.repositories.BookRepository;
import org.vg.books.repositories.GenreRepository;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(value = "/books")
@RequiredArgsConstructor
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookOrderRepository bookOrderRepository;

    //    @PostConstruct
//    @Transactional
    public void initialize() {

        bookRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        GenreEntity genreNonFiction = genreRepository.save(GenreEntity.builder()
                .name("Non-fiction")
                .build());

        bookRepository.save(BookEntity.builder()
                .title("Refactoring. Improving the Design of Existing Code")
                .author(AuthorEntity.builder()
                        .name("Martin Fowler")
                        .build())
                .genre(genreNonFiction)
                .yearReleased(2018)
                .build());

        bookRepository.save(BookEntity.builder()
                .title("Smalltalk Best Practice Patterns")
                .author(AuthorEntity.builder()
                        .name("Kent Beck")
                        .build())
                .genre(genreNonFiction)
                .yearReleased(1997)
                .build());
    }

    @GetMapping(path = {""})
    public String list(Model model, @RequestParam(value = "genre", required = false) String genre) {
        model.addAttribute("books", getBooks(genre));
        return "books";
    }

    private List<BookDto> getBooks(String genre) {
        return ((List<BookEntity>) bookRepository.findAll()).stream()
                .map(BookController::convertToDto)
                .filter(b -> genre == null || b.getGenre().equalsIgnoreCase(genre))
                .toList();
    }

    @GetMapping("/new")
    public String createDialog(Model model) {
        model.addAttribute("book", new BookDto());
        model.addAttribute("authors", authorRepository.findAllOrdered());
        model.addAttribute("genres", genreRepository.findAllOrdered());
        return "book-edit";
    }

    @PostMapping(path = "/new", consumes = "application/x-www-form-urlencoded")
    public String create(BookDto params) {
        cleanup(params);

        // create
        final BookEntity book = BookEntity.builder()
                .title(params.getTitle())
                .yearReleased(params.getYearReleased())
                .author(authorRepository.findByName(params.getAuthor()).orElse(AuthorEntity.builder()
                        .name(params.getAuthor())
                        .build()))
                .genre(genreRepository.findByName(params.getGenre()).orElse(GenreEntity.builder()
                        .name(params.getGenre())
                        .build()))
                .build();

        bookRepository.save(book);
        return "redirect:/books";
    }

    @GetMapping("/edit/{id}")
    public String editDialog(@PathVariable("id") long id, Model model) {
        model.addAttribute("book", bookRepository.findById(id).map(BookController::convertToDto).get());
        model.addAttribute("authors", authorRepository.findAllOrdered());
        model.addAttribute("genres", genreRepository.findAllOrdered());
        return "book-edit";
    }

    @PostMapping(path = "/edit/{id}", consumes = "application/x-www-form-urlencoded")
    public String edit(@PathVariable("id") long id, BookDto params) {
        cleanup(params);

        // update
        final BookEntity book = bookRepository.findById(id).get();

        book.setTitle(params.getTitle());
        book.setYearReleased(params.getYearReleased());
        book.setAuthor(authorRepository.findByName(params.getAuthor()).orElse(AuthorEntity.builder()
                .name(params.getAuthor())
                .build()));
        book.setGenre(genreRepository.findByName(params.getGenre()).orElse(GenreEntity.builder()
                .name(params.getGenre())
                .build()));

        bookRepository.save(book);
        return "redirect:/books";
    }

    @GetMapping("/delete/{id}")
    public String deleteDialog(@PathVariable("id") long id, Model model) {
        model.addAttribute("book", bookRepository.findById(id).map(BookController::convertToDto).get());
        return "book-delete";
    }

    @PostMapping("/delete/{id}")
    @Transactional
    public String delete(@PathVariable("id") long id) {
        // FIXME: cascade removal properly
        bookOrderRepository.deleteAllByBookId(id);
        bookRepository.deleteById(id);
        return "redirect:/books";
    }

    public static void cleanup(BookDto params) {
        params.setTitle(params.getTitle().trim());
        params.setAuthor(params.getAuthor().trim());
        params.setGenre(params.getGenre().trim());
    }

    public static BookDto convertToDto(BookEntity book) {
        return BookDto.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor().getName())
                .genre(book.getGenre().getName())
                .yearReleased(book.getYearReleased())
                .build();
    }
}
