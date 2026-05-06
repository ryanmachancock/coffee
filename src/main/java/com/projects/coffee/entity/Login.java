package com.projects.coffee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "login")
@Entity
public class Login {

    @Id
    @GeneratedValue
    private Long id;

    private String username;
    private String password;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;
}
