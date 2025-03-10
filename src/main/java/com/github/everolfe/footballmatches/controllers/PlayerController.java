package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.service.PlayerService;
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
@RequestMapping("/players")
@AllArgsConstructor
public class PlayerController {

    private final PlayerService playerService;


    @PostMapping(value = "/create")
    public ResponseEntity<Void> createPlayer(@RequestBody Player player) {
        playerService.create(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<PlayerDtoWithTeam>> readAllPlayers() {
        final List<PlayerDtoWithTeam> players = playerService.readAll();
        return players != null && !players.isEmpty()
                ? new ResponseEntity<List<PlayerDtoWithTeam>>(players, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<PlayerDto> readPlayerById(@PathVariable(name = "id") Integer id) {
        final PlayerDto player = playerService.read(id);
        return  player != null
                ? new ResponseEntity<PlayerDto>(player, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<PlayerDto>> readPlayersByAge(
            @RequestParam(value = "age") Integer age) {
        final List<PlayerDto> players = playerService.getPlayersByAge(age);
        return players != null && !players.isEmpty()
                ? new ResponseEntity<List<PlayerDto>>(players, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updatePlayer(
            @PathVariable(name = "id") Integer id, @RequestBody Player player) {
        final boolean updated = playerService.update(player, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable(name = "id") Integer id) {
        final boolean deleted = playerService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
