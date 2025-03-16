package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
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
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;




@Service
@Transactional
@AllArgsConstructor
public class MatchService  {

    private final Cache<String, Object> cache;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final ArenaRepository  arenaRepository;
    private static final String DOESNT_EXIST = "Match does not exist with ID = ";
    private static final String TEAM_DOESNT_EXIST = "Team does not exist with ID = ";
    private static final String ARENA_DOESNT_EXIST = "Arena does not exist with ID = ";
    private static final String ARENA_CACHE_PREFIX = "arena_";
    private static final String MATCH_CACHE_PREFIX = "match_";
    private static final String TEAM_CACHE_PREFIX = "team_";

    public void create(Match match) {
        matchRepository.save(match);
        cache.put(MATCH_CACHE_PREFIX + match.getId().toString(), match);
    }

    public List<MatchDtoWithArenaAndTeams> readAll() {
        List<Match> matches = matchRepository.findAll();
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList = new ArrayList<>();
        if (matches != null) {
            for (Match match : matches) {
                matchDtoWithArenaAndTeamsList
                        .add(ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
            }
        }
        return matchDtoWithArenaAndTeamsList;
    }

    public MatchDtoWithArenaAndTeams read(final Integer id) throws ResourceNotFoundException {
        Object match = cache.get(MATCH_CACHE_PREFIX + id);
        if (match != null) {
            return (MatchDtoWithArenaAndTeams) match;
        } else {
            MatchDtoWithArenaAndTeams matchDtoWithArenaAndTeams = ConvertDtoClasses
                    .convertToMatchDtoWithArenaAndTeams(matchRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + id)));
            cache.put(MATCH_CACHE_PREFIX + id.toString(), matchDtoWithArenaAndTeams);
            return matchDtoWithArenaAndTeams;
        }
    }

    public boolean update(Match match, final Integer id) {
        if (matchRepository.existsById(id)) {
            match.setId(id);
            matchRepository.save(match);
            cache.put(MATCH_CACHE_PREFIX + id.toString(), match);
            return true;
        }
        return false;
    }

    public boolean delete(final Integer matchId) {
        if (matchRepository.existsById(matchId)) {
            Match match = matchRepository.findById(matchId).orElseThrow(
                    () -> new ResourceNotFoundException(DOESNT_EXIST + matchId));
            List<Team> teams = match.getTeamList();
            if (teams != null) {
                for (Team team : teams) {
                    team.getMatches().remove(match);
                    cache.put(TEAM_CACHE_PREFIX + team.getId().toString(), team);
                    teamRepository.delete(team);
                }
            }
            Arena arena = match.getArena();
            arena.getMatchList().remove(match);
            arenaRepository.save(arena);
            cache.put(ARENA_CACHE_PREFIX + arena.getId().toString(), arena);
            matchRepository.deleteById(matchId);
            cache.remove(MATCH_CACHE_PREFIX + matchId);
            return true;
        }
        return false;
    }

    public boolean addTeamToMatch(final Integer matchId, final Integer teamId) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + matchId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(TEAM_DOESNT_EXIST+ teamId));
        if (!match.getTeamList().contains(team)) {
            team.getMatches().add(match);
            teamRepository.save(team);
            cache.put(TEAM_CACHE_PREFIX + teamId.toString(), team);
            match.getTeamList().add(team);
            matchRepository.save(match);
            cache.put(MATCH_CACHE_PREFIX + matchId.toString(), match);
            return true;
        }
        return false;
    }

    public boolean removeTeamFromMatch(
            final Integer matchId, final Integer teamId) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + matchId));
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(TEAM_DOESNT_EXIST + teamId));
        if (match.getTeamList().contains(team)) {
            team.getMatches().remove(match);
            teamRepository.save(team);
            cache.put(TEAM_CACHE_PREFIX + teamId.toString(), team);
            match.getTeamList().remove(team);
            matchRepository.save(match);
            cache.put(MATCH_CACHE_PREFIX + matchId.toString(), match);
            return true;
        }
        return false;
    }

    public boolean setNewArena(final Integer matchId, final Integer arenaId) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + matchId));
        Arena newArena = arenaRepository.findById(arenaId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + arenaId));
        newArena.getMatchList().add(match);
        arenaRepository.save(newArena);
        cache.put(ARENA_CACHE_PREFIX + arenaId.toString(), newArena);
        match.setArena(newArena);
        matchRepository.save(match);
        cache.put(MATCH_CACHE_PREFIX + matchId.toString(), match);
        return true;
    }

    public boolean updateMatchTime(
            final Integer matchId, final LocalDateTime time) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + matchId));
        match.setDateTime(time);
        matchRepository.save(match);
        cache.put(MATCH_CACHE_PREFIX + matchId.toString(), match);
        return true;
    }

    public List<MatchDtoWithArenaAndTeams> findMatchesByDates(LocalDateTime startDate, LocalDateTime endDate) {
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList = new ArrayList<>();
        if (startDate == null && endDate == null
        || startDate != null && endDate != null && startDate.isAfter(endDate)) {
            return matchDtoWithArenaAndTeamsList;
        } else if(startDate == null) {
            for (Match match : matchRepository.findByDateTimeLessThanEqual(endDate)) {
                matchDtoWithArenaAndTeamsList.add(
                        ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
            }
        } else if(endDate == null) {
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

    public List<MatchDtoWithArenaAndTeams> getMatchesByTournamentName(final String tournamentName) {
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList = new ArrayList<>();
        for (Match match : matchRepository.findByTournamentNameIgnoreCase(tournamentName)) {
            matchDtoWithArenaAndTeamsList
                    .add(ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
        }
        return matchDtoWithArenaAndTeamsList;
    }
}
