package com.projects.coffee.repository;

import com.projects.coffee.entity.RecipeShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeShareRepository extends JpaRepository<RecipeShare, Long> {

    @Query("SELECT rs FROM RecipeShare rs " +
            "JOIN FETCH rs.recipe r " +
            "JOIN FETCH r.bean " +
            "JOIN FETCH r.instruction " +
            "JOIN FETCH rs.sharedBy " +
            "WHERE rs.sharedWith.username = :username")
    List<RecipeShare> findRecipesSharedWithUser(@Param("username") String username);

    @Query("SELECT rs FROM RecipeShare rs " +
            "WHERE rs.recipe.id = :recipeId AND rs.sharedWith.username = :username")
    Optional<RecipeShare> findByRecipeIdAndSharedWithUsername(@Param("recipeId") Long recipeId,
                                                              @Param("username") String username);

    @Query("SELECT rs FROM RecipeShare rs " +
            "WHERE rs.recipe.id = :recipeId AND rs.sharedBy.username = :sharedByUsername AND rs.sharedWith.username = :sharedWithUsername")
    Optional<RecipeShare> findExistingShare(@Param("recipeId") Long recipeId,
                                            @Param("sharedByUsername") String sharedByUsername,
                                            @Param("sharedWithUsername") String sharedWithUsername);

    @Query("SELECT rs FROM RecipeShare rs " +
            "JOIN FETCH rs.recipe r " +
            "JOIN FETCH r.bean " +
            "JOIN FETCH r.instruction " +
            "JOIN FETCH rs.sharedWith " +
            "JOIN FETCH r.user " +
            "WHERE rs.sharedBy.username = :username")
    List<RecipeShare> findRecipesSharedByUser(@Param("username") String username);
}
