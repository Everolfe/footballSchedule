package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    List<Team> findByCountryIgnoreCase(String country);
}
