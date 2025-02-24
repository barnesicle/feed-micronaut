package com.motivationadivisor.feed.repositories;

import java.util.List;
import java.util.UUID;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import com.motivationadivisor.feed.domain.FeedDAO;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;
import io.micronaut.data.repository.PageableRepository;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedRepository extends CrudRepository<FeedDAO, Long> {
    List<FeedDAO> findAllByUserId(String userId);
}