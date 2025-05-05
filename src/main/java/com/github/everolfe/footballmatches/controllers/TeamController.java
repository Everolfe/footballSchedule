package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.aspect.CounterAnnotation;
import com.github.everolfe.footballmatches.controllers.constants.TeamConstants;
import com.github.everolfe.footballmatches.controllers.constants.UrlConstants;
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
import org.springframework.web.bind.annotation.CrossOrigin;
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



@Tag(name = TeamConstants.TAG_NAME,
        description = TeamConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(UrlConstants.TEAMS_URL)
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TeamController {

    private final TeamService teamService;

    private static final String NEW_DATA = "New Data";

    @Operation(summary = TeamConstants.CREATE_SUMMARY,
            description = TeamConstants.CREATE_DESCRIPTION)
    @PostMapping(UrlConstants.CREATE_URL)
    public ResponseEntity<Void> createTeam(
            @Parameter(description = TeamConstants.TEAM_JSON_DESCRIPTION)
            @Valid @RequestBody final Team team) {
        teamService.create(team);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = TeamConstants.GET_ALL_SUMMARY,
            description = TeamConstants.GET_ALL_DESCRIPTION)
    @CounterAnnotation
    @GetMapping
    public ResponseEntity<List<TeamDtoWithMatchesAndPlayers>> readAllTeams() {
        final List<TeamDtoWithMatchesAndPlayers> teams = teamService.readAll();
        return Handler.handleResponse(teams, !teams.isEmpty());
    }

    @Operation(summary = TeamConstants.GET_BY_ID_SUMMARY,
            description = TeamConstants.GET_BY_ID_DESCRIPTION)
    @CounterAnnotation
    @GetMapping(UrlConstants.ID_URL)
    public ResponseEntity<TeamDtoWithPlayers> readTeamById(
            @Parameter(description = TeamConstants.TEAM_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id) {
        final TeamDtoWithPlayers team = teamService.read(id);
        return Handler.handleResponse(team,  team != null);
    }

    @Operation(summary = TeamConstants.UPDATE_SUMMARY,
            description = TeamConstants.UPDATE_DESCRIPTION)
    @PutMapping(UrlConstants.ID_URL)
    public ResponseEntity<Void> updateTeam(
            @Parameter(description = TeamConstants.TEAM_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = NEW_DATA)
            @Valid @RequestBody final Team team) {
        return Handler.handleResponse(null, teamService.update(team, id));
    }

    @Operation(summary = TeamConstants.DELETE_SUMMARY,
            description = TeamConstants.DELETE_DESCRIPTION)
    @DeleteMapping(UrlConstants.ID_URL)
    public ResponseEntity<Void> deleteTeam(
            @Parameter(description = TeamConstants.TEAM_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id) {
        return Handler.handleResponse(null, teamService.delete(id));
    }

    @Operation(summary = TeamConstants.GET_BY_COUNTRY_SUMMARY,
            description = TeamConstants.GET_BY_COUNTRY_DESCRIPTION)
    @CounterAnnotation
    @GetMapping(UrlConstants.SEARCH_URL)
    public ResponseEntity<List<TeamDtoWithPlayers>> readTeamsByCountry(
            @Parameter(description = TeamConstants.COUNTRY_DESCRIPTION)
            @RequestParam(value = "country") final String country) {
        final List<TeamDtoWithPlayers> teams = teamService.getTeamsByCountry(country);
        return Handler.handleResponse(teams, !teams.isEmpty());
    }



    @Operation(summary = TeamConstants.REMOVE_PLAYER_SUMMARY,
            description = TeamConstants.REMOVE_PLAYER_DESCRIPTION)
    @PatchMapping(UrlConstants.REMOVE_PLAYER_URL)
    public ResponseEntity<Void> deletePlayerFromTeam(
            @Parameter(description = TeamConstants.TEAM_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = TeamConstants.PLAYER_ID_DESCRIPTION)
            @RequestParam(value = "playerId") final Integer playerId)
            throws ResourcesNotFoundException, BadRequestException {
        return Handler.handleResponse(null, teamService.deletePlayerFromTeam(id, playerId));
    }

    @Operation(summary = TeamConstants.REMOVE_MATCH_SUMMARY,
            description = TeamConstants.REMOVE_MATCH_DESCRIPTION)
    @PatchMapping(UrlConstants.REMOVE_MATCH_URL)
    public ResponseEntity<Void> deleteMatchFromTeam(
            @Parameter(description = TeamConstants.TEAM_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = TeamConstants.MATCH_ID_DESCRIPTION)
            @RequestParam(value = "matchId") final Integer matchId)
            throws ResourcesNotFoundException, BadRequestException {
        return Handler.handleResponse(null, teamService.deleteMatchFromTeam(id, matchId));
    }

    @Operation(summary = TeamConstants.ADD_MATCH_SUMMARY,
            description = TeamConstants.ADD_MATCH_DESCRIPTION)
    @PatchMapping(UrlConstants.ADD_MATCH_URL)
    public ResponseEntity<Void> addMatchToTeam(
            @Parameter(description = TeamConstants.TEAM_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = TeamConstants.MATCH_ID_DESCRIPTION)
            @RequestParam(value = "matchId") final Integer matchId)
            throws ResourcesNotFoundException, BadRequestException {
        return Handler.handleResponse(null, teamService.addMatchToTeam(id, matchId));
    }

    @Operation(summary = TeamConstants.ADD_PLAYER_SUMMARY,
            description = TeamConstants.ADD_PLAYER_DESCRIPTION)
    @PatchMapping(UrlConstants.ADD_PLAYER_URL)
    public ResponseEntity<Void> addPlayerToTeam(
            @Parameter(description = TeamConstants.TEAM_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = TeamConstants.PLAYER_ID_DESCRIPTION)
            @RequestParam(value = "playerId") final Integer playerId)
            throws ResourcesNotFoundException, BadRequestException {
        return Handler.handleResponse(null, teamService.addPlayerToTeam(id, playerId));
    }



    @Operation(summary = TeamConstants.BULK_CREATE_SUMMARY,
            description = TeamConstants.BULK_CREATE_DESCRIPTION)
    @PostMapping(UrlConstants.BULK_CREATE)
    public ResponseEntity<Void> createTeamsBulk(
            @Parameter(description = TeamConstants.TEAMS_LIST_DESCRIPTION)
            @RequestBody final List<Team> teams) {

        teamService.createBulk(teams);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
