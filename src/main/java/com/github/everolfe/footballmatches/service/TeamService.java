package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatchesAndPlayers;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;



@Service
@Transactional
@AllArgsConstructor
public class TeamService {

    private final Cache<String, Object> cache;
    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final  PlayerRepository playerRepository;
    private static final String DOESNT_EXIST = "The team does not exist with ID = ";
    private static final String MATCH_DOESNT_EXIST = "Match does not exist with ID = ";
    private static final String PLAYER_DOESNT_EXIST = "Player does not exist with ID = ";
    private static final String PLAYER_CACHE_PREFIX = "player_";
    private static final String MATCH_CACHE_PREFIX = "match_";
    private static final String TEAM_CACHE_PREFIX = "team_";

    public void create(Team team) {
        teamRepository.save(team);
        cache.put(TEAM_CACHE_PREFIX + team.getId().toString(), team);
    }

    public List<TeamDtoWithMatchesAndPlayers> readAll() {
        List<Team> teams = teamRepository.findAll();
        List<TeamDtoWithMatchesAndPlayers> teamDtoWithMatchesAndPlayers = new ArrayList<>();
        if (teams != null) {
            for (Team team : teams) {
                teamDtoWithMatchesAndPlayers.add(ConvertDtoClasses
                        .convertToTeamDtoWithMatchesAndPlayers(team));
            }
        }
        return teamDtoWithMatchesAndPlayers;
    }


    public TeamDtoWithPlayers read(final Integer id) {
        Object team = cache.get(TEAM_CACHE_PREFIX + id.toString());
        if (team != null) {
            return (TeamDtoWithPlayers) team;
        } else {
            TeamDtoWithPlayers teamDtoWithPlayers = ConvertDtoClasses
                    .convertToTeamDtoWithPlayers(teamRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + id)));
            cache.put(TEAM_CACHE_PREFIX + id.toString(), teamDtoWithPlayers);
            return teamDtoWithPlayers;
        }
    }


    public boolean update(Team team, final Integer id) {
        if (teamRepository.existsById(id)) {
            team.setId(id);
            teamRepository.save(team);
            cache.put(TEAM_CACHE_PREFIX + id.toString(), team);
            return true;
        }
        return false;
    }


    public boolean delete(final Integer id) {
        if (teamRepository.existsById(id)) {
            Team team = teamRepository.findById(id).orElseThrow(
                    () -> new ResourceNotFoundException(DOESNT_EXIST + id));
            List<Player> players = team.getPlayers();
            if (players != null) {
                for (Player player : players) {
                    player.setTeam(null);
                    cache.put(PLAYER_CACHE_PREFIX + player.getId().toString(), player);
                    playerRepository.save(player);
                }
            }
            List<Match> matches = team.getMatches();
            if (matches != null) {
                for (Match match : matches) {
                    List<Team> teamList = match.getTeamList();
                    if (teamList != null) {
                        teamList.removeIf(team2 -> team.getId().equals(team2.getId()));
                        cache.put(MATCH_CACHE_PREFIX + match.getId().toString(), match);
                        matchRepository.save(match);
                    }
                }
            }
            teamRepository.deleteById(id);
            cache.remove(TEAM_CACHE_PREFIX + id.toString());
            return true;
        }
        return false;
    }

    public boolean addPlayerToTeam(final Integer teamId, final Integer playerId) throws Exception {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + teamId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PLAYER_DOESNT_EXIST + playerId));
        if (player.getTeam() != null && player.getTeam().equals(team)) {
            throw new BadRequestException("Player already exists in this team");
        }
        player.setTeam(team);
        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }
        team.getPlayers().add(player);
        teamRepository.save(team);
        cache.put(TEAM_CACHE_PREFIX + teamId.toString(), team);
        playerRepository.save(player);
        cache.put(PLAYER_CACHE_PREFIX + playerId.toString(), player);
        return true;
    }

    public boolean deletePlayerFromTeam(
            final Integer teamId, final Integer playerId) throws Exception {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + teamId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        PLAYER_DOESNT_EXIST + playerId));
        if (!team.getPlayers().contains(player)) {
            throw new IllegalArgumentException("Player does not belong to the team");
        }
        player.setTeam(null);
        playerRepository.save(player);
        team.getPlayers().remove(player);
        teamRepository.save(team);
        cache.put(PLAYER_CACHE_PREFIX + playerId.toString(), player);
        cache.put(TEAM_CACHE_PREFIX + teamId.toString(), team);
        return true;
    }

    public boolean addMatchToTeam(final Integer teamId, final Integer matchId) throws Exception {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + teamId));
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MATCH_DOESNT_EXIST + matchId));
        if (match.getTeamList().isEmpty()) {
            List<Team> teamList = new ArrayList<>();
            teamList.add(team);
            match.setTeamList(teamList);
            matchRepository.save(match);
            cache.put(MATCH_CACHE_PREFIX + matchId.toString(), match);
        } else if (match.getTeamList().contains(team)) {
            throw new BadRequestException("Match already exists");
        } else if (match.getTeamList().size() >= 2) {
            throw new BadRequestException("Match can contain only 2 teams");
        } else {
            match.getTeamList().add(team);
            matchRepository.save(match);
            cache.put(MATCH_CACHE_PREFIX + matchId.toString(), match);
        }
        return true;
    }

    public boolean deleteMatchFromTeam(
            final Integer teamId, final Integer matchId) throws Exception {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + teamId));
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MATCH_DOESNT_EXIST + matchId));
        if (!match.getTeamList().contains(team)) {
            throw new IllegalArgumentException("Team does not participate in the match");
        }
        if (match.getTeamList().remove(team)) {
            matchRepository.save(match);
            cache.put(MATCH_CACHE_PREFIX + matchId.toString(), match);
        }
        if (team.getMatches().remove(match)) {
            teamRepository.save(team);
            cache.remove(TEAM_CACHE_PREFIX + teamId.toString());
        }
        return true;
    }



    public List<TeamDtoWithPlayers> getTeamsByCountry(final String country) {
        List<TeamDtoWithPlayers> teamDtoWithPlayers = new ArrayList<>();
        for (Team team : teamRepository.findByCountryIgnoreCase(country)) {
            teamDtoWithPlayers.add(ConvertDtoClasses.convertToTeamDtoWithPlayers(team));
        }
        return teamDtoWithPlayers;
    }
}
