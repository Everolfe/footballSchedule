package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatchesAndPlayers;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.service.TeamService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/teams")
@AllArgsConstructor
public class TeamController {

    private final TeamService teamService;


    @PostMapping(value = "/create")
    public ResponseEntity<Void> createTeam(@RequestBody Team team) {
        teamService.create(team);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<TeamDtoWithMatchesAndPlayers>> readAllTeams() {
        final List<TeamDtoWithMatchesAndPlayers> teams = teamService.readAll();
        return teams != null && !teams.isEmpty()
                ? new ResponseEntity<List<TeamDtoWithMatchesAndPlayers>>(teams, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<TeamDtoWithPlayers>> readTeamsByCountry(
            @RequestParam(value = "country") String country) {
        final List<TeamDtoWithPlayers> teams = teamService.getTeamsByCountry(country);
        return teams != null && !teams.isEmpty()
                ? new ResponseEntity<List<TeamDtoWithPlayers>>(teams, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<TeamDtoWithPlayers> readTeamById(@PathVariable(name = "id") Integer id) {
        final TeamDtoWithPlayers team = teamService.read(id);
        return team != null
                ? new ResponseEntity<TeamDtoWithPlayers>(team, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updateTeam(
            @PathVariable(name = "id") Integer id, @RequestBody Team team) {
        final boolean updated = teamService.update(team, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable(name = "id") Integer id) {
        final boolean deleted = teamService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
