package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatchesAndPlayers;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.NotExistMessage;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.exceptions.ValidationUtils;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@Transactional
@AllArgsConstructor
public class TeamService {

    private static final String ID_FIELD = "id";
    private static final String TEAM_NAME_FIELD = "teamName";

    private final Cache<String, Object> cache;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final  PlayerRepository playerRepository;

    @AspectAnnotation
    public void create(Team team) {
        if (team == null) {
            throw new BadRequestException("Team is null");
        }
        ValidationUtils.validateProperName(team.getCountry());
        ValidationUtils.validateCapitalizedWords(TEAM_NAME_FIELD, team.getTeamName());
        teamRepository.save(team);
        cache.put(CacheConstants.getTeamCacheKey(team.getId()), team);
    }


    @AspectAnnotation
    public List<TeamDtoWithMatchesAndPlayers> readAll() {
        List<Team> teams = teamRepository.findAll();
        List<TeamDtoWithMatchesAndPlayers> teamDtoWithMatchesAndPlayers = new ArrayList<>();
        if (!teams.isEmpty()) {
            for (Team team : teams) {
                teamDtoWithMatchesAndPlayers.add(ConvertDtoClasses
                        .convertToTeamDtoWithMatchesAndPlayers(team));
            }
        }
        return teamDtoWithMatchesAndPlayers;
    }

    @AspectAnnotation
    public TeamDtoWithPlayers read(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Object team = cache.get(CacheConstants.getTeamCacheKey(id));
        if (team != null) {
            return (TeamDtoWithPlayers) team;
        } else {
            TeamDtoWithPlayers teamDtoWithPlayers = ConvertDtoClasses
                    .convertToTeamDtoWithPlayers(teamRepository.findById(id)
                            .orElseThrow(() -> new ResourcesNotFoundException(
                                    NotExistMessage.getTeamNotExistMessage(id))));
            cache.put(CacheConstants.getTeamCacheKey(id), teamDtoWithPlayers);
            return teamDtoWithPlayers;
        }
    }

