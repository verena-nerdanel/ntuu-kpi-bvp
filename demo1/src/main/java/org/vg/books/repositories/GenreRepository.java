package org.vg.books.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.vg.books.entities.GenreEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface GenreRepository extends CrudRepository<GenreEntity, Long> {

    @Query("SELECT g FROM GenreEntity g ORDER BY name")
    List<GenreEntity> findAllOrdered();

    Optional<GenreEntity> findByName(String name);
}
