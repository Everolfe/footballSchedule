package com.github.everolfe.footballmatches.dto;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArena;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithTeams;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatches;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatchesAndPlayers;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;


@Service
public class ConvertDtoClasses {
    private ConvertDtoClasses() {}

    public static ArenaDtoWithMatches convertToArenaDtoWithMatches(final Arena arena) {
        if (arena == null) {
            return null;
        }
        ArenaDtoWithMatches arenaDto = new ArenaDtoWithMatches();
        arenaDto.setId(arena.getId());
        arenaDto.setCity(arena.getCity());
        arenaDto.setCapacity(arena.getCapacity());
        List<MatchDtoWithTeams> matchDtoWithTeamsList = new ArrayList<>();
        if (arena.getMatchList() != null) {
            for (Match match : arena.getMatchList()) {
                matchDtoWithTeamsList.add(convertToMatchDtoWithTeams(match));
            }
        }
        arenaDto.setMatchDtoWithTeamsList(matchDtoWithTeamsList);
        return arenaDto;
    }

    public static ArenaDto convertToArenaDto(final Arena arena) {
        if (arena == null) {
            return null;
        }
        ArenaDto arenaDto = new ArenaDto();
        arenaDto.setId(arena.getId());
        arenaDto.setCity(arena.getCity());
        arenaDto.setCapacity(arena.getCapacity());
        return arenaDto;
    }

    public static MatchDtoWithTeams convertToMatchDtoWithTeams(final Match match) {
        if (match == null) {
            return null;
        }
        MatchDtoWithTeams matchDto = new MatchDtoWithTeams();
        matchDto.setId(match.getId());
        matchDto.setDateTime(match.getDateTime());
        matchDto.setTournamentName(match.getTournamentName());
        List<TeamDtoWithPlayers> teamDtoWithPlayersList = new ArrayList<>();
        if (match.getTeamList() != null) {
            for (Team team : match.getTeamList()) {
                teamDtoWithPlayersList.add(convertToTeamDtoWithPlayers(team));
            }
        }
        matchDto.setTeamDtoWithPlayersList(teamDtoWithPlayersList);
        return matchDto;
    }

    public static MatchDtoWithArenaAndTeams convertToMatchDtoWithArenaAndTeams(final Match match) {
        if (match == null) {
            return null;
        }
        MatchDtoWithArenaAndTeams matchDto = new MatchDtoWithArenaAndTeams();
        matchDto.setId(match.getId());
        matchDto.setDateTime(match.getDateTime());
        matchDto.setTournamentName(match.getTournamentName());
        matchDto.setArenaDto(convertToArenaDto(match.getArena()));
        List<TeamDtoWithPlayers> teamDtoWithPlayersList = new ArrayList<>();
        if (match.getTeamList() != null) {
            for (Team team : match.getTeamList()) {
                teamDtoWithPlayersList.add(convertToTeamDtoWithPlayers(team));
            }
        }
        matchDto.setTeamDtoWithPlayersList(teamDtoWithPlayersList);
        return matchDto;
    }

    public static MatchDtoWithArena convertToMatchDtoWithArena(final Match match) {
        if (match == null) {
            return null;
        }
        MatchDtoWithArena matchDto = new MatchDtoWithArena();
        matchDto.setId(match.getId());
        matchDto.setDateTime(match.getDateTime());
        matchDto.setTournamentName(match.getTournamentName());
        matchDto.setArenaDto(convertToArenaDto(match.getArena()));
        return matchDto;
    }

    public static PlayerDto convertToPlayerDto(final Player player) {
        if (player == null) {
            return null;
        }
        PlayerDto playerDto = new PlayerDto();
        playerDto.setId(player.getId());
        playerDto.setName(player.getName());
        playerDto.setAge(player.getAge());
        playerDto.setCountry(player.getCountry());
        return playerDto;
    }

    public static PlayerDtoWithTeam convertToPlayerDtoWithTeam(final Player player) {
        if (player == null) {
            return null;
        }
        PlayerDtoWithTeam playerDto = new PlayerDtoWithTeam();
        playerDto.setId(player.getId());
        playerDto.setName(player.getName());
        playerDto.setAge(player.getAge());
        playerDto.setCountry(player.getCountry());
        playerDto.setTeamDtoWithMatches(convertToTeamDtoWithMatches(player.getTeam()));
        return playerDto;
    }

    public static TeamDtoWithMatches convertToTeamDtoWithMatches(final Team team) {
        if (team == null) {
            return null;
        }
        TeamDtoWithMatches teamDto = new TeamDtoWithMatches();
        teamDto.setId(team.getId());
        teamDto.setCountry(team.getCountry());
        teamDto.setTeamName(team.getTeamName());
        List<MatchDtoWithArena> matchDtoWithArenaList = new ArrayList<>();
        if (team.getMatches() != null) {
            for (Match match : team.getMatches()) {
                matchDtoWithArenaList.add(convertToMatchDtoWithArena(match));
            }
        }
        teamDto.setMatchDtoWithArenaList(matchDtoWithArenaList);
        return teamDto;
    }

    public static TeamDtoWithPlayers convertToTeamDtoWithPlayers(final Team team) {
        if (team == null) {
            return null;
        }
        TeamDtoWithPlayers teamDto = new TeamDtoWithPlayers();
        teamDto.setId(team.getId());
        teamDto.setCountry(team.getCountry());
        teamDto.setTeamName(team.getTeamName());
        List<PlayerDto> playerDtoList = new ArrayList<>();
        if (team.getPlayers() != null) {
            for (Player player : team.getPlayers()) {
                playerDtoList.add(convertToPlayerDto(player));
            }
        }
        teamDto.setPlayerDtoList(playerDtoList);
        return teamDto;
    }

    public static TeamDtoWithMatchesAndPlayers convertToTeamDtoWithMatchesAndPlayers(
        final Team team) {

        if (team == null) {
            return null;
        }
        TeamDtoWithMatchesAndPlayers teamDto = new TeamDtoWithMatchesAndPlayers();
        teamDto.setId(team.getId());
        teamDto.setCountry(team.getCountry());
        teamDto.setTeamName(team.getTeamName());
        List<MatchDtoWithArena> matchDtoWithArenaList = new ArrayList<>();
        if (team.getMatches() != null) {
            for (Match match : team.getMatches()) {
                matchDtoWithArenaList.add(convertToMatchDtoWithArena(match));
            }
        }
        teamDto.setMatchDtoWithArenaList(matchDtoWithArenaList);
        List<PlayerDto> playerDtoList = new ArrayList<>();
        if (team.getPlayers() != null) {
            for (Player player : team.getPlayers()) {
                playerDtoList.add(convertToPlayerDto(player));
            }
        }
        teamDto.setPlayerDtoList(playerDtoList);
        return teamDto;
    }

}
