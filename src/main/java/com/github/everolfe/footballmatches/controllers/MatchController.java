package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.service.MatchServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/match")
public class MatchController {

    private final MatchServiceImpl matchService;

    @Autowired
    public MatchController(MatchServiceImpl matchService) {
        this.matchService = matchService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Void> createMatch(@RequestBody Match match) {
        matchService.create(match);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Match>> readAllMatches() {
        final List<Match> matches = matchService.readAll();
        return matches != null && !matches.isEmpty()
                ? new ResponseEntity<List<Match>>(matches, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<Match>> readMatchesByTournament(
            @RequestParam String tournamentName) {
        final List<Match> matches = matchService.getMatchesByTournamentName(tournamentName);
        return matches != null && !matches.isEmpty()
                ? new ResponseEntity<List<Match>>(matches, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/search/{id}")
    public ResponseEntity<Match> readMatchById(@PathVariable int id) {
        final Match match = matchService.read(id);
        return match != null
                ? new ResponseEntity<Match>(match, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updateMatch(@PathVariable int id, @RequestBody Match match) {
        final boolean updated = matchService.update(match, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable int id) {
        final boolean deleted = matchService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
