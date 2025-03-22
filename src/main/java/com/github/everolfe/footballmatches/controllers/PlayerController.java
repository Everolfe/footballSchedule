package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.service.PlayerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(summary = "Creating a player",
            description = "Allows you create a player")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createPlayer(
            @Parameter(description = "JSON object of new player ")
            @RequestBody Player player) {
        playerService.create(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "View all players",
            description = "Allows you to view all players")
    @GetMapping
    public ResponseEntity<List<PlayerDtoWithTeam>> readAllPlayers() {
        final List<PlayerDtoWithTeam> players = playerService.readAll();
        return players != null && !players.isEmpty()
                ? new ResponseEntity<List<PlayerDtoWithTeam>>(players, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "View a player by ID",
            description = "Allows you to view a player with a given ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<PlayerDto> readPlayerById(
            @Parameter(description = "ID of the player to be found ")
            @PathVariable(name = "id") Integer id) {
        final PlayerDto player = playerService.read(id);
        return  player != null
                ? new ResponseEntity<PlayerDto>(player, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "View all matches by age",
            description = "Allows you to view matches by a given age")
    @GetMapping(value = "/search")
    public ResponseEntity<List<PlayerDto>> readPlayersByAge(
            @Parameter(description = "Value of age")
            @RequestParam(value = "age") Integer age) {
        final List<PlayerDto> players = playerService.getPlayersByAge(age);
        return players != null && !players.isEmpty()
                ? new ResponseEntity<List<PlayerDto>>(players, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Сhange player data",
            description = "Allows you to change player data")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updatePlayer(
            @Parameter(description = "ID of the player to be update data")
            @PathVariable(name = "id") Integer id,
            @Parameter(description = "New data")
            @RequestBody Player player) {
        final boolean updated = playerService.update(player, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Delete player",
            description = "Allows you to delete player by it ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePlayer(
            @Parameter(description = "ID of the player to be delete")
            @PathVariable(name = "id") Integer id) {
        final boolean deleted = playerService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
