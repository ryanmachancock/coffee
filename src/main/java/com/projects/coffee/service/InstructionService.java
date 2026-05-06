package com.projects.coffee.service;

import com.projects.coffee.dto.InstructionDTO;
import com.projects.coffee.dto.InstructionDisplayDTO;
import com.projects.coffee.entity.Instruction;
import com.projects.coffee.repository.InstructionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InstructionService {

    private final InstructionRepository instructionRepository;

    public InstructionService(InstructionRepository instructionRepository) {
        this.instructionRepository = instructionRepository;
    }

    public List<InstructionDisplayDTO> getAllInstructions() {
        List<Instruction> instructions = instructionRepository.findAll();
        return instructions.stream()
                .map(instruction -> new InstructionDisplayDTO(
                        instruction.getId(),
                        instruction.getInstructionSteps(),
                        instruction.getWaterTemp(),
                        instruction.getGramsOfWater(),
                        instruction.getGramsOfCoffee(),
                        instruction.getGrindSize(),
                        instruction.getBrewMethod()
                ))
                .collect(Collectors.toList());
    }

    public InstructionDisplayDTO getInstructionById(Long id) {
        Optional<Instruction> instructionOpt = instructionRepository.findById(id);
        if (instructionOpt.isPresent()) {
            Instruction instruction = instructionOpt.get();
            return new InstructionDisplayDTO(
                    instruction.getId(),
                    instruction.getInstructionSteps(),
                    instruction.getWaterTemp(),
                    instruction.getGramsOfWater(),
                    instruction.getGramsOfCoffee(),
                    instruction.getGrindSize(),
                    instruction.getBrewMethod()
            );
        }
        return null;
    }

    public InstructionDisplayDTO createInstruction(InstructionDTO instructionDTO, String createdBy) {
        Instruction instruction = new Instruction();
        instruction.setInstructionSteps(instructionDTO.getInstructionSteps());
        instruction.setWaterTemp(instructionDTO.getWaterTemp());
        instruction.setGramsOfWater(instructionDTO.getGramsOfWater());
        instruction.setGramsOfCoffee(instructionDTO.getGramsOfCoffee());
        instruction.setGrindSize(instructionDTO.getGrindSize());
        instruction.setBrewMethod(instructionDTO.getBrewMethod());
        instruction.setIsPublic(true); // Default to public
        instruction.setCreatedBy(createdBy);

        Instruction savedInstruction = instructionRepository.save(instruction);

        return new InstructionDisplayDTO(
                savedInstruction.getId(),
                savedInstruction.getInstructionSteps(),
                savedInstruction.getWaterTemp(),
                savedInstruction.getGramsOfWater(),
                savedInstruction.getGramsOfCoffee(),
                savedInstruction.getGrindSize(),
                savedInstruction.getBrewMethod()
        );
    }

    public InstructionDisplayDTO updateInstruction(Long id, InstructionDTO instructionDTO, String username) {
        Optional<Instruction> instructionOpt = instructionRepository.findById(id);
        if (instructionOpt.isPresent()) {
            Instruction instruction = instructionOpt.get();

            // Check if user has permission to update this instruction
            if (!instruction.getCreatedBy().equals(username)) {
                throw new SecurityException("You can only update instructions you created");
            }

            instruction.setInstructionSteps(instructionDTO.getInstructionSteps());
            instruction.setWaterTemp(instructionDTO.getWaterTemp());
            instruction.setGramsOfWater(instructionDTO.getGramsOfWater());
            instruction.setGramsOfCoffee(instructionDTO.getGramsOfCoffee());
            instruction.setGrindSize(instructionDTO.getGrindSize());
            instruction.setBrewMethod(instructionDTO.getBrewMethod());

            Instruction savedInstruction = instructionRepository.save(instruction);

            return new InstructionDisplayDTO(
                    savedInstruction.getId(),
                    savedInstruction.getInstructionSteps(),
                    savedInstruction.getWaterTemp(),
                    savedInstruction.getGramsOfWater(),
                    savedInstruction.getGramsOfCoffee(),
                    savedInstruction.getGrindSize(),
                    savedInstruction.getBrewMethod()
            );
        }
        return null;
    }

    public boolean deleteInstruction(Long id, String username) {
        Optional<Instruction> instructionOpt = instructionRepository.findById(id);
        if (instructionOpt.isPresent()) {
            Instruction instruction = instructionOpt.get();

            if (!instruction.getCreatedBy().equals(username)) {
                throw new SecurityException("You can only delete instructions you created");
            }

            instructionRepository.delete(instruction);
            return true;
        }
        return false;
    }

    public List<InstructionDisplayDTO> searchInstructions(String brewMethod, String grindSize, Integer minWaterTemp, Integer maxWaterTemp) {
        List<Instruction> instructions = instructionRepository.findByBrewMethodAndGrindSizeAndWaterTempRange(
                brewMethod, grindSize, minWaterTemp, maxWaterTemp);

        return instructions.stream()
                .map(instruction -> new InstructionDisplayDTO(
                        instruction.getId(),
                        instruction.getInstructionSteps(),
                        instruction.getWaterTemp(),
                        instruction.getGramsOfWater(),
                        instruction.getGramsOfCoffee(),
                        instruction.getGrindSize(),
                        instruction.getBrewMethod()
                ))
                .collect(Collectors.toList());
    }

    public List<InstructionDisplayDTO> getInstructionsByCreator(String username) {
        List<Instruction> instructions = instructionRepository.findByCreatedBy(username);
        return instructions.stream()
                .map(instruction -> new InstructionDisplayDTO(
                        instruction.getId(),
                        instruction.getInstructionSteps(),
                        instruction.getWaterTemp(),
                        instruction.getGramsOfWater(),
                        instruction.getGramsOfCoffee(),
                        instruction.getGrindSize(),
                        instruction.getBrewMethod()
                ))
                .collect(Collectors.toList());
    }

    public List<String> getDistinctBrewMethods() {
        return instructionRepository.findDistinctBrewMethods();
    }

    public List<String> getDistinctGrindSizes() {
        return instructionRepository.findDistinctGrindSizes();
    }
}
