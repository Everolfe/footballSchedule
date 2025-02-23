package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.service.TeamServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/team")
public class TeamController {

    private final TeamServiceImpl teamService;

    @Autowired
    public TeamController(TeamServiceImpl teamService) {
        this.teamService = teamService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createTeam(@RequestBody Team team) {
        teamService.create(team);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Team>> readAllTeams() {
        final List<Team> teams = teamService.readAll();
        return teams != null && !teams.isEmpty()
                ? new ResponseEntity<>(teams, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<Team>> readTeamsByCountry(
            @RequestParam(value = "country") String country) {
        final List<Team> teams = teamService.getTeamsByCountry(country);
        return teams != null && !teams.isEmpty()
                ? new ResponseEntity<>(teams, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping(value = "/search/{id}")
    public ResponseEntity<Team> readTeamById(@PathVariable(name = "id") Integer id) {
        final Team team = teamService.read(id);
        return team != null
                ? new ResponseEntity<>(team, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updateTeam(
            @PathVariable(name = "id") Integer id, @RequestBody Team team) {
        final boolean updated = teamService.update(team, id);
        return updated
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteTeam(@PathVariable(name = "id") Integer id) {
        final boolean deleted = teamService.delete(id);
        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
