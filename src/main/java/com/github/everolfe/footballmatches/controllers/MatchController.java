package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.aspect.CounterAnnotation;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    private <T> ResponseEntity<T> handleResponse(final T body, final boolean condition) {
        return condition
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Creating a match",
            description = "Allow you create a match")
    @PostMapping("/create")
    public ResponseEntity<Void> createMatch(
            @Parameter(description = "JSON object of new match ")
            @Valid @RequestBody Match match) {
        matchService.create(match);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "View all matches",
            description = "Allow you to view all matches")
    @CounterAnnotation
    @GetMapping
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readAllMatches() {
        final List<MatchDtoWithArenaAndTeams> matches = matchService.readAll();
        return handleResponse(matches, !matches.isEmpty());
    }

    @Operation(summary = "View all matches by tournament name",
            description = "Allow you to view matches with a given tournament name")
    @CounterAnnotation
    @GetMapping("/search")
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readMatchesByTournament(
            @Parameter(description = "Tournament name")
            @RequestParam(value = "tournament") String tournamentName) {
        final List<MatchDtoWithArenaAndTeams> matches =
                matchService.getMatchesByTournamentName(tournamentName);
        return handleResponse(matches, !matches.isEmpty());
    }

    @Operation(summary = "View all matches by date and time",
            description = "Allow you to view matches with a given date and time")
    @CounterAnnotation
    @GetMapping("/search/by-date")
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
            description = "Allow you to view a match with a given ID")
    @CounterAnnotation
    @GetMapping("/{id}")
    public ResponseEntity<MatchDtoWithArenaAndTeams> readMatchById(
            @Parameter(description = "ID of the match to be found ")
            @PathVariable final Integer id) throws ResourcesNotFoundException {
        final MatchDtoWithArenaAndTeams match = matchService.read(id);
        return handleResponse(match, match != null);
    }

    @Operation(summary = "Сhange match data",
            description = "Allow you to change match data")
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateMatch(
            @Parameter(description = "ID of the match to be update data")
            @PathVariable final Integer id,
            @Parameter(description = "New data")
            @Valid @RequestBody Match match)
            throws ResourcesNotFoundException {
        return handleResponse(null, matchService.update(match, id));
    }

    @Operation(summary = "Set new arena to match",
            description = "Allow you to change arena in match")
    @PatchMapping("/{matchId}/set-arena")
    public ResponseEntity<Void> setNewArena(
            @Parameter(description = "ID of the match to be update arena")
            @PathVariable final Integer matchId,
            @Parameter(description = "ID of new arena")
            @RequestParam(value = "newArenaId") final Integer newArenaId)
            throws ResourcesNotFoundException {
        return handleResponse(null, matchService.setNewArena(matchId, newArenaId));
    }

    @Operation(summary = "Set new date to match",
            description = "Allow you to change date in match")
    @PatchMapping("/{matchId}/set-time")
    public ResponseEntity<Void> updateMatchTime(
            @Parameter(description = "ID of the match to be update date")
            @PathVariable final Integer matchId,
            @Parameter(description = "Value of new date")
            @RequestParam(value = "time") final LocalDateTime time)
            throws ResourcesNotFoundException {
        return handleResponse(null, matchService.updateMatchTime(matchId, time));
    }

    @Operation(summary = "Delete match",
            description = "Allow you to delete match by it ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = "ID of the match to be delete")
            @PathVariable final Integer id)
           throws ResourcesNotFoundException {
        return handleResponse(null, matchService.delete(id));
    }

    @Operation(summary = "Add team to match",
            description = "Allow you to add team to match")
    @PatchMapping("/{matchId}/add-team")
    public ResponseEntity<Void> addTeamToMatch(
            @Parameter(description = "ID of the match to be update")
            @PathVariable final Integer matchId,
            @Parameter(description = "ID of added team")
            @RequestParam(value = "teamId") final Integer teamId)
            throws ResourcesNotFoundException, BadRequestException {
        return handleResponse(null, matchService.addTeamToMatch(matchId, teamId));
    }

    @Operation(summary = "Remove team from match",
            description = "Allow you to remove team from match")
    @PatchMapping("/{matchId}/remove-team")
    public ResponseEntity<Void> removeTeamFromMatch(
            @Parameter(description = "ID of the match to be update")
            @PathVariable final Integer matchId,
            @Parameter(description = "ID of removing team")
            @RequestParam(value = "teamId") final Integer teamId)
            throws ResourcesNotFoundException, BadRequestException {
        return handleResponse(null, matchService.removeTeamFromMatch(matchId, teamId));
    }

    @Operation(summary = "Bulk create matches",
            description = "Allow you to create multiple matches at once")
    @PostMapping("/bulk-create")
    public ResponseEntity<Void> createMatchesBulk(
            @Parameter(description = "List of matches to create")
            @RequestBody List<Match> matches) {

        matchService.createBulk(matches);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}