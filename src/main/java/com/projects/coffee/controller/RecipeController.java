package com.projects.coffee.controller;

import com.projects.coffee.dto.RecipeCreationDTO;
import com.projects.coffee.dto.RecipeDisplayDTO;
import com.projects.coffee.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping
    public ResponseEntity<List<RecipeDisplayDTO>> getAllPublicRecipes() {
        try {
            List<RecipeDisplayDTO> recipes = recipeService.getAllPublicRecipes();
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDisplayDTO> getRecipeById(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            RecipeDisplayDTO recipe = recipeService.getRecipeById(id, username);
            if (recipe != null) {
                return ResponseEntity.ok(recipe);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<RecipeDisplayDTO> createRecipe(@Valid @RequestBody RecipeCreationDTO recipeCreationDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Use the new security-aware method
            RecipeDisplayDTO recipe = recipeService.createRecipeSecure(recipeCreationDTO, authentication);
            return ResponseEntity.status(HttpStatus.CREATED).body(recipe);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDisplayDTO> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeCreationDTO recipeCreationDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Use the new security-aware method
            RecipeDisplayDTO recipe = recipeService.updateRecipeSecure(id, recipeCreationDTO, authentication);
            if (recipe != null) {
                return ResponseEntity.ok(recipe);
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
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Use the new security-aware method
            boolean deleted = recipeService.deleteRecipeSecure(id, authentication);
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

    @GetMapping("/my")
    public ResponseEntity<List<RecipeDisplayDTO>> getMyRecipes() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            List<RecipeDisplayDTO> recipes = recipeService.getRecipesByUser(username);
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<RecipeDisplayDTO>> getPublicRecipes() {
        try {
            List<RecipeDisplayDTO> recipes = recipeService.getAllPublicRecipes();
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<RecipeDisplayDTO> toggleRecipeVisibility(@PathVariable Long id) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            RecipeDisplayDTO recipe = recipeService.toggleRecipeVisibility(id, username);
            if (recipe != null) {
                return ResponseEntity.ok(recipe);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<RecipeDisplayDTO>> searchRecipes(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String beanFlavor,
            @RequestParam(required = false) String beanOrigin,
            @RequestParam(required = false) String brewMethod,
            @RequestParam(required = false) Boolean publicOnly) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            List<RecipeDisplayDTO> recipes = recipeService.searchRecipes(title, beanFlavor, beanOrigin, brewMethod, publicOnly, username);
            return ResponseEntity.ok(recipes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/{id}/clone")
    public ResponseEntity<RecipeDisplayDTO> cloneRecipe(@PathVariable Long id, @RequestParam(required = false) String newTitle) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            RecipeDisplayDTO clonedRecipe = recipeService.cloneRecipeSecure(id, newTitle, authentication);
            if (clonedRecipe != null) {
                return ResponseEntity.status(HttpStatus.CREATED).body(clonedRecipe);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
