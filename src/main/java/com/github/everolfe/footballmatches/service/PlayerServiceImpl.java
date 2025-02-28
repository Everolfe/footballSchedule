package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class PlayerServiceImpl {
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;


    public void create(Player player) {
        playerRepository.save(player);
    }

    public List<PlayerDtoWithTeam> readAll() {
        List<Player> players = playerRepository.findAll();
        List<PlayerDtoWithTeam> playerDtoWithTeams = new ArrayList<>();
        if (players != null) {
            for (Player player : players) {
                playerDtoWithTeams.add(ConvertDtoClasses.convertToPlayerDtoWithTeam(player));
            }
        }
        return playerDtoWithTeams;
    }


    public PlayerDto read(final Integer id) {
        PlayerDto playerDto = ConvertDtoClasses
                .convertToPlayerDto(playerRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Doesn't exist" + id)));
        return playerDto;
    }


    public boolean update(Player player, final Integer id) {
        if (playerRepository.existsById(id)) {
            player.setId(id);
            playerRepository.save(player);
            return true;
        }
        return false;
    }


    public boolean delete(final Integer id) {
        if (playerRepository.existsById(id)) {
            playerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<PlayerDto> getPlayersByAge(final Integer age) {
        List<PlayerDto> playerDto = new ArrayList<>();
        for (Player player : playerRepository.findByAge(age)) {
            playerDto.add(ConvertDtoClasses.convertToPlayerDto(player));
        }
        return playerDto;
    }

}
