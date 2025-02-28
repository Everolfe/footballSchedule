package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.model.Player;
import java.util.List;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class PlayerServiceImpl implements ServiceInterface<Player> {
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @Override
    public void create(Player player) {
        playerRepository.save(player);
    }

    @Override
    public List<Player> readAll() {
        return playerRepository.findAll();
    }

    @Override
    public Player read(final Integer id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    @Override
    public boolean update(Player player, final Integer id) {
        if (playerRepository.existsById(id)) {
            player.setId(id);
            playerRepository.save(player);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final Integer id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Player> getPlayersByAge(final Integer age) {
        return playerRepository.findByAge(age);
    }

}
