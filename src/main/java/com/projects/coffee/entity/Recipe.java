package com.projects.coffee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "recipe")
@Entity
public class Recipe {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Person user;

    @ManyToOne
    @JoinColumn(name = "bean_id")
    private Bean bean;

    @ManyToOne
    @JoinColumn(name = "instruction_id")
    private Instruction instruction;

    private String title;
    private String notes;
    private Boolean isPublic = true;
    private LocalDateTime createTimestamp;
    private LocalDateTime lastUpdateTimestamp;

    @PrePersist
    protected void onCreate() {
        this.createTimestamp = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdateTimestamp = LocalDateTime.now();
    }
}
