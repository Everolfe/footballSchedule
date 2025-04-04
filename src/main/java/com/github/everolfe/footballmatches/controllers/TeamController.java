package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatchesAndPlayers;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.service.TeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    private <T> ResponseEntity<T> handleResponse(final T body, final boolean condition) {
        return condition
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Creating a team",
            description = "Allow you create a team")
    @PostMapping("/create")
    public ResponseEntity<Void> createTeam(
            @Parameter(description = "JSON object of new team ")
            @Valid @RequestBody Team team) {
        teamService.create(team);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "View all teams",
            description = "Allow you to view all teams")
    @GetMapping
    public ResponseEntity<List<TeamDtoWithMatchesAndPlayers>> readAllTeams() {
        final List<TeamDtoWithMatchesAndPlayers> teams = teamService.readAll();
        return handleResponse(teams, !teams.isEmpty());
    }

    @Operation(summary = "View all matches by country",
            description = "Allow you to view teams with a given country")
    @GetMapping("/search")
    public ResponseEntity<List<TeamDtoWithPlayers>> readTeamsByCountry(
            @Parameter(description = "Country")
            @RequestParam(value = "country") String country) {
        final List<TeamDtoWithPlayers> teams = teamService.getTeamsByCountry(country);
        return handleResponse(teams, !teams.isEmpty());
    }

    @Operation(summary = "View a team by ID",
            description = "Allow you to view a team with a given ID")
    @GetMapping("/{id}")
    public ResponseEntity<TeamDtoWithPlayers> readTeamById(
            @Parameter(description = "ID of the team to be found ")
            @PathVariable(name = "id") Integer id) {
        final TeamDtoWithPlayers team = teamService.read(id);
        return handleResponse(team,  team != null);
    }

    @Operation(summary = "Сhange team data",
            description = "Allow you to change team data")
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateTeam(
            @Parameter(description = "ID of the team to be update data")
            @PathVariable(name = "id") Integer id,
            @Parameter(description = "New data")
            @Valid @RequestBody Team team) {
        return handleResponse(null, teamService.update(team, id));
    }

    @Operation(summary = "Remove player from team",
            description = "Allow you to delete player from team")
    @PatchMapping("/{teamId}/remove-player")
    public ResponseEntity<Void> deletePlayerFromTeam(
            @Parameter(description = "ID of the team to be update")
            @PathVariable(name = "teamId") final Integer teamId,
            @Parameter(description = "ID of removing player")
            @RequestParam(value = "playerId") final Integer playerId)
            throws ResourcesNotFoundException, BadRequestException {
        return handleResponse(null, teamService.deletePlayerFromTeam(teamId, playerId));
    }

    @Operation(summary = "Remove match from team",
            description = "Allow you to delete match from team")
    @PatchMapping("/{teamId}/remove-match")
    public ResponseEntity<Void> deleteMatchFromTeam(
            @Parameter(description = "ID of the team to be update")
            @PathVariable(name = "teamId") final Integer teamId,
            @Parameter(description = "ID of removing match")
            @RequestParam(value = "matchId") final Integer matchId)
            throws ResourcesNotFoundException, BadRequestException {
        return handleResponse(null, teamService.deleteMatchFromTeam(teamId, matchId));
    }

    @Operation(summary = "Add match to team",
            description = "Allow you to add match to team")
    @PatchMapping("/{teamId}/add-match")
    public ResponseEntity<Void> addMatchToTeam(
            @Parameter(description = "ID of the team to be update")
            @PathVariable(name = "teamId") final Integer teamId,
            @Parameter(description = "ID of added match")
            @RequestParam(value = "matchId") final Integer matchId)
            throws ResourcesNotFoundException, BadRequestException {
        return handleResponse(null, teamService.addMatchToTeam(teamId, matchId));
    }

    @Operation(summary = "Add player to team",
            description = "Allow you to add player to team")
    @PatchMapping("/{teamId}/add-player")
    public ResponseEntity<Void> addPlayerToTeam(
            @Parameter(description = "ID of the team to be update")
            @PathVariable(name = "teamId") final Integer teamId,
            @Parameter(description = "ID of added player")
            @RequestParam(value = "playerId") final Integer matchId)
            throws ResourcesNotFoundException, BadRequestException {
        return handleResponse(null, teamService.addPlayerToTeam(teamId, matchId));
    }

    @Operation(summary = "Delete team",
            description = "Allow you to delete team by it ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = "ID of the team to be delete")
            @PathVariable(name = "id") Integer id) {
        return handleResponse(null, teamService.delete(id));
    }

    @Operation(summary = "Bulk create teams",
            description = "Allow you to create multiple teams at once")
    @PostMapping("/bulk-create")
    public ResponseEntity<Void> createTeamsBulk(
            @Parameter(description = "List of teams to create")
            @Valid @RequestBody List<Team> teams) {

        teamService.createBulk(teams);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
