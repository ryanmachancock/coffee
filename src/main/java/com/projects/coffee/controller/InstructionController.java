package com.projects.coffee.controller;

import com.projects.coffee.dto.InstructionDTO;
import com.projects.coffee.dto.InstructionDisplayDTO;
import com.projects.coffee.service.InstructionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/instructions")
public class InstructionController {

    private final InstructionService instructionService;

    public InstructionController(InstructionService instructionService) {
        this.instructionService = instructionService;
    }

    @GetMapping
    public ResponseEntity<List<InstructionDisplayDTO>> getAllInstructions() {
        try {
            List<InstructionDisplayDTO> instructions = instructionService.getAllInstructions();
            return ResponseEntity.ok(instructions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<InstructionDisplayDTO> getInstructionById(@PathVariable Long id) {
        try {
            InstructionDisplayDTO instruction = instructionService.getInstructionById(id);
            if (instruction != null) {
                return ResponseEntity.ok(instruction);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<InstructionDisplayDTO> createInstruction(@Valid @RequestBody InstructionDTO instructionDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            InstructionDisplayDTO createdInstruction = instructionService.createInstruction(instructionDTO, username);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInstruction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstructionDisplayDTO> updateInstruction(@PathVariable Long id, @Valid @RequestBody InstructionDTO instructionDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            InstructionDisplayDTO updatedInstruction = instructionService.updateInstruction(id, instructionDTO, username);
            if (updatedInstruction != null) {
                return ResponseEntity.ok(updatedInstruction);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstruction(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            boolean deleted = instructionService.deleteInstruction(id, username);
            if (deleted) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<InstructionDisplayDTO>> searchInstructions(
            @RequestParam(required = false) String brewMethod,
            @RequestParam(required = false) String grindSize,
            @RequestParam(required = false) Integer minWaterTemp,
            @RequestParam(required = false) Integer maxWaterTemp) {
        try {
            List<InstructionDisplayDTO> instructions = instructionService.searchInstructions(brewMethod, grindSize, minWaterTemp, maxWaterTemp);
            return ResponseEntity.ok(instructions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<InstructionDisplayDTO>> getMyInstructions() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            List<InstructionDisplayDTO> instructions = instructionService.getInstructionsByCreator(username);
            return ResponseEntity.ok(instructions);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/brew-methods")
    public ResponseEntity<List<String>> getBrewMethods() {
        try {
            List<String> brewMethods = instructionService.getDistinctBrewMethods();
            return ResponseEntity.ok(brewMethods);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/grind-sizes")
    public ResponseEntity<List<String>> getGrindSizes() {
        try {
            List<String> grindSizes = instructionService.getDistinctGrindSizes();
            return ResponseEntity.ok(grindSizes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
