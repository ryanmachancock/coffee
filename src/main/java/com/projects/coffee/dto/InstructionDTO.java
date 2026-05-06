package com.projects.coffee.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class InstructionDTO {
    @NotBlank(message = "Instruction steps are required")
    private String instructionSteps;

    @NotNull(message = "Water temperature is required")
    @Positive(message = "Water temperature must be positive")
    private Integer waterTemp;

    @NotNull(message = "Grams of water is required")
    @Positive(message = "Grams of water must be positive")
    private Integer gramsOfWater;

    @NotNull(message = "Grams of coffee is required")
    @Positive(message = "Grams of coffee must be positive")
    private Integer gramsOfCoffee;

    @NotBlank(message = "Grind size is required")
    private String grindSize;

    @NotBlank(message = "Brew method is required")
    private String brewMethod;
}
