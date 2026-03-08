package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ExceptionMessages;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.exceptions.ValidationUtils;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class PlayerService {

    private static final String ID_FIELD = "id";
    private static final String AGE_FIELD = "age";
    private static final String CACHE_NAME = "players";
    private static final String CACHE_NAME_WITH_TEAM = "playersWithTeam";

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @AspectAnnotation
    @CachePut(value = CACHE_NAME, key = "#result.id")
    public PlayerDto create(Player player, final Integer teamId) {
        if (player == null) {
            throw new BadRequestException("Player is null");
        }

        if (teamId != null) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourcesNotFoundException(
                            ExceptionMessages.getTeamNotExistMessage(teamId)));
            player.setTeam(team);
        }

        ValidationUtils.validateProperName(player.getCountry());
        ValidationUtils.validateNonNegative(AGE_FIELD, player.getAge());
        playerRepository.save(player);
        return ConvertDtoClasses.convertToPlayerDto(player);
    }

    @AspectAnnotation
    @Cacheable(value = CACHE_NAME_WITH_TEAM)
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
    @Cacheable(value = CACHE_NAME, key = "#id")
    public PlayerDto read(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getPlayerNotExistMessage(id)));
        return ConvertDtoClasses.convertToPlayerDto(player);
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_WITH_TEAM, allEntries = true),
            @CacheEvict(value = CACHE_NAME, key = "#id")
    })
    public boolean update(Player player, final Integer id, final Integer teamId) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        ValidationUtils.validateProperName(player.getCountry());
        ValidationUtils.validateNonNegative(AGE_FIELD, player.getAge());

        return playerRepository.findById(id)
                .map(existingPlayer -> {
                    player.setId(id);

                    if (teamId != null) {
                        Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourcesNotFoundException(
                                        ExceptionMessages.getTeamNotExistMessage(teamId)));
                        player.setTeam(team);
                    } else {
                        player.setTeam(null);
                    }

                    playerRepository.save(player);
                    return true;
                })
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getPlayerNotExistMessage(id)));
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#id"),
            @CacheEvict(value = CACHE_NAME_WITH_TEAM, allEntries = true)
    })
    public boolean delete(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getPlayerNotExistMessage(id)));

        playerRepository.deleteById(id);
        return true;
    }

    @AspectAnnotation
    @Cacheable(value = "playersByAge", key = "#age")
    public List<PlayerDto> getPlayersByAge(final Integer age) {
        ValidationUtils.validateNonNegative(AGE_FIELD, age);
        List<PlayerDto> playerDto = new ArrayList<>();
        for (Player player : playerRepository.findByAge(age)) {
            playerDto.add(ConvertDtoClasses.convertToPlayerDto(player));
        }
        return playerDto;
    }

    @AspectAnnotation
    @CacheEvict(value = CACHE_NAME_WITH_TEAM, allEntries = true)
    public void createBulk(List<Player> players) {
        if (players == null) {
            throw new BadRequestException("Players list cannot be null");
        }
        List<Player> validPlayers = players.stream()
                .filter(Objects::nonNull)
                .map(player -> {
                    ValidationUtils.validateProperName(player.getCountry());
                    ValidationUtils.validateNonNegative(AGE_FIELD, player.getAge());
                    return player;
                })
                .toList();
        if (validPlayers.isEmpty()) {
            throw new BadRequestException("No valid players provided");
        }
        playerRepository.saveAll(validPlayers);
    }
}