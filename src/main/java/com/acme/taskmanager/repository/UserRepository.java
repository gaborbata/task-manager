package com.acme.taskmanager.repository;

import com.acme.taskmanager.entity.UserEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

/**
 * CRUD repository for users.
 */
@Repository
public interface UserRepository extends ReactiveCrudRepository<UserEntity, Long> {
}
