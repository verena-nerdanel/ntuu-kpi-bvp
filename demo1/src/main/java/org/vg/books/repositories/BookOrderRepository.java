package org.vg.books.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.vg.books.dto.Counter;
import org.vg.books.entities.AuthorEntity;
import org.vg.books.entities.BookEntity;
import org.vg.books.entities.BookOrderEntity;

import java.util.Date;
import java.util.List;

@Repository
public interface BookOrderRepository extends CrudRepository<BookOrderEntity, Long> {

    @Query("SELECT new org.vg.books.dto.Counter(o.book, COUNT(1)) FROM BookOrderEntity o GROUP BY o.book ORDER BY count(1) DESC LIMIT 5")
    List<Counter<BookEntity>> getTopBooks();

    @Query("SELECT new org.vg.books.dto.Counter(o.book.author, COUNT(1)) FROM BookOrderEntity o GROUP BY o.book.author ORDER BY count(1) DESC LIMIT 5")
    List<Counter<AuthorEntity>> getTopAuthors();

    @Query("SELECT new org.vg.books.dto.Counter(o.book.genre.name, COUNT(1)) FROM BookOrderEntity o GROUP BY o.book.genre.name ORDER BY count(1) DESC LIMIT 5")
    List<Counter<String>> getTopGenres();

    void deleteAllByBookId(long id);

    long countByBookIdAndDateAfter(long id, Date after);

    // that's just insane
//    @Query("WITH ref AS (SELECT o.book book, COUNT(1) c FROM BookOrderEntity o WHERE o.date > DATEADD(YEAR, -1, CURRENT_DATE) GROUP BY o.book) " +
//            "SELECT new org.vg.books.dto.AverageStat(ref.book, ref.c, 0.0) FROM ref")
//    List<AverageStat> getAverageStats();

    // average orders count for the given genre:
    // SELECT AVERAGE(COUNT(1)) FROM BookOrderEntity o1 WHERE o1.book.genre = :g AND o1.date > DATEADD(YEAR, -1, CURRENT_DATE) GROUP BY o1.book
}