    @AspectAnnotation
    public boolean update(Team team, final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        return teamRepository.findById(id)
                .map(existingTeam -> {
                    team.setId(id);
                    teamRepository.save(team);
                    cache.put(CacheConstants.getTeamCacheKey(id), team);
                    return true;
                })
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getTeamNotExistMessage(id)));
    }

    @AspectAnnotation
    public boolean delete(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getTeamNotExistMessage(id)));
        List<Player> players = team.getPlayers();
        if (players != null) {
            for (Player player : players) {
                player.setTeam(null);
                cache.put(CacheConstants.getPlayerCacheKey(player.getId()), player);
                playerRepository.save(player);
            }
        }
        List<Match> matches = team.getMatches();
        if (matches != null) {
            for (Match match : matches) {
                List<Team> teamList = match.getTeamList();
                if (teamList != null) {
                    teamList.removeIf(team2 -> team.getId().equals(team2.getId()));
                    cache.put(CacheConstants.getMatchCacheKey(match.getId()), match);
                    matchRepository.save(match);
                }
            }
        }
        teamRepository.deleteById(id);
        cache.remove(CacheConstants.getTeamCacheKey(id));
        return true;
    }

    @AspectAnnotation
    public boolean addPlayerToTeam(final Integer teamId, final Integer playerId)
            throws ResourcesNotFoundException, BadRequestException {
        ValidationUtils.validateNonNegative(ID_FIELD, teamId);
        ValidationUtils.validateNonNegative(ID_FIELD, playerId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getTeamNotExistMessage(teamId)));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getPlayerNotExistMessage(playerId)));
        if (player.getTeam() != null && player.getTeam().equals(team)) {
            throw new BadRequestException("Player already exists in this team");
        }
        player.setTeam(team);
        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }
        team.getPlayers().add(player);
        teamRepository.save(team);
        cache.put(CacheConstants.getTeamCacheKey(teamId), team);
        playerRepository.save(player);
        cache.put(CacheConstants.getPlayerCacheKey(playerId), player);
        return true;
    }

    @AspectAnnotation
    public boolean deletePlayerFromTeam(
            final Integer teamId, final Integer playerId)
            throws ResourcesNotFoundException, BadRequestException {
        ValidationUtils.validateNonNegative(ID_FIELD, teamId);
        ValidationUtils.validateNonNegative(ID_FIELD, playerId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getTeamNotExistMessage(teamId)));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getPlayerNotExistMessage(playerId)));
        if (!team.getPlayers().contains(player)) {
            throw new BadRequestException("Player does not belong to the team");
        }
        player.setTeam(null);
        playerRepository.save(player);
        team.getPlayers().remove(player);
        teamRepository.save(team);
        cache.put(CacheConstants.getPlayerCacheKey(playerId), player);
        cache.put(CacheConstants.getTeamCacheKey(teamId), team);
        return true;
    }

    @AspectAnnotation
    public boolean addMatchToTeam(final Integer teamId, final Integer matchId)
            throws ResourcesNotFoundException, BadRequestException {
        ValidationUtils.validateNonNegative(ID_FIELD, teamId);
        ValidationUtils.validateNonNegative(ID_FIELD, matchId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getTeamNotExistMessage(teamId)));
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getMatchNotExistMessage(matchId)));
        if (match.getTeamList().isEmpty()) {
            List<Team> teamList = new ArrayList<>();
            teamList.add(team);
            match.setTeamList(teamList);
            matchRepository.save(match);
            cache.put(CacheConstants.getMatchCacheKey(matchId), match);
        } else if (match.getTeamList().contains(team)) {
            throw new BadRequestException("Match already exists");
        } else if (match.getTeamList().size() >= 2) {
            throw new BadRequestException("Match can contain only 2 teams");
        } else {
            match.getTeamList().add(team);
            matchRepository.save(match);
            cache.put(CacheConstants.getMatchCacheKey(matchId), match);
        }
        return true;
    }

    @AspectAnnotation
    public boolean deleteMatchFromTeam(
            final Integer teamId, final Integer matchId)
            throws ResourcesNotFoundException, BadRequestException {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getTeamNotExistMessage(teamId)));
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getMatchNotExistMessage(matchId)));
        if (!match.getTeamList().contains(team)) {
            throw new IllegalArgumentException("Team does not participate in the match");
        }
        if (match.getTeamList().remove(team)) {
            matchRepository.save(match);
            cache.put(CacheConstants.getMatchCacheKey(matchId), match);
        }
        if (team.getMatches().remove(match)) {
            teamRepository.save(team);
            cache.remove(CacheConstants.getTeamCacheKey(teamId));
        }
        return true;
    }

    @AspectAnnotation
    public List<TeamDtoWithPlayers> getTeamsByCountry(final String country) {
        ValidationUtils.validateProperName(country);
        List<TeamDtoWithPlayers> teamDtoWithPlayers = new ArrayList<>();
        for (Team team : teamRepository.findByCountryIgnoreCase(country)) {
            teamDtoWithPlayers.add(ConvertDtoClasses.convertToTeamDtoWithPlayers(team));
        }
        return teamDtoWithPlayers;
    }

    public void createBulk(List<Team> teams) {
        if (teams == null) {
            throw new BadRequestException("Teams list cannot be null");
        }
        List<Team> validTeams = teams.stream()
                .filter(Objects::nonNull)
                .map(team -> {
                    ValidationUtils.validateProperName(team.getCountry());
                    ValidationUtils.validateCapitalizedWords(TEAM_NAME_FIELD, team.getTeamName());
                    return team;
                })
                .toList();
        if (validTeams.isEmpty()) {
            throw new BadRequestException("No valid teams provided");
        }
        teamRepository.saveAll(teams);
    }
}
