package com.projects.coffee.dto;

import jakarta.validation.Valid;
import lombok.Data;

@Data
public class RecipeCreationDTO {
    private Long beanId;
    private Long instructionId;

    private String title;
    private String notes;

    @Valid
    private BeanDTO newBean;

    @Valid
    private InstructionDTO newInstruction;
}
