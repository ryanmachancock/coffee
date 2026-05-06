package com.projects.coffee.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecipeShareDTO {
    private Long id;
    private Long recipeId;
    private String recipeName;
    private String sharedBy;
    private String sharedWith;
    private LocalDateTime sharedTimestamp;
    private String note;

    private RecipeDisplayDTO recipe;

    public RecipeShareDTO() {}

    public RecipeShareDTO(Long id, Long recipeId, String recipeName, String sharedBy,
                          String sharedWith, LocalDateTime sharedTimestamp, String note) {
        this.id = id;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
        this.sharedBy = sharedBy;
        this.sharedWith = sharedWith;
        this.sharedTimestamp = sharedTimestamp;
        this.note = note;
    }
}
