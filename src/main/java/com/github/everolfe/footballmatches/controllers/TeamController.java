package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatchesAndPlayers;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "TeamController",
        description = "You can edit and view information about teams")
@RestController
@RequestMapping("/teams")
@AllArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @Operation(summary = "Creating a team",
            description = "Allows you create a team")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createTeam(
            @Parameter(description = "JSON object of new team ")
            @RequestBody Team team) {
        teamService.create(team);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "View all teams",
            description = "Allows you to view all teams")
    @GetMapping
    public ResponseEntity<List<TeamDtoWithMatchesAndPlayers>> readAllTeams() {
        final List<TeamDtoWithMatchesAndPlayers> teams = teamService.readAll();
        return teams != null && !teams.isEmpty()
                ? new ResponseEntity<List<TeamDtoWithMatchesAndPlayers>>(teams, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "View all matches by country",
            description = "Allows you to view teams with a given country")
    @GetMapping(value = "/search")
    public ResponseEntity<List<TeamDtoWithPlayers>> readTeamsByCountry(
            @Parameter(description = "Country")
            @RequestParam(value = "country") String country) {
        final List<TeamDtoWithPlayers> teams = teamService.getTeamsByCountry(country);
        return teams != null && !teams.isEmpty()
                ? new ResponseEntity<List<TeamDtoWithPlayers>>(teams, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "View a team by ID",
            description = "Allows you to view a team with a given ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<TeamDtoWithPlayers> readTeamById(
            @Parameter(description = "ID of the team to be found ")
            @PathVariable(name = "id") Integer id) {
        final TeamDtoWithPlayers team = teamService.read(id);
        return team != null
                ? new ResponseEntity<TeamDtoWithPlayers>(team, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Сhange team data",
            description = "Allows you to change team data")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updateTeam(
            @Parameter(description = "ID of the team to be update data")
            @PathVariable(name = "id") Integer id,
            @Parameter(description = "New data")
            @RequestBody Team team) {
        final boolean updated = teamService.update(team, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Remove player from team",
            description = "Allows you to delete player from team")
    @PatchMapping(value = "/{teamId}/del-player")
    public ResponseEntity<Void> deletePlayerFromTeam(
            @Parameter(description = "ID of the team to be update")
            @PathVariable(name = "teamId") final Integer teamId,
            @Parameter(description = "ID of removing player")
            @RequestParam(value = "playerId") final Integer playerId) throws Exception {
        final boolean updated = teamService.deletePlayerFromTeam(teamId, playerId);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Remove match from team",
            description = "Allows you to delete match from team")
    @PatchMapping(value = "/{teamId}/del-match")
    public ResponseEntity<Void> deleteMatchFromTeam(
            @Parameter(description = "ID of the team to be update")
            @PathVariable(name = "teamId") final Integer teamId,
            @Parameter(description = "ID of removing match")
            @RequestParam(value = "matchId") final Integer matchId) throws Exception {
        final boolean updated = teamService.deleteMatchFromTeam(teamId, matchId);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Add match to team",
            description = "Allows you to add match to team")
    @PatchMapping(value = "/{teamId}/add-match")
    public ResponseEntity<Void> addMatchToTeam(
            @Parameter(description = "ID of the team to be update")
            @PathVariable(name = "teamId") final Integer teamId,
            @Parameter(description = "ID of added match")
            @RequestParam(value = "matchId") final Integer matchId) throws Exception {
        final boolean updated = teamService.addMatchToTeam(teamId, matchId);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Add player to team",
            description = "Allows you to add player to team")
    @PatchMapping(value = "/{teamId}/add-player")
    public ResponseEntity<Void> addPlayerToTeam(
            @Parameter(description = "ID of the team to be update")
            @PathVariable(name = "teamId") final Integer teamId,
            @Parameter(description = "ID of added player")
            @RequestParam(value = "playerId") final Integer matchId) throws Exception {
        final boolean updated = teamService.addPlayerToTeam(teamId, matchId);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    @Operation(summary = "Delete team",
            description = "Allows you to delete team by it ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "ID of the team to be delete")
            @PathVariable(name = "id") Integer id) {
        final boolean deleted = teamService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
