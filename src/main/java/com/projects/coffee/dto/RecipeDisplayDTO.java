package com.projects.coffee.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecipeDisplayDTO {
    private Long id;
    private String title;
    private String notes;
    private LocalDateTime createTimestamp;
    private LocalDateTime lastUpdateTimestamp;

    private BeanDisplayDTO bean;
    private InstructionDisplayDTO instruction;
    private String createdBy;

    @JsonProperty("isPublic")
    private boolean publicRecipe;

    public RecipeDisplayDTO(Long id, String title, String notes, LocalDateTime createTimestamp,
                            LocalDateTime lastUpdateTimestamp, BeanDisplayDTO bean,
                            InstructionDisplayDTO instruction, String createdBy, boolean publicRecipe) {
        this.id = id;
        this.title = title;
        this.notes = notes;
        this.createTimestamp = createTimestamp;
        this.lastUpdateTimestamp = lastUpdateTimestamp;
        this.bean = bean;
        this.instruction = instruction;
        this.createdBy = createdBy;
        this.publicRecipe = publicRecipe;
    }

    public RecipeDisplayDTO() {}

    @JsonProperty("isPublic")
    public boolean isPublic() {
        return this.publicRecipe;
    }

    @JsonProperty("isPublic")
    public void setPublic(boolean publicRecipe) {
        this.publicRecipe = publicRecipe;
    }
}
