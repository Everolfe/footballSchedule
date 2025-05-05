package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
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
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class PlayerService {

    private static final String ID_FIELD = "id";
    private static final String AGE_FIELD = "age";

    private final Cache<String, Object> cache;
    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;

    @AspectAnnotation
    public void create(Player player, final Integer teamId) {
        if (teamId != null) {
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new ResourcesNotFoundException(
                            ExceptionMessages.getTeamNotExistMessage(teamId)));
            player.setTeam(team);
        }
        if (player == null) {
            throw new BadRequestException("Player is null");
        }
        ValidationUtils.validateProperName(player.getCountry());
        ValidationUtils.validateNonNegative(AGE_FIELD, player.getAge());
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
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Object cachedPlayer = cache.get(CacheConstants.getPlayerCacheKey(id));
        if (cachedPlayer != null) {
            return (PlayerDto) cachedPlayer;
        } else {
            PlayerDto playerDto = ConvertDtoClasses.convertToPlayerDto(playerRepository.findById(id)
                    .orElseThrow(() -> new ResourcesNotFoundException(
                            ExceptionMessages.getPlayerNotExistMessage(id))));
            cache.put(CacheConstants.getPlayerCacheKey(id), playerDto);
            return playerDto;
        }
    }

    @AspectAnnotation
    public boolean update(Player player, final Integer id, final Integer teamId) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);

        return playerRepository.findById(id)
                .map(existingPlayer -> {
                    player.setId(id);

                    if (teamId != null) {
                        Team team = teamRepository.findById(teamId)
                                .orElseThrow(() -> new ResourcesNotFoundException(
                                        ExceptionMessages.getTeamNotExistMessage(teamId)));
                        player.setTeam(team);
                    } else {
                        player.setTeam(null); // Убираем команду у игрока, если teamId не передан
                    }

                    playerRepository.save(player);
                    cache.put(CacheConstants.getPlayerCacheKey(id), player);
                    return true;
                })
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getPlayerNotExistMessage(id)));
    }


    @AspectAnnotation
    public boolean delete(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getPlayerNotExistMessage(id)));
        if (player.getTeam() != null) {
            Team team = player.getTeam();
            teamRepository.save(team);
            cache.put(CacheConstants.getTeamCacheKey(team.getId()), team);
        }
        playerRepository.deleteById(id);
        cache.remove(CacheConstants.getPlayerCacheKey(player.getId()));
        return true;
    }

    @AspectAnnotation
    public List<PlayerDto> getPlayersByAge(final Integer age) {
        ValidationUtils.validateNonNegative(AGE_FIELD, age);
        List<PlayerDto> playerDto = new ArrayList<>();
        for (Player player : playerRepository.findByAge(age)) {
            playerDto.add(ConvertDtoClasses.convertToPlayerDto(player));
        }
        return playerDto;
    }

    @AspectAnnotation
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
