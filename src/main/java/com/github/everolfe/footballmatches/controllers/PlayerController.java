package com.github.everolfe.footballmatches.controllers;

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


@Tag(name = "PlayerController",
        description = "You can edit and view information about players")
@RestController
@RequestMapping("/players")
@AllArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    private <T> ResponseEntity<T> handleResponse(final T body, final boolean condition) {
        return condition
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Creating a player",
            description = "Allow you create a player")
    @PostMapping("/create")
    public ResponseEntity<Void> createPlayer(
            @Parameter(description = "JSON object of new player ")
            @Valid @RequestBody Player player) {
        playerService.create(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "View all players",
            description = "Allow you to view all players")
    @GetMapping
    public ResponseEntity<List<PlayerDtoWithTeam>> readAllPlayers() {
        final List<PlayerDtoWithTeam> players = playerService.readAll();
        return handleResponse(players, !players.isEmpty());
    }

    @Operation(summary = "View a player by ID",
            description = "Allow you to view a player with a given ID")
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDto> readPlayerById(
            @Parameter(description = "ID of the player to be found ")
            @PathVariable(name = "id") Integer id)
            throws ResourcesNotFoundException {
        final PlayerDto player = playerService.read(id);
        return handleResponse(player, player != null);
    }

    @Operation(summary = "View all matches by age",
            description = "Allow you to view matches by a given age")
    @GetMapping("/search")
    public ResponseEntity<List<PlayerDto>> readPlayersByAge(
            @Parameter(description = "Value of age")
            @RequestParam(value = "age") Integer age) {
        final List<PlayerDto> players = playerService.getPlayersByAge(age);
        return handleResponse(players, !players.isEmpty());
    }

    @Operation(summary = "Сhange player data",
            description = "Allow you to change player data")
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updatePlayer(
            @Parameter(description = "ID of the player to be update data")
            @PathVariable(name = "id") Integer id,
            @Parameter(description = "New data")
            @Valid @RequestBody Player player)
            throws ResourcesNotFoundException {
        return handleResponse(null, playerService.update(player, id));
    }

    @Operation(summary = "Delete player",
            description = "Allow you to delete player by it ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(
            @Parameter(description = "ID of the player to be delete")
            @PathVariable(name = "id") Integer id)
            throws ResourcesNotFoundException {
        return handleResponse(null, playerService.delete(id));
    }

    @Operation(summary = "Bulk create players",
            description = "Allow you to create multiple players at once")
    @PostMapping("/bulk-create")
    public ResponseEntity<Void> createPlayersBulk(
            @Parameter(description = "List of players to create")
            @RequestBody List<Player> players) {

        playerService.createBulk(players);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
