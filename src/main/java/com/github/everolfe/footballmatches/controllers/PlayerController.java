package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.service.PlayerServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/players")
public class PlayerController {

    private final PlayerServiceImpl playerService;

    @Autowired
    public PlayerController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Void> createPlayer(@RequestBody Player player) {
        playerService.create(player);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<Player>> readAllPlayers() {
        final List<Player> players = playerService.readAll();
        return players != null && !players.isEmpty()
                ? new ResponseEntity<List<Player>>(players, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Player> readPlayerById(@PathVariable(name = "id") Integer id) {
        final Player player = playerService.read(id);
        return  player != null
                ? new ResponseEntity<Player>(player, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<Player>> readPlayersByAge(
            @RequestParam(value = "age") Integer age) {
        final List<Player> players = playerService.getPlayersByAge(age);
        return players != null && !players.isEmpty()
                ? new ResponseEntity<List<Player>>(players, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updatePlayer(
            @PathVariable(name = "id") Integer id, @RequestBody Player player) {
        final boolean updated = playerService.update(player, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable(name = "id") Integer id) {
        final boolean deleted = playerService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
