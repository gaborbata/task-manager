package com.acme.taskmanager.repository;

import com.acme.taskmanager.exception.EntityNotFoundException;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Update.from;

/**
 * CRUD repository for entities.
 *
 * Provides an alternative to {@link org.springframework.data.repository.reactive.ReactiveCrudRepository},
 * using {@link R2dbcEntityTemplate} which provides more control on queries and criteria.
 *
 * @param <T> the type of entity
 * @param <ID> the id type
 */
public abstract class CriteriaBasedRepository<T, ID> {
    protected final R2dbcEntityTemplate r2dbcEntityTemplate;
    protected final String identifier;

    protected CriteriaBasedRepository(R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.identifier = r2dbcEntityTemplate.getDataAccessStrategy().getIdentifierColumns(getEntityClass()).stream()
                .findFirst()
                .map(SqlIdentifier::getReference)
                .orElseThrow(() -> new IllegalArgumentException("identifier must be defined"));
    }

    public abstract Class<T> getEntityClass();

    public Mono<T> save(T entity) {
        return r2dbcEntityTemplate.insert(entity);
    }

    public Mono<Boolean> existsById(ID id) {
        return r2dbcEntityTemplate.exists(query(where(identifier).is(id)), getEntityClass());
    }

    public Mono<T> findById(ID id) {
        return r2dbcEntityTemplate.selectOne(query(where(identifier).is(id)), getEntityClass());
    }

    public Flux<T> findAll() {
        return r2dbcEntityTemplate.select(getEntityClass()).all();
    }

    public Mono<Integer> updateNonNull(ID id, T entity) {
        var outboundRow = r2dbcEntityTemplate.getDataAccessStrategy().getOutboundRow(entity);
        Map<SqlIdentifier, Object> assignments = outboundRow.entrySet().stream()
                .filter(entry -> entry.getValue().hasValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return r2dbcEntityTemplate.update(getEntityClass())
                .matching(query(where(identifier).is(id)))
                .apply(from(assignments));
    }

    public Mono<Void> deleteById(ID id) {
        return r2dbcEntityTemplate.delete(query(where(identifier).is(id)), getEntityClass())
                .flatMap(count -> count > 0 ? Mono.empty() : Mono.error(new EntityNotFoundException("entity not found")));
    }
}
