package com.projects.coffee.controller;

import com.projects.coffee.dto.RecipeDisplayDTO;
import com.projects.coffee.dto.RecipeShareDTO;
import com.projects.coffee.service.RecipeShareService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes/shares")
public class RecipeShareController {

    private final RecipeShareService recipeShareService;

    public RecipeShareController(RecipeShareService recipeShareService) {
        this.recipeShareService = recipeShareService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> shareRecipe(@Valid @RequestBody ShareRecipeRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUsername = authentication.getName();

            System.out.println("=== Sharing recipe - Current user: " + currentUsername + " ===");
            System.out.println("=== Authentication type: " + authentication.getClass().getSimpleName() + " ===");
            System.out.println("=== Authorities: " + authentication.getAuthorities() + " ===");

            RecipeShareDTO share = recipeShareService.shareRecipeSecure(
                    request.getRecipeId(),
                    request.getSharedWithUsername(),
                    request.getNote(),
                    authentication
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(share);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to share recipe"));
        }
    }

    @GetMapping("/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RecipeShareDTO>> getRecipesSharedWithMe() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<RecipeShareDTO> shares = recipeShareService.getRecipesSharedWithUserSecure(authentication);
            return ResponseEntity.ok(shares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/sent")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<RecipeShareDTO>> getRecipesSharedByMe() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            List<RecipeShareDTO> shares = recipeShareService.getRecipesSharedByUserSecure(authentication);
            return ResponseEntity.ok(shares);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/{shareId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> removeShare(@PathVariable Long shareId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            boolean removed = recipeShareService.removeShareSecure(shareId, authentication);

            if (removed) {
                return ResponseEntity.ok(Map.of("message", "Share removed successfully"));
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to remove share"));
        }
    }

    @GetMapping("/{shareId}/recipe")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<RecipeDisplayDTO> getSharedRecipeDetails(@PathVariable Long shareId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            RecipeDisplayDTO recipe = recipeShareService.getSharedRecipeDetailsSecure(shareId, authentication);

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

    @PostMapping("/token-based")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> shareRecipeWithToken(@Valid @RequestBody TokenBasedShareRequest request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Map<String, Object> tokenClaims = extractTokenClaims(authentication);

            RecipeShareDTO share = recipeShareService.shareRecipeWithTokenInfo(
                    request.getRecipeId(),
                    request.getSharedWithUsername(),
                    request.getNote(),
                    authentication,
                    tokenClaims
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(share);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to share recipe with token"));
        }
    }

    private Map<String, Object> extractTokenClaims(Authentication authentication) {
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities().toString(),
                "authType", authentication.getClass().getSimpleName()
        );
    }

    public static class ShareRecipeRequest {
        private Long recipeId;
        private String sharedWithUsername;
        private String note;

        public Long getRecipeId() {return recipeId;}

        public void setRecipeId(Long recipeId) {this.recipeId = recipeId;}

        public String getSharedWithUsername() {return sharedWithUsername;}

        public void setSharedWithUsername(String sharedWithUsername) {this.sharedWithUsername = sharedWithUsername;}

        public String getNote() {return note;}

        public void setNote(String note) {this.note = note;}
    }

    public static class TokenBasedShareRequest extends ShareRecipeRequest {
        private Map<String, Object> additionalClaims;

        public Map<String, Object> getAdditionalClaims() {return additionalClaims;}

        public void setAdditionalClaims(Map<String, Object> additionalClaims) {this.additionalClaims = additionalClaims;}
    }
}
