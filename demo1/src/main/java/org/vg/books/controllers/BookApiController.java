package org.vg.books.controllers;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
@RestController
@RequestMapping(value = "/api/books", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BookApiController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookOrderRepository bookOrderRepository;

    @GetMapping(path = {""})
    public List<BookDto> list(@RequestParam(value = "genre", required = false) String genre) {
        return ((List<BookEntity>) bookRepository.findAll()).stream()
                .map(BookController::convertToDto)
                .filter(b -> genre == null || b.getGenre().equalsIgnoreCase(genre))
                .toList();
    }

    @GetMapping(path = "/{id}")
    public BookDto get(@PathVariable("id") long id) {
        return bookRepository.findById(id)
                .map(BookController::convertToDto)
                .orElse(null);
    }

    @PostMapping(path = "")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(BookDto params) {
        BookController.cleanup(params);

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
    }

    @PutMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void edit(@PathVariable("id") long id, BookDto params) {
        BookController.cleanup(params);

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
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") long id) {
        // FIXME: cascade removal properly
        bookOrderRepository.deleteAllByBookId(id);
        bookRepository.deleteById(id);
    }
}
