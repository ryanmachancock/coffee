package com.projects.coffee.repository;

import com.projects.coffee.entity.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("SELECT r FROM Recipe r " +
            "JOIN FETCH r.bean " +
            "JOIN FETCH r.instruction " +
            "JOIN FETCH r.user " +
            "WHERE r.user.username = :username")
    List<Recipe> findByUserUsername(@Param("username") String username);

    @Query("SELECT r FROM Recipe r " +
            "JOIN FETCH r.bean " +
            "JOIN FETCH r.instruction " +
            "JOIN FETCH r.user " +
            "WHERE r.isPublic = true")
    List<Recipe> findByIsPublicTrue();

    List<Recipe> findByTitleContainingIgnoreCase(String title);

    @Query("SELECT r FROM Recipe r " +
            "JOIN FETCH r.bean " +
            "JOIN FETCH r.instruction " +
            "JOIN FETCH r.user " +
            "WHERE " +
            "(:title IS NULL OR LOWER(r.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:beanFlavor IS NULL OR LOWER(r.bean.flavor) LIKE LOWER(CONCAT('%', :beanFlavor, '%'))) AND " +
            "(:beanOrigin IS NULL OR LOWER(r.bean.origin) LIKE LOWER(CONCAT('%', :beanOrigin, '%'))) AND " +
            "(:brewMethod IS NULL OR LOWER(r.instruction.brewMethod) LIKE LOWER(CONCAT('%', :brewMethod, '%'))) AND " +
            "(:publicOnly IS NULL OR :publicOnly = false OR r.isPublic = true)")
    List<Recipe> findBySearchCriteria(@Param("title") String title,
                                      @Param("beanFlavor") String beanFlavor,
                                      @Param("beanOrigin") String beanOrigin,
                                      @Param("brewMethod") String brewMethod,
                                      @Param("publicOnly") Boolean publicOnly);

    @Query("SELECT r FROM Recipe r " +
            "JOIN FETCH r.bean " +
            "JOIN FETCH r.instruction " +
            "JOIN FETCH r.user " +
            "WHERE r.id = :id AND (r.isPublic = true OR r.user.username = :username)")
    Recipe findByIdAndAccessibleToUser(@Param("id") Long id, @Param("username") String username);

    @Query("SELECT r FROM Recipe r " +
            "JOIN FETCH r.bean " +
            "JOIN FETCH r.instruction " +
            "JOIN FETCH r.user " +
            "WHERE r.id = :id AND (" +
            "r.isPublic = true OR " +
            "r.user.username = :username OR " +
            "EXISTS (SELECT 1 FROM RecipeShare rs WHERE rs.recipe.id = r.id AND rs.sharedWith.username = :username))")
    Recipe findByIdAndAccessibleToUserIncludingShared(@Param("id") Long id, @Param("username") String username);
}
