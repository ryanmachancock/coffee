package com.projects.coffee.service;

import com.projects.coffee.dto.BeanDisplayDTO;
import com.projects.coffee.dto.InstructionDisplayDTO;
import com.projects.coffee.dto.RecipeDisplayDTO;
import com.projects.coffee.dto.RecipeShareDTO;
import com.projects.coffee.entity.Person;
import com.projects.coffee.entity.Recipe;
import com.projects.coffee.entity.RecipeShare;
import com.projects.coffee.repository.LoginRepository;
import com.projects.coffee.repository.RecipeRepository;
import com.projects.coffee.repository.RecipeShareRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeShareService {

    private final RecipeShareRepository recipeShareRepository;
    private final RecipeRepository recipeRepository;
    private final LoginRepository loginRepository;
    private final RecipeService recipeService;

    public RecipeShareService(RecipeShareRepository recipeShareRepository,
                              RecipeRepository recipeRepository,
                              LoginRepository loginRepository,
                              RecipeService recipeService) {
        this.recipeShareRepository = recipeShareRepository;
        this.recipeRepository = recipeRepository;
        this.loginRepository = loginRepository;
        this.recipeService = recipeService;
    }

    public RecipeShareDTO shareRecipe(Long recipeId, String sharedWithUsername, String note, String sharedByUsername) {
        Recipe recipe = recipeRepository.findByIdAndAccessibleToUser(recipeId, sharedByUsername);
        if (recipe == null) {
            throw new IllegalArgumentException("Recipe not found or you don't have permission to share it");
        }

        Person sharedWithUser = loginRepository.findByUsername(sharedWithUsername).getPerson();
        if (sharedWithUser == null) {
            throw new IllegalArgumentException("User '" + sharedWithUsername + "' not found");
        }

        Optional<RecipeShare> existingShare = recipeShareRepository.findExistingShare(
                recipeId, sharedByUsername, sharedWithUsername);
        if (existingShare.isPresent()) {
            throw new IllegalArgumentException("Recipe is already shared with this user");
        }

        if (sharedByUsername.equals(sharedWithUsername)) {
            throw new IllegalArgumentException("You cannot share a recipe with yourself");
        }

        RecipeShare recipeShare = new RecipeShare();
        recipeShare.setRecipe(recipe);
        recipeShare.setSharedBy(recipe.getUser());
        recipeShare.setSharedWith(sharedWithUser);
        recipeShare.setNote(note);

        RecipeShare savedShare = recipeShareRepository.save(recipeShare);
        return convertToDTO(savedShare);
    }

    public List<RecipeShareDTO> getRecipesSharedWithUser(String username) {
        List<RecipeShare> shares = recipeShareRepository.findRecipesSharedWithUser(username);
        return shares.stream()
                .map(this::convertToDTOWithRecipe)
                .collect(Collectors.toList());
    }

    public boolean removeShare(Long shareId, String username) {
        Optional<RecipeShare> shareOpt = recipeShareRepository.findById(shareId);
        if (shareOpt.isPresent()) {
            RecipeShare share = shareOpt.get();
            if (share.getSharedBy().getUsername().equals(username) ||
                    share.getSharedWith().getUsername().equals(username)) {
                recipeShareRepository.delete(share);
                return true;
            }
        }
        return false;
    }

    public RecipeDisplayDTO getSharedRecipeDetails(Long shareId, String username) {
        Optional<RecipeShare> shareOpt = recipeShareRepository.findById(shareId);
        if (shareOpt.isPresent()) {
            RecipeShare share = shareOpt.get();
            if (share.getSharedWith().getUsername().equals(username)) {
                return recipeService.getRecipeById(share.getRecipe().getId(), username);
            }
        }
        return null;
    }

    private RecipeShareDTO convertToDTO(RecipeShare share) {
        return new RecipeShareDTO(
                share.getId(),
                share.getRecipe().getId(),
                share.getRecipe().getTitle(),
                share.getSharedBy().getUsername(),
                share.getSharedWith().getUsername(),
                share.getSharedTimestamp(),
                share.getNote()
        );
    }

    private RecipeShareDTO convertToDTOWithRecipe(RecipeShare share) {
        RecipeShareDTO dto = convertToDTO(share);

        try {
            Recipe recipe = share.getRecipe();
            if (recipe != null) {
                RecipeDisplayDTO recipeDisplay = createRecipeDisplayDTODirectly(recipe);
                if (recipeDisplay != null) {
                    dto.setRecipe(recipeDisplay);
                }
            }
        } catch (Exception e) {
            // Log error appropriately in production
        }

        return dto;
    }

    private RecipeDisplayDTO createRecipeDisplayDTODirectly(Recipe recipe) {
        try {
            BeanDisplayDTO beanDisplayDTO = null;
            if (recipe.getBean() != null) {
                beanDisplayDTO = new BeanDisplayDTO(
                        recipe.getBean().getId(),
                        recipe.getBean().getFlavor(),
                        recipe.getBean().getOrigin(),
                        recipe.getBean().getRoast(),
                        recipe.getBean().getCreatedBy(),
                        recipe.getBean().getIsPublic()
                );
            }

            InstructionDisplayDTO instructionDisplayDTO = null;
            if (recipe.getInstruction() != null) {
                instructionDisplayDTO = new InstructionDisplayDTO(
                        recipe.getInstruction().getId(),
                        recipe.getInstruction().getInstructionSteps(),
                        recipe.getInstruction().getWaterTemp(),
                        recipe.getInstruction().getGramsOfWater(),
                        recipe.getInstruction().getGramsOfCoffee(),
                        recipe.getInstruction().getGrindSize(),
                        recipe.getInstruction().getBrewMethod()
                );
            }

            return new RecipeDisplayDTO(
                    recipe.getId(),
                    recipe.getTitle(),
                    recipe.getNotes(),
                    recipe.getCreateTimestamp(),
                    recipe.getLastUpdateTimestamp(),
                    beanDisplayDTO,
                    instructionDisplayDTO,
                    recipe.getUser().getUsername(),
                    recipe.getIsPublic()
            );
        } catch (Exception e) {
            return null;
        }
    }

    public RecipeShareDTO shareRecipeSecure(Long recipeId, String sharedWithUsername, String note, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User must be authenticated to share recipes");
        }

        String sharedByUsername = authentication.getName();
        return shareRecipe(recipeId, sharedWithUsername, note, sharedByUsername);
    }

    public List<RecipeShareDTO> getRecipesSharedWithUserSecure(Authentication authentication) {
        validateAuthentication(authentication);
        return getRecipesSharedWithUser(authentication.getName());
    }

    public List<RecipeShareDTO> getRecipesSharedByUserSecure(Authentication authentication) {
        validateAuthentication(authentication);
        String username = authentication.getName();

        List<RecipeShare> shares = recipeShareRepository.findRecipesSharedByUser(username);
        return shares.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public boolean removeShareSecure(Long shareId, Authentication authentication) {
        validateAuthentication(authentication);

        Optional<RecipeShare> shareOpt = recipeShareRepository.findById(shareId);
        if (shareOpt.isPresent()) {
            RecipeShare share = shareOpt.get();
            String username = authentication.getName();

            boolean canRemove = share.getSharedBy().getUsername().equals(username) ||
                    share.getSharedWith().getUsername().equals(username);

            if (canRemove) {
                recipeShareRepository.delete(share);
                return true;
            } else {
                throw new SecurityException("User does not have permission to remove this share");
            }
        }
        return false;
    }

    public RecipeDisplayDTO getSharedRecipeDetailsSecure(Long shareId, Authentication authentication) {
        validateAuthentication(authentication);

        Optional<RecipeShare> shareOpt = recipeShareRepository.findById(shareId);
        if (shareOpt.isPresent()) {
            RecipeShare share = shareOpt.get();
            String username = authentication.getName();

            if (share.getSharedWith().getUsername().equals(username)) {
                return recipeService.getRecipeById(share.getRecipe().getId(), username);
            } else {
                throw new SecurityException("User does not have permission to view this shared recipe");
            }
        }
        return null;
    }

    public RecipeShareDTO shareRecipeWithTokenInfo(Long recipeId, String sharedWithUsername,
                                                   String note, Authentication authentication,
                                                   Map<String, Object> tokenClaims) {
        validateAuthentication(authentication);

        String enhancedNote = note;
        if (tokenClaims != null && !tokenClaims.isEmpty()) {
            enhancedNote += " [Shared via API - Auth: " + tokenClaims.get("authType") + "]";
        }

        return shareRecipeSecure(recipeId, sharedWithUsername, enhancedNote, authentication);
    }

    public boolean canUserAccessRecipe(Long recipeId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        String username = authentication.getName();

        Recipe recipe = recipeRepository.findByIdAndAccessibleToUserIncludingShared(recipeId, username);
        return recipe != null;
    }

    public Map<String, List<RecipeShareDTO>> getAllUserShares(Authentication authentication) {
        validateAuthentication(authentication);

        return Map.of(
                "received", getRecipesSharedWithUserSecure(authentication),
                "sent", getRecipesSharedByUserSecure(authentication)
        );
    }

    private void validateAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User must be authenticated");
        }

        if (authentication.getName() == null || authentication.getName().trim().isEmpty()) {
            throw new SecurityException("Invalid authentication - no username found");
        }
    }
}
