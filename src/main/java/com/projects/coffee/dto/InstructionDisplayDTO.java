package com.projects.coffee.dto;

import lombok.Data;

@Data
public class InstructionDisplayDTO {
    private Long id;
    private String instructionSteps;
    private Integer waterTemp;
    private Integer gramsOfWater;
    private Integer gramsOfCoffee;
    private String grindSize;
    private String brewMethod;

    public InstructionDisplayDTO(Long id, String instructionSteps, Integer waterTemp,
                                 Integer gramsOfWater, Integer gramsOfCoffee, String grindSize, String brewMethod) {
        this.id = id;
        this.instructionSteps = instructionSteps;
        this.waterTemp = waterTemp;
        this.gramsOfWater = gramsOfWater;
        this.gramsOfCoffee = gramsOfCoffee;
        this.grindSize = grindSize;
        this.brewMethod = brewMethod;
    }

    public InstructionDisplayDTO() {}
}
