package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Team;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {

    //native query
    //@Query(
    // value = "SELECT * FROM teams WHERE LOWER(country) = LOWER(:country)", nativeQuery = true)
    //JPQL
    @Query("SELECT t FROM Team t WHERE LOWER(t.country) = LOWER(:country)")
    List<Team> findByCountryIgnoreCase(@Param("country") String country);

}
