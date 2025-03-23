package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Arena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ArenaRepository extends JpaRepository<Arena, Integer> {

    //@Query(value = "SELECT * FROM arenas
    // WHERE capacity BETWEEN :minValue AND :maxValue ORDER BY capacity ASC", nativeQuery = true)
    //@Query(value = "SELECT * FROM arenas
    // WHERE capacity <= :maxValue ORDER BY capacity DESC", nativeQuery = true)
    //@Query(value = "SELECT * FROM arenas
    // WHERE capacity >= :minValue ORDER BY capacity ASC", nativeQuery = true)
    @Query("SELECT a FROM Arena a WHERE a.capacity BETWEEN"
            + " :minValue AND :maxValue ORDER BY a.capacity ASC")
    List<Arena> findByCapacityBetween(
            @Param("minValue") Integer minCapacity, @Param("maxValue") Integer maxCapacity);

    @Query("Select a FROM Arena a WHERE"
            + " a.capacity <= :maxValue ORDER BY a.capacity DESC")
    List<Arena> findByCapacityLessThanEqual(@Param("maxValue") Integer maxCapacity);

    @Query("Select a FROM Arena a WHERE a.capacity >= :minValue ORDER BY a.capacity ASC")
    List<Arena> findByCapacityGreaterThanEqual(@Param("minValue") Integer minCapacity);
}
