package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.aspect.CounterAnnotation;
import com.github.everolfe.footballmatches.controllers.constants.PlayerConstants;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.service.PlayerService;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@Tag(name = PlayerConstants.TAG_NAME,
        description = PlayerConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping(PlayerConstants.PLAYER_CREATE)
@AllArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    private static final String NEW_DATA = "New Data";

    @Operation(summary = PlayerConstants.CREATE_SUMMARY,
            description = PlayerConstants.CREATE_DESCRIPTION)
    @PostMapping("/create")
    public ResponseEntity<Void> createPlayer(
            @Parameter(description = PlayerConstants.PLAYER_JSON_DESCRIPTION)
            @Valid @RequestBody final Player player) {
        playerService.create(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = PlayerConstants.GET_ALL_SUMMARY,
            description = PlayerConstants.GET_ALL_DESCRIPTION)
    @CounterAnnotation
    @GetMapping
    public ResponseEntity<List<PlayerDtoWithTeam>> readAllPlayers() {
        final List<PlayerDtoWithTeam> players = playerService.readAll();
        return Handler.handleResponse(players, !players.isEmpty());
    }

    @Operation(summary = PlayerConstants.GET_BY_ID_SUMMARY,
            description = PlayerConstants.GET_BY_ID_DESCRIPTION)
    @CounterAnnotation
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDto> readPlayerById(
            @Parameter(description = PlayerConstants.PLAYER_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id)
            throws ResourcesNotFoundException {
        final PlayerDto player = playerService.read(id);
        return Handler.handleResponse(player, player != null);
    }

    @Operation(summary = PlayerConstants.UPDATE_SUMMARY,
            description = PlayerConstants.UPDATE_DESCRIPTION)
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updatePlayer(
            @Parameter(description = PlayerConstants.PLAYER_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = NEW_DATA)
            @Valid @RequestBody final Player player)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, playerService.update(player, id));
    }

    @Operation(summary = PlayerConstants.DELETE_SUMMARY,
            description = PlayerConstants.DELETE_DESCRIPTION)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(
            @Parameter(description = PlayerConstants.PLAYER_ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, playerService.delete(id));
    }

    @Operation(summary = PlayerConstants.GET_BY_AGE_SUMMARY,
            description = PlayerConstants.GET_BY_AGE_DESCRIPTION)
    @CounterAnnotation
    @GetMapping("/search")
    public ResponseEntity<List<PlayerDto>> readPlayersByAge(
            @Parameter(description = PlayerConstants.AGE_DESCRIPTION)
            @RequestParam(value = "age") final Integer age) {
        final List<PlayerDto> players = playerService.getPlayersByAge(age);
        return Handler.handleResponse(players, !players.isEmpty());
    }


    @Operation(summary = PlayerConstants.BULK_CREATE_SUMMARY,
            description = PlayerConstants.BULK_CREATE_DESCRIPTION)
    @PostMapping("/bulk-create")
    public ResponseEntity<Void> createPlayersBulk(
            @Parameter(description = PlayerConstants.PLAYERS_LIST_DESCRIPTION)
            @RequestBody final List<Player> players) {

        playerService.createBulk(players);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
