package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.service.PlayerServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/player")
public class PlayerController {

    private final PlayerServiceImpl playerService;

    @Autowired
    public PlayerController(PlayerServiceImpl playerService) {
        this.playerService = playerService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<?> createPlayer(@RequestBody Player player) {
        playerService.create(player);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Player>> readAllPlayers() {
        final List<Player> players = playerService.readAll();
        return players != null && !players.isEmpty()
                ? new ResponseEntity<>(players, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<Player>> readPlayersByAge(
            @RequestParam(value = "age") Integer age) {
        final List<Player> players = playerService.getPlayersByAge(age);
        return players != null && !players.isEmpty()
                ? new ResponseEntity<>(players, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<?> updatePlayer(
            @PathVariable(name = "id") Integer id, @RequestBody Player player) {
        final boolean updated = playerService.update(player, id);
        return updated
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_MODIFIED);
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable(name = "id") Integer id) {
        final boolean deleted = playerService.delete(id);
        return deleted
                ? new ResponseEntity<>(HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
