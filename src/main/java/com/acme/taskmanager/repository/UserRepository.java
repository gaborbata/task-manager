package com.acme.taskmanager.repository;

import com.acme.taskmanager.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;

/**
 * CRUD repository for users.
 */
@Repository
public class UserRepository extends CriteriaBasedRepository<UserEntity, Long> {

    @Autowired
    public UserRepository(R2dbcEntityTemplate r2dbcEntityTemplate) {
        super(r2dbcEntityTemplate);
    }

    @Override
    public Class<UserEntity> getEntityClass() {
        return UserEntity.class;
    }
}
