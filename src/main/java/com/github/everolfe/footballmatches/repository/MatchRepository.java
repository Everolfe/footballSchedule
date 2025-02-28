package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Match;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    List<Match> findByTournamentNameIgnoreCase(String tournamentName);
}
