package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Player;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface PlayerRepository extends JpaRepository<Player, Integer> {

    //native query
    //@Query(value = "SELECT * FROM players WHERE age = :age" , nativeQuery = true)
    //JPQL
    @Query("SELECT p FROM Player p WHERE p.age = :age")
    List<Player> findByAge(@Param("age") Integer age);

}
