package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.service.ArenaService;
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


@Tag(name = "ArenaController",
    description = "You can edit and view information about arenas")
@RestController
@RequestMapping("/arenas")
@AllArgsConstructor
public class ArenaController {
    private final ArenaService arenaService;

    @Operation(summary = "Creating an arena",
            description = "Allows you create an arena")
    @PostMapping(value = "/create")
    public ResponseEntity<Void> createArena(
            @Parameter(description = "JSON object of new arena ")
            @RequestBody Arena arena) {
        arenaService.create(arena);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "View all arenas",
            description = "Allows you to view all arenas")
    @GetMapping
    public ResponseEntity<List<ArenaDtoWithMatches>> readAllArenas() {
        final List<ArenaDtoWithMatches> arenas = arenaService.readAll();
        return arenas != null && !arenas.isEmpty()
                ? new ResponseEntity<List<ArenaDtoWithMatches>>(arenas, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Operation(summary = "View all arenas by capacity",
            description = "Allows you to view arenas with a given capacity")
    @GetMapping(value = "/search")
    public ResponseEntity<List<ArenaDto>> readArenasByCapacity(
            @Parameter(description = "Min value of capacity ")
            @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = "Max value of capacity ")
            @RequestParam(required = false) Integer maxCapacity) {
        final List<ArenaDto> arenas = arenaService.getArenasByCapacity(minCapacity, maxCapacity);
        return arenas != null && !arenas.isEmpty()
                ? new ResponseEntity<List<ArenaDto>>(arenas, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "View an arena by ID",
            description = "Allows you to view an arena with a given ID")
    @GetMapping(value = "/{id}")
    public ResponseEntity<ArenaDto> readArenaById(
            @Parameter(description = "ID of the arena to be found ")
            @PathVariable(name = "id") final Integer id) {
        final ArenaDto arena = arenaService.read(id);
        return arena != null
                ? new ResponseEntity<ArenaDto>(arena, HttpStatus.OK)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Сhange arena data",
            description = "Allows you to change arena data")
    @PutMapping(value = "/update/{id}")
    public ResponseEntity<Void> updateArena(
            @Parameter(description = "ID of the arena to be update data")
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = "New data")
            @RequestBody Arena arena) {
        final boolean updated = arenaService.update(arena, id);
        return updated
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Delete arena",
            description = "Allows you to delete arena by it ID")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteArena(
            @Parameter(description = "ID of the arena to be delete")
            @PathVariable(name = "id") final Integer id) {
        final boolean deleted = arenaService.delete(id);
        return deleted
                ? ResponseEntity.status(HttpStatus.OK).build()
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}
