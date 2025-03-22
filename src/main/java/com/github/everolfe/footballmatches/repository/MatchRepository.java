package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Match;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Integer> {
    @Query("SELECT m FROM Match m WHERE LOWER(m.tournamentName) = LOWER(:tournamentName)")
    List<Match> findByTournamentNameIgnoreCase(@Param("tournamentName") String tournamentName);

    @Query("SELECT m FROM Match m WHERE m.dateTime <= :endDate")
    List<Match> findByDateTimeLessThanEqual(@Param("endDate") LocalDateTime endDate);

    @Query("SELECT m FROM Match m WHERE m.dateTime >= :startDate")
    List<Match> findByDateTimeGreaterThanEqual(@Param("startDate") LocalDateTime startDate);

    @Query("SELECT m FROM Match m WHERE m.dateTime BETWEEN :startDate AND :endDate")
    List<Match> findByDateTimeBetween(@Param("startDate") LocalDateTime startDate,
                                      @Param("endDate") LocalDateTime endDate);
}
