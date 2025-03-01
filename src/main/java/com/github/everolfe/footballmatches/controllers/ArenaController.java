package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.service.ArenaService;
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
@RequestMapping("/arenas")
@AllArgsConstructor
public class ArenaController {
    private final ArenaService arenaService;

    @PostMapping(value = "/create")
    public ResponseEntity<Void> createArena(@RequestBody Arena arena) {
        arenaService.create(arena);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<ArenaDtoWithMatches>> readAllArenas() {
        final List<ArenaDtoWithMatches> arenas = arenaService.readAll();
        return arenas != null && !arenas.isEmpty()
                ? new ResponseEntity<List<ArenaDtoWithMatches>>(arenas, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping(value = "/search")
    public ResponseEntity<List<ArenaDto>> readArenasByCapacity(
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity) {
        final List<ArenaDto> arenas = arenaService.getArenasByCapacity(minCapacity, maxCapacity);
        return arenas != null && !arenas.isEmpty()
                ? new ResponseEntity<List<ArenaDto>>(arenas, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<ArenaDto> readArenaById(@PathVariable(name = "id") int id) {
        final ArenaDto arena = arenaService.read(id);
        return arena != null
                ? new ResponseEntity<ArenaDto>(arena, HttpStatus.OK)
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
