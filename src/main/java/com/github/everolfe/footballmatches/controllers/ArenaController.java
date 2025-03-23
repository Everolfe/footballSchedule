package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.service.ArenaService;
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


@Tag(name = "ArenaController",
    description = "You can edit and view information about arenas")
@RestController
@RequestMapping("/arenas")
@AllArgsConstructor
public class ArenaController {
    private final ArenaService arenaService;

    private <T> ResponseEntity<T> handleResponse(final T body, final boolean condition) {
        return condition
                ? ResponseEntity.ok(body)
                : ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Operation(summary = "Creating an arena",
            description = "Allow you create an arena")
    @PostMapping("/create")
    public ResponseEntity<Void> createArena(
            @Parameter(description = "JSON object of new arena ")
            @Valid @RequestBody final Arena arena) {
        arenaService.create(arena);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "View all arenas",
            description = "Allow you to view all arenas")
    @GetMapping
    public ResponseEntity<List<ArenaDtoWithMatches>> readAllArenas() {
        final List<ArenaDtoWithMatches> arenas = arenaService.readAll();
        return handleResponse(arenas, !arenas.isEmpty());
    }

    @Operation(summary = "View all arenas by capacity",
            description = "Allow you to view arenas with a given capacity")
    @GetMapping("/search")
    public ResponseEntity<List<ArenaDto>> readArenasByCapacity(
            @Parameter(description = "Min value of capacity ")
            @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = "Max value of capacity ")
            @RequestParam(required = false) Integer maxCapacity) {
        final List<ArenaDto> arenas = arenaService.getArenasByCapacity(minCapacity, maxCapacity);
        return handleResponse(arenas, !arenas.isEmpty());
    }

    @Operation(summary = "View an arena by ID",
            description = "Allow you to view an arena with a given ID")
    @GetMapping("/{id}")
    public ResponseEntity<ArenaDto> readArenaById(
            @Parameter(description = "ID of the arena to be found ")
            @PathVariable(name = "id") final Integer id)
            throws ResourcesNotFoundException {
        final ArenaDto arena = arenaService.read(id);
        return handleResponse(arena, arena != null);
    }

    @Operation(summary = "Сhange arena data",
            description = "Allow you to change arena data")
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateArena(
            @Parameter(description = "ID of the arena to be update data")
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = "New data")
            @Valid @RequestBody Arena arena)
            throws ResourcesNotFoundException {
        return handleResponse(null, arenaService.update(arena, id));
    }

    @Operation(summary = "Delete arena",
            description = "Allow you to delete arena by it ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArena(
            @Parameter(description = "ID of the arena to be delete")
            @PathVariable(name = "id") final Integer id)
            throws ResourcesNotFoundException {
        return handleResponse(null, arenaService.delete(id));
    }
}
