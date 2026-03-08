package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ExceptionMessages;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.exceptions.ValidationUtils;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class MatchService  {

    private static final String ID_FIELD = "id";
    private static final String TOURNAMENT_NAME_FIELD = "tournamentName";
    private static final String CACHE_NAME = "matches";
    private static final String CACHE_NAME_WITH_ARENA_AND_TEAMS = "matchesWithArenaAndTeams";

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final ArenaRepository arenaRepository;

    @AspectAnnotation
    @CachePut(value = CACHE_NAME, key = "#result.id")
    @Transactional
    public Match create(Match match) {
        if (match == null) {
            throw new BadRequestException("Match is null");
        }
        ValidationUtils.validateCapitalizedWords(TOURNAMENT_NAME_FIELD, match.getTournamentName());
        ValidationUtils.validateDateFormat(match.getDateTime().toString());
        return matchRepository.save(match);
    }

    @AspectAnnotation
    @Cacheable(value = CACHE_NAME_WITH_ARENA_AND_TEAMS)
    @Transactional(readOnly = true)
    public List<MatchDtoWithArenaAndTeams> readAll() {
        List<Match> matches = matchRepository.findAll();
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList = new ArrayList<>();
        if (!matches.isEmpty()) {
            for (Match match : matches) {
                matchDtoWithArenaAndTeamsList
                        .add(ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
            }
        }
        return matchDtoWithArenaAndTeamsList;
    }

    @AspectAnnotation
    @Cacheable(value = CACHE_NAME, key = "#id")
    @Transactional(readOnly = true)
    public MatchDtoWithArenaAndTeams read(final Integer id) throws ResourcesNotFoundException {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getMatchNotExistMessage(id)));
        return ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match);
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_WITH_ARENA_AND_TEAMS, allEntries = true),
            @CacheEvict(value = CACHE_NAME, key = "#id")
    })
    @Transactional
    public boolean update(Match match, final Integer id) {
        ValidationUtils.validateCapitalizedWords(TOURNAMENT_NAME_FIELD, match.getTournamentName());
        ValidationUtils.validateDateFormat(match.getDateTime().toString());
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Optional<Match> existingMatch = matchRepository.findById(id);
        if (existingMatch.isPresent()) {
            match.setId(id);
            matchRepository.save(match);
            return true;
        } else {
            throw new ResourcesNotFoundException(ExceptionMessages.getMatchNotExistMessage(id));
        }
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#matchId"),
            @CacheEvict(value = CACHE_NAME_WITH_ARENA_AND_TEAMS, allEntries = true)
    })
    @Transactional
    public boolean delete(final Integer matchId) {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            List<Team> teams = match.getTeamList();
            if (teams != null) {
                teams.forEach(team -> {
                    team.getMatches().remove(match);
                    teamRepository.save(team);
                });
            }
            Arena arena = match.getArena();
            if (arena != null) {
                arena.getMatchList().remove(match);
                arenaRepository.save(arena);
            }
            matchRepository.deleteById(matchId);
            return true;
        } else {
            throw new ResourcesNotFoundException(
                    ExceptionMessages.getMatchNotExistMessage(matchId));
        }
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#matchId"),
            @CacheEvict(value = CACHE_NAME_WITH_ARENA_AND_TEAMS, allEntries = true)
    })
    @Transactional
    public boolean addTeamToMatch(final Integer matchId, final Integer teamId)
            throws ResourcesNotFoundException, BadRequestException {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        ValidationUtils.validateNonNegative(ID_FIELD, teamId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getMatchNotExistMessage(matchId)));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getTeamNotExistMessage(teamId)));
        if (!match.getTeamList().contains(team)) {
            team.getMatches().add(match);
            teamRepository.save(team);
            match.getTeamList().add(team);
            matchRepository.save(match);
            return true;
        } else {
            throw new BadRequestException("Match already has such team");
        }
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#matchId"),
            @CacheEvict(value = CACHE_NAME_WITH_ARENA_AND_TEAMS, allEntries = true)
    })
    @Transactional
    public boolean removeTeamFromMatch(final Integer matchId, final Integer teamId)
            throws ResourcesNotFoundException, BadRequestException {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        ValidationUtils.validateNonNegative(ID_FIELD, teamId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getMatchNotExistMessage(matchId)));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getTeamNotExistMessage(teamId)));
        if (match.getTeamList().contains(team)) {
            team.getMatches().remove(match);
            teamRepository.save(team);
            match.getTeamList().remove(team);
            matchRepository.save(match);
            return true;
        } else {
            throw new BadRequestException("Match does not have such team");
        }
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#matchId"),
            @CacheEvict(value = CACHE_NAME_WITH_ARENA_AND_TEAMS, allEntries = true)
    })
    @Transactional
    public boolean setNewArena(final Integer matchId, final Integer arenaId)
            throws ResourcesNotFoundException {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        ValidationUtils.validateNonNegative(ID_FIELD, arenaId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getMatchNotExistMessage(matchId)));
        Arena newArena = arenaRepository.findById(arenaId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getArenaNotExistMessage(arenaId)));

        // Remove from old arena if exists
        Arena oldArena = match.getArena();
        if (oldArena != null) {
            oldArena.getMatchList().remove(match);
            arenaRepository.save(oldArena);
        }

        newArena.getMatchList().add(match);
        arenaRepository.save(newArena);
        match.setArena(newArena);
        matchRepository.save(match);
        return true;
    }

    @AspectAnnotation
    @CacheEvict(value = CACHE_NAME, key = "#matchId")
    @Transactional
    public boolean updateMatchTime(final Integer matchId, final LocalDateTime time)
            throws ResourcesNotFoundException {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        ValidationUtils.validateDateFormat(time.toString());
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getMatchNotExistMessage(matchId)));
        match.setDateTime(time);
        matchRepository.save(match);
        return true;
    }

    @AspectAnnotation
    @Cacheable(value = "matchesByDate", key = "{#startDate, #endDate}")
    @Transactional(readOnly = true)
    public List<MatchDtoWithArenaAndTeams> findMatchesByDates(
            LocalDateTime startDate, LocalDateTime endDate) {

        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList = new ArrayList<>();
        if (startDate == null && endDate == null
                || startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return matchDtoWithArenaAndTeamsList;
        } else if (startDate == null) {
            for (Match match : matchRepository.findByDateTimeLessThanEqual(endDate)) {
                matchDtoWithArenaAndTeamsList.add(
                        ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
            }
        } else if (endDate == null) {
            for (Match match : matchRepository.findByDateTimeGreaterThanEqual(startDate)) {
                matchDtoWithArenaAndTeamsList.add(
                        ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
            }
        } else {
            for (Match match : matchRepository.findByDateTimeBetween(startDate, endDate)) {
                matchDtoWithArenaAndTeamsList.add(
                        ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
            }
        }
        return matchDtoWithArenaAndTeamsList;
    }

    @AspectAnnotation
    @Cacheable(value = "matchesByTournament", key = "#tournamentName")
    @Transactional(readOnly = true)
    public List<MatchDtoWithArenaAndTeams> getMatchesByTournamentName(final String tournamentName) {
        ValidationUtils.validateCapitalizedWords(TOURNAMENT_NAME_FIELD, tournamentName);
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList = new ArrayList<>();
        for (Match match : matchRepository.findByTournamentNameIgnoreCase(tournamentName)) {
            matchDtoWithArenaAndTeamsList
                    .add(ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
        }
        return matchDtoWithArenaAndTeamsList;
    }

    @AspectAnnotation
    @CacheEvict(value = CACHE_NAME_WITH_ARENA_AND_TEAMS, allEntries = true)
    @Transactional
    public void createBulk(List<Match> matches) {
        if (matches == null) {
            throw new BadRequestException("Match list cannot be null");
        }
        List<Match> validMatches = matches.stream()
                .filter(Objects::nonNull)
                .map(match -> {
                    ValidationUtils.validateCapitalizedWords(
                            TOURNAMENT_NAME_FIELD, match.getTournamentName());
                    ValidationUtils.validateDateFormat(match.getDateTime().toString());
                    return match;
                })
                .toList();
        if (validMatches.isEmpty()) {
            throw new BadRequestException("No valid matches provided");
        }
        matchRepository.saveAll(validMatches);
    }
}