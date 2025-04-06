package org.vg.books.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.vg.books.entities.BookEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AverageStat {
    private BookEntity book;
    private long averageOrdersPerYear;
    private double averageExceedingOrdersPerYear;
}
