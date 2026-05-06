package com.projects.coffee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Table(name = "recipe_share")
@Entity
public class RecipeShare {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "shared_by_user_id")
    private Person sharedBy;

    @ManyToOne
    @JoinColumn(name = "shared_with_user_id")
    private Person sharedWith;

    private LocalDateTime sharedTimestamp;
    private String note;

    @PrePersist
    protected void onCreate() {
        this.sharedTimestamp = LocalDateTime.now();
    }
}
