package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
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
public class PlayerService {

    private final Cache<String, Object> cache;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private static final String DOESNT_EXIST = "Team does not exist with ID = ";
    private static final String PLAYER_CACHE_PREFIX = "player_";
    private static final String TEAM_CACHE_PREFIX = "team_";

    public void create(Player player) {
        if (player == null) {
            throw new ResourceNotFoundException("Arena is null");
        }
        playerRepository.save(player);
        PlayerDto playerDto = ConvertDtoClasses.convertToPlayerDto(player);
        cache.put(PLAYER_CACHE_PREFIX + player.getId(), playerDto);
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
        Object cachedPlayer = cache.get(PLAYER_CACHE_PREFIX + id);
        if (cachedPlayer != null) {
            return (PlayerDto) cachedPlayer;
        } else {
            PlayerDto playerDto = ConvertDtoClasses.convertToPlayerDto(playerRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + id)));
            cache.put(PLAYER_CACHE_PREFIX + id, playerDto);
            return playerDto;
        }
    }


    public boolean update(Player player, final Integer id) {
        if (playerRepository.existsById(id)) {
            player.setId(id);
            playerRepository.save(player);
            cache.put(PLAYER_CACHE_PREFIX + id, player);
            return true;
        }
        return false;
    }


    public boolean delete(final Integer id) {
        if (playerRepository.existsById(id)) {
            Player player = playerRepository.findById(id).orElseThrow(()
                    -> new ResourceNotFoundException(DOESNT_EXIST + id));
            Team team = player.getTeam();
            team.getPlayers().remove(player);
            teamRepository.save(team);
            cache.put(TEAM_CACHE_PREFIX + team.getId().toString(), team);
            playerRepository.deleteById(id);
            cache.remove(PLAYER_CACHE_PREFIX + player.getId());
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
