package com.projects.coffee.repository;

import com.projects.coffee.entity.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
    List<Instruction> findByBrewMethodContainingIgnoreCase(String brewMethod);

    List<Instruction> findByGrindSizeContainingIgnoreCase(String grindSize);

    List<Instruction> findByCreatedBy(String createdBy);

    List<Instruction> findByIsPublicTrue();

    List<Instruction> findByWaterTempBetween(Integer minTemp, Integer maxTemp);

    @Query("SELECT DISTINCT i.brewMethod FROM Instruction i WHERE i.brewMethod IS NOT NULL ORDER BY i.brewMethod")
    List<String> findDistinctBrewMethods();

    @Query("SELECT DISTINCT i.grindSize FROM Instruction i WHERE i.grindSize IS NOT NULL ORDER BY i.grindSize")
    List<String> findDistinctGrindSizes();

    @Query("SELECT i FROM Instruction i WHERE " +
            "(:brewMethod IS NULL OR LOWER(i.brewMethod) LIKE LOWER(CONCAT('%', :brewMethod, '%'))) AND " +
            "(:grindSize IS NULL OR LOWER(i.grindSize) LIKE LOWER(CONCAT('%', :grindSize, '%'))) AND " +
            "(:minWaterTemp IS NULL OR i.waterTemp >= :minWaterTemp) AND " +
            "(:maxWaterTemp IS NULL OR i.waterTemp <= :maxWaterTemp)")
    List<Instruction> findByBrewMethodAndGrindSizeAndWaterTempRange(@Param("brewMethod") String brewMethod,
                                                                    @Param("grindSize") String grindSize,
                                                                    @Param("minWaterTemp") Integer minWaterTemp,
                                                                    @Param("maxWaterTemp") Integer maxWaterTemp);
}
