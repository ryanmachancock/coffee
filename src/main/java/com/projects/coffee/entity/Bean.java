package com.projects.coffee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Table(name = "bean")
@Entity
public class Bean {
    @Id
    @GeneratedValue
    private Long id;

    private String flavor;
    private String origin;
    private String roast;
    private Boolean isPublic;
    private String createdBy;

}
