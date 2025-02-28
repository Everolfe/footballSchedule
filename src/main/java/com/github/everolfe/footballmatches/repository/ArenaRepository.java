package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Arena;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ArenaRepository extends JpaRepository<Arena, Integer> {
    List<Arena> findByCapacityBetween(Integer minCapacity, Integer maxCapacity);

    List<Arena> findByCapacityLessThanEqual(Integer maxCapacity);

    List<Arena> findByCapacityGreaterThanEqual(Integer minCapacity);
}
