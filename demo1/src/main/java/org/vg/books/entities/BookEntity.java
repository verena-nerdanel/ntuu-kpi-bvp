package org.vg.books.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOK")
public class BookEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "YEAR_RELEASED")
    private Integer yearReleased;

    @JoinColumn(name = "GENRE_ID")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private GenreEntity genre;

    @JoinColumn(name = "AUTHOR")
    @ManyToOne(cascade = CascadeType.PERSIST)
    private AuthorEntity author;
}
