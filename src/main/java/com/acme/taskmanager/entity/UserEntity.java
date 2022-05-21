package com.acme.taskmanager.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Class to represent a user entity.
 */
@Table("user")
public class UserEntity {

    @Id
    private final Long id;

    @Column("username")
    private final String username;

    @Column("first_name")
    private final String firstName;

    @Column("last_name")
    private final String lastName;

    public UserEntity(Long id, String username, String firstName, String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
