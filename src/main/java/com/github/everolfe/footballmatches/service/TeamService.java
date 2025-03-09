package com.github.everolfe.footballmatches.service;

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

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final  PlayerRepository playerRepository;
    private static final String DOESNT_EXIST = "The team does not exist with ID = ";

    public void create(Team team) {
        teamRepository.save(team);
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
        return ConvertDtoClasses
                .convertToTeamDtoWithPlayers(teamRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + id)));
    }


    public boolean update(Team team, final Integer id) {
        if (teamRepository.existsById(id)) {
            team.setId(id);
            teamRepository.save(team);
            return true;
        }
        return false;
    }


    public boolean delete(final Integer id) {
        if (teamRepository.existsById(id)) {
            teamRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean addPlayerToTeam(final Integer teamId, final Integer playerId) throws Exception {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + teamId));
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dont find player with id = " + playerId));
        if (player.getTeam() != null && player.getTeam().equals(team)) {
            throw new BadRequestException("Player already exists in this team");
        }
        player.setTeam(team);
        if (team.getPlayers() == null) {
            team.setPlayers(new ArrayList<>());
        }
        team.getPlayers().add(player);
        teamRepository.save(team);
        playerRepository.save(player);
        return true;
    }

    public boolean addMatchToTeam(final Integer teamId, final Integer matchId) throws Exception {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + teamId));
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Dont find match with id = " + matchId));
        if (match.getTeamList().isEmpty()) {
            List<Team> teamList = new ArrayList<>();
            teamList.add(team);
            match.setTeamList(teamList);
            matchRepository.save(match);
        } else if (match.getTeamList().contains(team)) {
            throw new BadRequestException("Match already exists");
        } else if (match.getTeamList().size() >= 2) {
            throw new BadRequestException("Match can contain only 2 teams");
        } else {
            match.getTeamList().add(team);
            matchRepository.save(match);
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
