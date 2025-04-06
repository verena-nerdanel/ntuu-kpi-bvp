package org.vg.books.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.vg.books.entities.AuthorEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends CrudRepository<AuthorEntity, Long> {

    @Query("SELECT a FROM AuthorEntity a ORDER BY name")
    List<AuthorEntity> findAllOrdered();

    Optional<AuthorEntity> findByName(String name);
}
