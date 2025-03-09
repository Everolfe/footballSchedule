package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.service.MatchService;
import java.time.LocalDateTime;
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


@RestController
@RequestMapping("/matches")
@AllArgsConstructor
public class MatchController {

    private final MatchService matchService;


    @PostMapping(value = "/create")
    public ResponseEntity<Void> createMatch(@RequestBody Match match) {
        matchService.create(match);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readAllMatches() {
        final List<MatchDtoWithArenaAndTeams> matches = matchService.readAll();
        return matches != null && !matches.isEmpty()
                ? new ResponseEntity<List<MatchDtoWithArenaAndTeams>>(matches, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readMatchesByTournament(
            @RequestParam(value = "tournament") String tournamentName) {
        final List<MatchDtoWithArenaAndTeams> matches =
                matchService.getMatchesByTournamentName(tournamentName);
        return matches != null && !matches.isEmpty()
                ? new ResponseEntity<List<MatchDtoWithArenaAndTeams>>(matches, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<MatchDtoWithArenaAndTeams> readMatchById(
            @PathVariable final Integer id) throws Exception {
        final MatchDtoWithArenaAndTeams match = matchService.read(id);
        return match != null
                ? new ResponseEntity<MatchDtoWithArenaAndTeams>(match, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updateMatch(@PathVariable final Integer id,
                                            @RequestBody Match match) {
        final boolean updated = matchService.update(match, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PatchMapping(value = "/{matchId}/set-arena")
    public ResponseEntity<Void> setNewArena(
            @PathVariable final Integer matchId,
            @RequestParam(value = "newArenaId") final Integer newArenaId) throws Exception {
        final boolean updated = matchService.setNewArena(matchId, newArenaId);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PatchMapping(value = "/{matchId}/update-time")
    public ResponseEntity<Void> updateMatchTime(
            @PathVariable final Integer matchId,
            @RequestParam(value = "time") final LocalDateTime time) throws Exception {

        boolean updated = matchService.updateMatchTime(matchId, time);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable final Integer id) {
        final boolean deleted = matchService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
