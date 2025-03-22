package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

@Tag(name = "MatchController",
     description = "You can edit and view information about matches")
@RestController
@RequestMapping("/matches")
@AllArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @Operation(summary = "Creating a match",
            description = "Allows you create a match")
    @PostMapping()
    public ResponseEntity<Void> createMatch(
            @Parameter(description = "JSON object of new match ")
            @RequestBody Match match) {
        matchService.create(match);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "View all matches",
            description = "Allows you to view all matches")
    @GetMapping
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readAllMatches() {
        final List<MatchDtoWithArenaAndTeams> matches = matchService.readAll();
        return matches != null && !matches.isEmpty()
                ? new ResponseEntity<>(matches, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "View all matches by tournament name",
            description = "Allows you to view matches with a given tournament name")
    @GetMapping(value = "/search")
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readMatchesByTournament(
            @Parameter(description = "Tournament name")
            @RequestParam(value = "tournament") String tournamentName) {
        final List<MatchDtoWithArenaAndTeams> matches =
                matchService.getMatchesByTournamentName(tournamentName);
        return matches != null && !matches.isEmpty()
                ? new ResponseEntity<>(matches, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "View all matches by date and time",
            description = "Allows you to view matches with a given date and time")
    @GetMapping(value = "/search/by-dates")
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readMatchesByDateTime(
            @Parameter(description = "Min value of date")
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "Max value of date")
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList =
                matchService.findMatchesByDates(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(matchDtoWithArenaAndTeamsList);
    }

    @Operation(summary = "View a match by ID",
            description = "Allows you to view a match with a given ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<MatchDtoWithArenaAndTeams> readMatchById(
            @Parameter(description = "ID of the match to be found ")
            @PathVariable final Integer id) throws ResourcesNotFoundException {
        final MatchDtoWithArenaAndTeams match = matchService.read(id);
        return match != null
                ? new ResponseEntity<>(match, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Сhange match data",
            description = "Allows you to change match data")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updateMatch(
            @Parameter(description = "ID of the match to be update data")
            @PathVariable final Integer id,
            @Parameter(description = "New data")
            @RequestBody Match match) {
        final boolean updated = matchService.update(match, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Set new arena to match",
            description = "Allows you to change arena in match")
    @PatchMapping(value = "/{matchId}/set-arena")
    public ResponseEntity<Void> setNewArena(
            @Parameter(description = "ID of the match to be update arena")
            @PathVariable final Integer matchId,
            @Parameter(description = "ID of new arena")
            @RequestParam(value = "newArenaId") final Integer newArenaId) throws Exception {
        final boolean updated = matchService.setNewArena(matchId, newArenaId);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Set new date to match",
            description = "Allows you to change date in match")
    @PatchMapping(value = "/{matchId}/update-time")
    public ResponseEntity<Void> updateMatchTime(
            @Parameter(description = "ID of the match to be update date")
            @PathVariable final Integer matchId,
            @Parameter(description = "Value of new date")
            @RequestParam(value = "time") final LocalDateTime time) throws Exception {
        boolean updated = matchService.updateMatchTime(matchId, time);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Delete match",
            description = "Allows you to delete match by it ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "ID of the match to be delete")
            @PathVariable final Integer id) {
        final boolean deleted = matchService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Add team to match",
            description = "Allows you to add team to match")
    @PatchMapping(value = "/{matchId}/add-team")
    public ResponseEntity<Void> addTeamToMatch(
            @Parameter(description = "ID of the match to be update")
            @PathVariable final Integer matchId,
            @Parameter(description = "ID of added team")
            @RequestParam(value = "teamId") final Integer teamId) throws Exception {
        boolean updated = matchService.addTeamToMatch(matchId, teamId);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @Operation(summary = "Remove team from match",
            description = "Allows you to remove team from match")
    @PatchMapping(value = "/{matchId}/remove-team")
    public ResponseEntity<Void> removeTeamFromMatch(
            @Parameter(description = "ID of the match to be update")
            @PathVariable final Integer matchId,
            @Parameter(description = "ID of removing team")
            @RequestParam(value = "teamId") final Integer teamId) throws Exception {
        boolean updated = matchService.removeTeamFromMatch(matchId, teamId);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

}