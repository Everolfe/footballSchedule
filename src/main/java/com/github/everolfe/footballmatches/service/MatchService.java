package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.NotExistMessage;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.exceptions.ValidationUtils;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;




@Service
@Transactional
@AllArgsConstructor
public class MatchService  {

    private static final String ID_FIELD = "id";
    private static final String TOURNAMENT_NAME_FIELD = "tournamentName";

    private final Cache<String, Object> cache;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final ArenaRepository  arenaRepository;

    @AspectAnnotation
    public void create(Match match) {
        if (match == null) {
            throw new BadRequestException("Match is null");
        }
        ValidationUtils.validateCapitalizedWords(TOURNAMENT_NAME_FIELD, match.getTournamentName());
        ValidationUtils.validateDateFormat(match.getDateTime().toString());
        matchRepository.save(match);
        cache.put(CacheConstants.getMatchCacheKey(match.getId()), match);
    }

    @AspectAnnotation
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
    public MatchDtoWithArenaAndTeams read(final Integer id) throws ResourcesNotFoundException {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Object match = cache.get(CacheConstants.getMatchCacheKey(id));
        if (match != null) {
            return (MatchDtoWithArenaAndTeams) match;
        } else {
            MatchDtoWithArenaAndTeams matchDtoWithArenaAndTeams = ConvertDtoClasses
                    .convertToMatchDtoWithArenaAndTeams(matchRepository.findById(id)
                            .orElseThrow(() -> new ResourcesNotFoundException(
                                    NotExistMessage.getMatchNotExistMessage(id))));
            cache.put(CacheConstants.getMatchCacheKey(id), matchDtoWithArenaAndTeams);
            return matchDtoWithArenaAndTeams;
        }
    }

    @AspectAnnotation
    public boolean update(Match match, final Integer id) {
        ValidationUtils.validateCapitalizedWords(TOURNAMENT_NAME_FIELD, match.getTournamentName());
        ValidationUtils.validateDateFormat(match.getDateTime().toString());
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Optional<Match> existingMatch = matchRepository.findById(id);
        if (existingMatch.isPresent()) {
            match.setId(id);
            matchRepository.save(match);
            cache.put(CacheConstants.getMatchCacheKey(id), match);
            return true;
        } else {
            throw new ResourcesNotFoundException(NotExistMessage.getMatchNotExistMessage(id));
        }
    }

    @AspectAnnotation
    public boolean delete(final Integer matchId) {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        Optional<Match> matchOptional = matchRepository.findById(matchId);
        if (matchOptional.isPresent()) {
            Match match = matchOptional.get();
            List<Team> teams = match.getTeamList();
            if (teams != null) {
                teams.forEach(team -> {
                    team.getMatches().remove(match);
                    cache.put(CacheConstants.getTeamCacheKey(team.getId()), team);
                    teamRepository.save(team);
                });
            }
            Arena arena = match.getArena();
            arena.getMatchList().remove(match);
            arenaRepository.save(arena);
            cache.put(CacheConstants.getArenaCacheKey(arena.getId()), arena);
            matchRepository.deleteById(matchId);
            cache.remove(CacheConstants.getMatchCacheKey(matchId));
            return true;
        } else {
            throw new ResourcesNotFoundException(NotExistMessage.getMatchNotExistMessage(matchId));
        }
    }

    @AspectAnnotation
    public boolean addTeamToMatch(final Integer matchId, final Integer teamId)
            throws ResourcesNotFoundException, BadRequestException {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        ValidationUtils.validateNonNegative(ID_FIELD, teamId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getMatchNotExistMessage(matchId)));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getTeamNotExistMessage(teamId)));
        if (!match.getTeamList().contains(team)) {
            team.getMatches().add(match);
            teamRepository.save(team);
            cache.put(CacheConstants.getTeamCacheKey(teamId), team);
            match.getTeamList().add(team);
            matchRepository.save(match);
            cache.put(CacheConstants.getMatchCacheKey(matchId), match);
            return true;
        } else {
            throw new BadRequestException("Match have already such team");
        }
    }

    @AspectAnnotation
    public boolean removeTeamFromMatch(final Integer matchId, final Integer teamId)
            throws ResourcesNotFoundException, BadRequestException {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        ValidationUtils.validateNonNegative(ID_FIELD, teamId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getMatchNotExistMessage(matchId)));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getTeamNotExistMessage(teamId)));
        if (match.getTeamList().contains(team)) {
            team.getMatches().remove(match);
            teamRepository.save(team);
            cache.put(CacheConstants.getTeamCacheKey(teamId), team);
            match.getTeamList().remove(team);
            matchRepository.save(match);
            cache.put(CacheConstants.getMatchCacheKey(matchId), match);
            return true;
        } else {
            throw new BadRequestException("Match does not have such team");
        }
    }

    @AspectAnnotation
    public boolean setNewArena(final Integer matchId, final Integer arenaId)
            throws ResourcesNotFoundException {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        ValidationUtils.validateNonNegative(ID_FIELD, arenaId);
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getMatchNotExistMessage(matchId)));
        Arena newArena = arenaRepository.findById(arenaId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getArenaNotExistMessage(arenaId)));
        newArena.getMatchList().add(match);
        arenaRepository.save(newArena);
        cache.put(CacheConstants.getArenaCacheKey(arenaId), newArena);
        match.setArena(newArena);
        matchRepository.save(match);
        cache.put(CacheConstants.getMatchCacheKey(matchId), match);
        return true;
    }

    @AspectAnnotation
    public boolean updateMatchTime(final Integer matchId, final LocalDateTime time)
            throws ResourcesNotFoundException {
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        ValidationUtils.validateDateFormat(time.toString());
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getMatchNotExistMessage(matchId)));
        match.setDateTime(time);
        matchRepository.save(match);
        cache.put(CacheConstants.getMatchCacheKey(matchId), match);
        return true;
    }

    @AspectAnnotation
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
            throw new BadRequestException("No valid players provided");
        }
        matchRepository.saveAll(validMatches);
    }
}
