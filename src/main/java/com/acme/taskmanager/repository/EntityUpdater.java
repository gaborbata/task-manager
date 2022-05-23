package com.acme.taskmanager.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Updater to support selective property (e.g. non-null) updates.
 * @param <T> the type of entity
 * @param <ID> the id type
 */
@Component
public class EntityUpdater<T, ID> {

    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    public EntityUpdater(R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    /**
     * Update entity based on non-null properties.
     *
     * @param id the entity id
     * @param entity the entity
     * @return the number of affected rows by the update
     */
    public Mono<Integer> updateNonNull(ID id, T entity) {
        var outboundRow = r2dbcEntityTemplate.getDataAccessStrategy().getOutboundRow(entity);
        Map<SqlIdentifier, Object> assignments = outboundRow.entrySet().stream()
                .filter(entry -> entry.getValue().hasValue())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return r2dbcEntityTemplate.update(entity.getClass())
                .matching(Query.query(Criteria.where("id").is(id)))
                .apply(Update.from(assignments));
    }
}
