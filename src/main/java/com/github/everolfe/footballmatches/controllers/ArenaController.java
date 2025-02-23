package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.service.ArenaServiceImpl;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/arena")
public class ArenaController {
    private final ArenaServiceImpl arenaService;

    @Autowired
    public ArenaController(ArenaServiceImpl arenaService) {
        this.arenaService = arenaService;
    }

    @PostMapping(value = "/create")
    public ResponseEntity<Void> createArena(@RequestBody Arena arena) {
        arenaService.create(arena);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/all")
    public ResponseEntity<List<Arena>> readAllArenas() {
        final List<Arena> arenas = arenaService.readAll();
        return arenas != null && !arenas.isEmpty()
                ? new ResponseEntity<List<Arena>>(arenas, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/search/capacity")
    public ResponseEntity<List<Arena>> readArenasByCapacity(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity) {
        final List<Arena> arenas = arenaService.getArenasByCapacity(minCapacity, maxCapacity);
        return arenas != null && !arenas.isEmpty()
                ? new ResponseEntity<List<Arena>>(arenas, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/search/{id}")
    public ResponseEntity<Arena> readArenaById(@PathVariable(name = "id") int id) {
        final Arena arena = arenaService.read(id);
        return arena != null
                ? new ResponseEntity<Arena>(arena, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updateArena(
            @PathVariable(name = "id") int id, @RequestBody Arena arena) {
        final boolean updated = arenaService.update(arena, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
    }

    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<Void> deleteArena(@PathVariable(name = "id") int id) {
        final boolean deleted = arenaService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
