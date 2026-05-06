package com.projects.coffee.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Table(name = "instruction")
@Entity
public class Instruction {
    @Id
    @GeneratedValue
    private Long id;

    private String instructionSteps;
    private Integer waterTemp;
    private Integer gramsOfWater;
    private Integer gramsOfCoffee;
    private String grindSize;
    private String brewMethod;
    private Boolean isPublic;
    private String createdBy;

}
