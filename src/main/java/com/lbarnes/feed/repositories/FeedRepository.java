package com.lbarnes.feed.repositories;

import java.util.List;

import io.micronaut.data.jdbc.annotation.JdbcRepository;
import com.lbarnes.feed.domain.FeedDAO;
import io.micronaut.data.model.query.builder.sql.Dialect;
import io.micronaut.data.repository.CrudRepository;


@JdbcRepository(dialect = Dialect.POSTGRES)
public interface FeedRepository extends CrudRepository<FeedDAO, Long> {
    List<FeedDAO> findAllByUserId(String userId);
}