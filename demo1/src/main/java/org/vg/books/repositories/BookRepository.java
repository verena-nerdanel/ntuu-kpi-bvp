package org.vg.books.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.vg.books.entities.BookEntity;

import java.util.List;

@Repository
public interface BookRepository extends CrudRepository<BookEntity, Long> {
    @Query("SELECT b FROM BookEntity b ORDER BY b.author.name, b.title")
    List<BookEntity> findAllOrdered();
}
