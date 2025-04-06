package org.vg.books.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOK_ORDER")
public class BookOrderEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "BOOK_ID")
    @ManyToOne
    private BookEntity book;

    @Column(name = "DATE")
    private Date date;

    @Column(name = "PRICE")
    private Double price;
}
