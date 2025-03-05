package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Match;
import java.util.List;

import com.github.everolfe.footballmatches.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    @Query("SELECT m FROM Match m WHERE LOWER(m.tournamentName) = LOWER(:tournamentName)")
    List<Match> findByTournamentNameIgnoreCase(@Param("tournamentName") String tournamentName);

    @Query("SELECT m FROM Match m JOIN m.teamList t WHERE t = :team")
    List<Match> findByTeam(@Param("team") Team team);
}
