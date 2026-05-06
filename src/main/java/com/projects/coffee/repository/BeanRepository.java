package com.projects.coffee.repository;

import com.projects.coffee.entity.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeanRepository extends JpaRepository<Bean, Long> {
    List<Bean> findByFlavorContainingIgnoreCase(String flavor);

    List<Bean> findByOriginContainingIgnoreCase(String origin);

    List<Bean> findByRoastContainingIgnoreCase(String roast);

    List<Bean> findByCreatedBy(String createdBy);

    List<Bean> findByIsPublicTrue();

    @Query("SELECT b FROM Bean b WHERE " +
            "(:flavor IS NULL OR LOWER(b.flavor) LIKE LOWER(CONCAT('%', :flavor, '%'))) AND " +
            "(:origin IS NULL OR LOWER(b.origin) LIKE LOWER(CONCAT('%', :origin, '%'))) AND " +
            "(:roast IS NULL OR LOWER(b.roast) LIKE LOWER(CONCAT('%', :roast, '%')))")
    List<Bean> findByFlavorAndOriginAndRoast(@Param("flavor") String flavor,
                                             @Param("origin") String origin,
                                             @Param("roast") String roast);
}
