package com.projects.coffee.service;

import com.projects.coffee.dto.BeanDisplayDTO;
import com.projects.coffee.dto.InstructionDisplayDTO;
import com.projects.coffee.dto.RecipeCreationDTO;
import com.projects.coffee.dto.RecipeDisplayDTO;
import com.projects.coffee.entity.Bean;
import com.projects.coffee.entity.Instruction;
import com.projects.coffee.entity.Login;
import com.projects.coffee.entity.Person;
import com.projects.coffee.entity.Recipe;
import com.projects.coffee.repository.BeanRepository;
import com.projects.coffee.repository.InstructionRepository;
import com.projects.coffee.repository.LoginRepository;
import com.projects.coffee.repository.RecipeRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final BeanRepository beanRepository;
    private final InstructionRepository instructionRepository;
    private final LoginRepository loginRepository;

    public RecipeService(RecipeRepository recipeRepository,
                         BeanRepository beanRepository,
                         InstructionRepository instructionRepository,
                         LoginRepository loginRepository) {
        this.recipeRepository = recipeRepository;
        this.beanRepository = beanRepository;
        this.instructionRepository = instructionRepository;
        this.loginRepository = loginRepository;
    }

    public List<RecipeDisplayDTO> getAllPublicRecipes() {
        List<Recipe> recipes = recipeRepository.findByIsPublicTrue();
        return recipes.stream()
                .map(this::convertToDisplayDTO)
                .collect(Collectors.toList());
    }

    public RecipeDisplayDTO getRecipeById(Long id, String username) {
        Recipe recipe = recipeRepository.findByIdAndAccessibleToUser(id, username);
        if (recipe != null) {
            return convertToDisplayDTO(recipe);
        }
        return null;
    }

    public RecipeDisplayDTO createRecipe(RecipeCreationDTO recipeCreationDTO, String username) {
        // Get the user (Person) entity with null check
        Login login = loginRepository.findByUsername(username);
        if (login == null) {
            throw new IllegalArgumentException("Login not found for username: " + username);
        }

        Person user = login.getPerson();
        if (user == null) {
            throw new IllegalArgumentException("Person not found for username: " + username);
        }

        Bean bean = null;
        if (recipeCreationDTO.getNewBean() != null) {
            bean = new Bean();
            bean.setFlavor(recipeCreationDTO.getNewBean().getFlavor());
            bean.setOrigin(recipeCreationDTO.getNewBean().getOrigin());
            bean.setRoast(recipeCreationDTO.getNewBean().getRoast());
            bean.setIsPublic(recipeCreationDTO.getNewBean().getIsPublic());
            bean.setCreatedBy(username);
            bean = beanRepository.save(bean);
        } else if (recipeCreationDTO.getBeanId() != null) {
            Optional<Bean> beanOpt = beanRepository.findById(recipeCreationDTO.getBeanId());
            if (beanOpt.isPresent()) {
                bean = beanOpt.get();
            } else {
                throw new IllegalArgumentException("Bean not found with ID: " + recipeCreationDTO.getBeanId());
            }
        } else {
            throw new IllegalArgumentException("Either beanId or newBean must be provided");
        }

        Instruction instruction = null;
        if (recipeCreationDTO.getNewInstruction() != null) {
            instruction = new Instruction();
            instruction.setInstructionSteps(recipeCreationDTO.getNewInstruction().getInstructionSteps());
            instruction.setWaterTemp(recipeCreationDTO.getNewInstruction().getWaterTemp());
            instruction.setGramsOfWater(recipeCreationDTO.getNewInstruction().getGramsOfWater());
            instruction.setGramsOfCoffee(recipeCreationDTO.getNewInstruction().getGramsOfCoffee());
            instruction.setGrindSize(recipeCreationDTO.getNewInstruction().getGrindSize());
            instruction.setBrewMethod(recipeCreationDTO.getNewInstruction().getBrewMethod());
            instruction.setIsPublic(true);
            instruction.setCreatedBy(username);
            instruction = instructionRepository.save(instruction);
        } else if (recipeCreationDTO.getInstructionId() != null) {
            Optional<Instruction> instructionOpt = instructionRepository.findById(recipeCreationDTO.getInstructionId());
            if (instructionOpt.isPresent()) {
                instruction = instructionOpt.get();
            } else {
                throw new IllegalArgumentException("Instruction not found with ID: " + recipeCreationDTO.getInstructionId());
            }
        } else {
            throw new IllegalArgumentException("Either instructionId or newInstruction must be provided");
        }

        Recipe recipe = new Recipe();
        recipe.setUser(user);
        recipe.setBean(bean);
        recipe.setInstruction(instruction);
        recipe.setTitle(recipeCreationDTO.getTitle());
        recipe.setNotes(recipeCreationDTO.getNotes());
        recipe.setIsPublic(true);

        Recipe savedRecipe = recipeRepository.save(recipe);
        return convertToDisplayDTO(savedRecipe);
    }

    public RecipeDisplayDTO updateRecipe(Long id, RecipeCreationDTO recipeCreationDTO, String username) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isPresent()) {
            Recipe recipe = recipeOpt.get();

            if (!recipe.getUser().getUsername().equals(username)) {
                throw new SecurityException("You can only update recipes you created");
            }

            recipe.setTitle(recipeCreationDTO.getTitle());
            recipe.setNotes(recipeCreationDTO.getNotes());

            if (recipeCreationDTO.getBeanId() != null) {
                Optional<Bean> beanOpt = beanRepository.findById(recipeCreationDTO.getBeanId());
                if (beanOpt.isPresent()) {
                    recipe.setBean(beanOpt.get());
                }
            }

            if (recipeCreationDTO.getInstructionId() != null) {
                Optional<Instruction> instructionOpt = instructionRepository.findById(recipeCreationDTO.getInstructionId());
                if (instructionOpt.isPresent()) {
                    recipe.setInstruction(instructionOpt.get());
                }
            }

            Recipe savedRecipe = recipeRepository.save(recipe);
            return convertToDisplayDTO(savedRecipe);
        }
        return null;
    }

    public boolean deleteRecipe(Long id, String username) {
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isPresent()) {
            Recipe recipe = recipeOpt.get();

            if (!recipe.getUser().getUsername().equals(username)) {
                throw new SecurityException("You can only delete recipes you created");
            }

            recipeRepository.delete(recipe);
            return true;
        }
        return false;
    }

    public List<RecipeDisplayDTO> getRecipesByUser(String username) {
        List<Recipe> recipes = recipeRepository.findByUserUsername(username);

        List<RecipeDisplayDTO> result = recipes.stream()
                .map(this::convertToDisplayDTO)
                .collect(Collectors.toList());

        return result;
    }

    public RecipeDisplayDTO toggleRecipeVisibility(Long id, String username) {
        Recipe recipe = recipeRepository.findByIdAndAccessibleToUser(id, username);
        if (recipe != null) {
            if (!recipe.getUser().getUsername().equals(username)) {
                throw new SecurityException("You can only modify recipes you created");
            }

            recipe.setIsPublic(!recipe.getIsPublic());
            Recipe savedRecipe = recipeRepository.save(recipe);

            return convertToDisplayDTO(savedRecipe);
        }
        return null;
    }

    public List<RecipeDisplayDTO> searchRecipes(String title, String beanFlavor, String beanOrigin,
                                                String brewMethod, Boolean publicOnly, String username) {
        List<Recipe> recipes = recipeRepository.findBySearchCriteria(title, beanFlavor, beanOrigin, brewMethod, publicOnly);

        return recipes.stream()
                .filter(recipe -> recipe.getIsPublic() || recipe.getUser().getUsername().equals(username))
                .map(this::convertToDisplayDTO)
                .collect(Collectors.toList());
    }

    public RecipeDisplayDTO cloneRecipe(Long id, String username, String newTitle) {
        Recipe originalRecipe = recipeRepository.findByIdAndAccessibleToUserIncludingShared(id, username);
        if (originalRecipe != null) {
            Person user = loginRepository.findByUsername(username).getPerson();

            Recipe clonedRecipe = new Recipe();
            clonedRecipe.setUser(user);
            clonedRecipe.setBean(originalRecipe.getBean());
            clonedRecipe.setInstruction(originalRecipe.getInstruction());
            clonedRecipe.setTitle(newTitle != null ? newTitle : "Copy of " + originalRecipe.getTitle());
            clonedRecipe.setNotes("Cloned from recipe by " + originalRecipe.getUser().getUsername());
            clonedRecipe.setIsPublic(false); // Default cloned recipes to private

            Recipe savedRecipe = recipeRepository.save(clonedRecipe);
            return convertToDisplayDTO(savedRecipe);
        }
        return null;
    }

    /**
     * Clone recipe using Spring Security Authentication context
     */
    public RecipeDisplayDTO cloneRecipeSecure(Long id, String newTitle, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User must be authenticated to clone recipes");
        }

        String username = authentication.getName();

        // Log security audit
        System.out.println("=== SECURITY AUDIT: Recipe clone attempt ===");
        System.out.println("User: " + username);
        System.out.println("Recipe ID: " + id);
        System.out.println("Auth Type: " + authentication.getClass().getSimpleName());

        return cloneRecipe(id, username, newTitle);
    }

    /**
     * Get recipes by user using Authentication context
     */
    public List<RecipeDisplayDTO> getRecipesByUserSecure(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User must be authenticated");
        }

        return getRecipesByUser(authentication.getName());
    }

    /**
     * Create recipe using Authentication context
     */
    public RecipeDisplayDTO createRecipeSecure(RecipeCreationDTO recipeCreationDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User must be authenticated to create recipes");
        }

        String username = authentication.getName();

        // Log security audit
        System.out.println("=== SECURITY AUDIT: Recipe creation ===");
        System.out.println("User: " + username);
        System.out.println("Recipe Title: " + recipeCreationDTO.getTitle());
        System.out.println("Auth Type: " + authentication.getClass().getSimpleName());

        return createRecipe(recipeCreationDTO, username);
    }

    /**
     * Update recipe using Authentication context with enhanced security
     */
    public RecipeDisplayDTO updateRecipeSecure(Long id, RecipeCreationDTO recipeCreationDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User must be authenticated to update recipes");
        }

        String username = authentication.getName();

        // Additional security check - verify ownership before update
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isPresent()) {
            Recipe recipe = recipeOpt.get();
            if (!recipe.getUser().getUsername().equals(username)) {
                System.out.println("=== SECURITY VIOLATION: Update attempt by non-owner ===");
                System.out.println("User: " + username + " attempted to update recipe owned by: " + recipe.getUser().getUsername());
                throw new SecurityException("You can only update recipes you created");
            }
        }

        System.out.println("=== SECURITY AUDIT: Recipe update ===");
        System.out.println("User: " + username + " updating recipe ID: " + id);

        return updateRecipe(id, recipeCreationDTO, username);
    }

    /**
     * Delete recipe using Authentication context with enhanced security
     */
    public boolean deleteRecipeSecure(Long id, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("User must be authenticated to delete recipes");
        }

        String username = authentication.getName();

        // Log security audit before deletion
        Optional<Recipe> recipeOpt = recipeRepository.findById(id);
        if (recipeOpt.isPresent()) {
            Recipe recipe = recipeOpt.get();
            System.out.println("=== SECURITY AUDIT: Recipe deletion ===");
            System.out.println("User: " + username + " deleting recipe: " + recipe.getTitle());
        }

        return deleteRecipe(id, username);
    }

    private RecipeDisplayDTO convertToDisplayDTO(Recipe recipe) {
        BeanDisplayDTO beanDisplayDTO = new BeanDisplayDTO(
                recipe.getBean().getId(),
                recipe.getBean().getFlavor(),
                recipe.getBean().getOrigin(),
                recipe.getBean().getRoast(),
                recipe.getBean().getCreatedBy(),
                recipe.getBean().getIsPublic()
        );

        InstructionDisplayDTO instructionDisplayDTO = new InstructionDisplayDTO(
                recipe.getInstruction().getId(),
                recipe.getInstruction().getInstructionSteps(),
                recipe.getInstruction().getWaterTemp(),
                recipe.getInstruction().getGramsOfWater(),
                recipe.getInstruction().getGramsOfCoffee(),
                recipe.getInstruction().getGrindSize(),
                recipe.getInstruction().getBrewMethod()
        );

        return new RecipeDisplayDTO(
                recipe.getId(),
                recipe.getTitle(),
                recipe.getNotes(),
                recipe.getCreateTimestamp(),
                recipe.getLastUpdateTimestamp(),
                beanDisplayDTO,
                instructionDisplayDTO,
                recipe.getUser().getUsername(),
                recipe.getIsPublic() // This will now properly map to the publicRecipe field
        );
    }
}
