package org.vg.books.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vg.books.entities.AuthorEntity;
import org.vg.books.entities.BookEntity;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StatsDto {
    private List<Counter<BookEntity>> topBooks;
    private List<Counter<AuthorEntity>> topAuthors;
    private List<Counter<String>> topGenres;
    private List<AverageStat> average;
}
