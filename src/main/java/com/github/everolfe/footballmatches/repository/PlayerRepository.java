package com.github.everolfe.footballmatches.repository;

import com.github.everolfe.footballmatches.model.Player;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlayerRepository extends JpaRepository<Player, Integer> {
    List<Player> findByAge(Integer age);
}
