package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.aspect.CounterAnnotation;
import com.github.everolfe.footballmatches.controllers.constants.MatchConstants;
import com.github.everolfe.footballmatches.controllers.constants.UrlConstants;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Match;
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

@Tag(name = MatchConstants.TAG_NAME,
     description = MatchConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(UrlConstants.MATCHES_URL)
@AllArgsConstructor
public class MatchController {

    private final MatchService matchService;

    private static final String NEW_DATA = "New Data";

    @Operation(summary = MatchConstants.CREATE_SUMMARY,
            description = MatchConstants.CREATE_DESCRIPTION)
    @PostMapping(UrlConstants.CREATE_URL)
    public ResponseEntity<Void> createMatch(
            @Parameter(description = MatchConstants.MATCH_JSON_DESCRIPTION)
            @Valid @RequestBody final Match match) {
        matchService.create(match);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = MatchConstants.GET_ALL_SUMMARY,
            description = MatchConstants.GET_ALL_DESCRIPTION)
    @CounterAnnotation
    @GetMapping
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readAllMatches() {
        final List<MatchDtoWithArenaAndTeams> matches = matchService.readAll();
        return Handler.handleResponse(matches, !matches.isEmpty());
    }

    @Operation(summary = MatchConstants.GET_BY_ID_SUMMARY,
            description = MatchConstants.GET_BY_ID_DESCRIPTION)
    @CounterAnnotation
    @GetMapping(UrlConstants.ID_URL)
    public ResponseEntity<MatchDtoWithArenaAndTeams> readMatchById(
            @Parameter(description = MatchConstants.MATCH_ID_DESCRIPTION)
            @PathVariable final Integer id) throws ResourcesNotFoundException {
        final MatchDtoWithArenaAndTeams match = matchService.read(id);
        return Handler.handleResponse(match, match != null);
    }

    @Operation(summary = MatchConstants.DELETE_SUMMARY,
            description = MatchConstants.DELETE_DESCRIPTION)
    @DeleteMapping(UrlConstants.ID_URL)
    public ResponseEntity<Void> deleteMatch(
            @Parameter(description = MatchConstants.MATCH_ID_DESCRIPTION)
            @PathVariable final Integer id)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, matchService.delete(id));
    }

    @Operation(summary = MatchConstants.UPDATE_SUMMARY,
            description = MatchConstants.UPDATE_DESCRIPTION)
    @PutMapping(UrlConstants.ID_URL)
    public ResponseEntity<Void> updateMatch(
            @Parameter(description = MatchConstants.MATCH_ID_DESCRIPTION)
            @PathVariable final Integer id,
            @Parameter(description = NEW_DATA)
            @Valid @RequestBody final Match match)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, matchService.update(match, id));
    }

    @Operation(summary = MatchConstants.GET_BY_TOURNAMENT_SUMMARY,
            description = MatchConstants.GET_BY_TOURNAMENT_DESCRIPTION)
    @CounterAnnotation
    @GetMapping(UrlConstants.SEARCH_URL)
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readMatchesByTournament(
            @Parameter(description = MatchConstants.TOURNAMENT_DESCRIPTION)
            @RequestParam(value = "tournament") final String tournamentName) {
        final List<MatchDtoWithArenaAndTeams> matches =
                matchService.getMatchesByTournamentName(tournamentName);
        return Handler.handleResponse(matches, !matches.isEmpty());
    }

    @Operation(summary = MatchConstants.GET_BY_DATE_SUMMARY,
            description = MatchConstants.GET_BY_DATE_DESCRIPTION)
    @CounterAnnotation
    @GetMapping(UrlConstants.SEARCH_BY_DATE_URL)
    public ResponseEntity<List<MatchDtoWithArenaAndTeams>> readMatchesByDateTime(
            @Parameter(description = MatchConstants.START_DATE_DESCRIPTION)
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            final LocalDateTime startDate,
            @Parameter(description = MatchConstants.END_DATE_DESCRIPTION)
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            final LocalDateTime endDate) {
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList =
                matchService.findMatchesByDates(startDate, endDate);
        return ResponseEntity.status(HttpStatus.OK).body(matchDtoWithArenaAndTeamsList);
    }


    @Operation(summary = MatchConstants.SET_ARENA_SUMMARY,
            description = MatchConstants.SET_ARENA_DESCRIPTION)
    @PatchMapping(UrlConstants.SET_ARENA_URL)
    public ResponseEntity<Void> setNewArena(
            @Parameter(description = MatchConstants.MATCH_ID_DESCRIPTION)
            @PathVariable final Integer id,
            @Parameter(description = MatchConstants.ARENA_ID_DESCRIPTION)
            @RequestParam(value = "arenaId") final Integer arenaId)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, matchService.setNewArena(id, arenaId));
    }

    @Operation(summary = MatchConstants.SET_TIME_SUMMARY,
            description = MatchConstants.SET_TIME_DESCRIPTION)
    @PatchMapping(UrlConstants.SET_TIME_URL)
    public ResponseEntity<Void> updateMatchTime(
            @Parameter(description =MatchConstants.MATCH_ID_DESCRIPTION)
            @PathVariable final Integer id,
            @Parameter(description = MatchConstants.TIME_DESCRIPTION)
            @RequestParam(value = "time") final LocalDateTime time)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, matchService.updateMatchTime(id, time));
    }

    @Operation(summary = MatchConstants.ADD_TEAM_SUMMARY,
            description = MatchConstants.ADD_TEAM_DESCRIPTION)
    @PatchMapping(UrlConstants.ADD_TEAM_URL)
    public ResponseEntity<Void> addTeamToMatch(
            @Parameter(description = MatchConstants.MATCH_ID_DESCRIPTION)
            @PathVariable final Integer id,
            @Parameter(description = MatchConstants.TEAM_ID_DESCRIPTION)
            @RequestParam(value = "teamId") final Integer teamId)
            throws ResourcesNotFoundException, BadRequestException {
        return Handler.handleResponse(null, matchService.addTeamToMatch(id, teamId));
    }

    @Operation(summary = MatchConstants.REMOVE_TEAM_SUMMARY,
            description = MatchConstants.REMOVE_TEAM_DESCRIPTION)
    @PatchMapping(UrlConstants.REMOVE_TEAM_URL)
    public ResponseEntity<Void> removeTeamFromMatch(
            @Parameter(description = MatchConstants.MATCH_ID_DESCRIPTION)
            @PathVariable final Integer id,
            @Parameter(description = MatchConstants.TEAM_ID_DESCRIPTION)
            @RequestParam(value = "teamId") final Integer teamId)
            throws ResourcesNotFoundException, BadRequestException {
        return Handler.handleResponse(null, matchService.removeTeamFromMatch(id, teamId));
    }

    @Operation(summary = MatchConstants.BULK_CREATE_SUMMARY,
            description = MatchConstants.BULK_CREATE_DESCRIPTION)
    @PostMapping(UrlConstants.BULK_CREATE)
    public ResponseEntity<Void> createMatchesBulk(
            @Parameter(description = MatchConstants.MATCHES_LIST_DESCRIPTION)
            @RequestBody final List<Match> matches) {
        matchService.createBulk(matches);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}