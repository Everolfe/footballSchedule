package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    @Query("SELECT t FROM Team t WHERE LOWER(t.country) = LOWER(:country)")
    List<Team> findByCountryIgnoreCase(@Param("country") String country);

    @Query("SELECT t FROM Team t JOIN t.players p WHERE p = :player")
    Team findByPlayer(@Param("player") Player player);
}
