package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.NotExistMessage;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class PlayerService {

    private final Cache<String, Object> cache;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @AspectAnnotation
    public void create(Player player) {
        if (player == null) {
            throw new BadRequestException("Player is null");
        }
        playerRepository.save(player);
        PlayerDto playerDto = ConvertDtoClasses.convertToPlayerDto(player);
        cache.put(CacheConstants.getPlayerCacheKey(player.getId()), playerDto);
    }

    @AspectAnnotation
    public List<PlayerDtoWithTeam> readAll() {
        List<Player> players = playerRepository.findAll();
        List<PlayerDtoWithTeam> playerDtoWithTeams = new ArrayList<>();
        if (!players.isEmpty()) {
            for (Player player : players) {
                playerDtoWithTeams.add(ConvertDtoClasses.convertToPlayerDtoWithTeam(player));
            }
        }
        return playerDtoWithTeams;
    }

    @AspectAnnotation
    public PlayerDto read(final Integer id) {
        Object cachedPlayer = cache.get(CacheConstants.getPlayerCacheKey(id));
        if (cachedPlayer != null) {
            return (PlayerDto) cachedPlayer;
        } else {
            PlayerDto playerDto = ConvertDtoClasses.convertToPlayerDto(playerRepository.findById(id)
                            .orElseThrow(() -> new ResourcesNotFoundException(
                                    NotExistMessage.getPlayerNotExistMessage(id))));
            cache.put(CacheConstants.getPlayerCacheKey(id), playerDto);
            return playerDto;
        }
    }

    @AspectAnnotation
    public boolean update(Player player, final Integer id) {
        return playerRepository.findById(id)
                .map(existingPlayer -> {
                    player.setId(id);
                    playerRepository.save(player);
                    cache.put(CacheConstants.getPlayerCacheKey(id), player);
                    return true;
                })
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getPlayerNotExistMessage(id)));
    }

    @AspectAnnotation
    public boolean delete(final Integer id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getPlayerNotExistMessage(id)));
        Team team = player.getTeam();
        team.getPlayers().remove(player);
        teamRepository.save(team);
        cache.put(CacheConstants.getTeamCacheKey(team.getId()), team);
        playerRepository.deleteById(id);
        cache.remove(CacheConstants.getPlayerCacheKey(player.getId()));
        return true;
    }

    @AspectAnnotation
    public List<PlayerDto> getPlayersByAge(final Integer age) {
        List<PlayerDto> playerDto = new ArrayList<>();
        for (Player player : playerRepository.findByAge(age)) {
            playerDto.add(ConvertDtoClasses.convertToPlayerDto(player));
        }
        return playerDto;
    }

}
