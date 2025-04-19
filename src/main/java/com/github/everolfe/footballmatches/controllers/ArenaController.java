package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.aspect.CounterAnnotation;
import com.github.everolfe.footballmatches.controllers.constants.ArenaConstants;
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


@Tag(name = ArenaConstants.TAG_NAME,
    description = ArenaConstants.TAG_DESCRIPTION)
@RestController
@RequestMapping("/arenas")
@AllArgsConstructor
public class ArenaController {
    private final ArenaService arenaService;


    @Operation(summary = ArenaConstants.CREATE_SUMMARY,
            description = ArenaConstants.CREATE_DESCRIPTION)
    @PostMapping("/create")
    public ResponseEntity<Void> createArena(
            @Parameter(description = ArenaConstants.ARENA_JSON_DESCRIPTION)
            @Valid @RequestBody final Arena arena) {
        arenaService.create(arena);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = ArenaConstants.GET_ALL_SUMMARY,
            description = ArenaConstants.GET_ALL_DESCRIPTION)
    @CounterAnnotation
    @GetMapping
    public ResponseEntity<List<ArenaDtoWithMatches>> readAllArenas() {
        final List<ArenaDtoWithMatches> arenas = arenaService.readAll();
        return Handler.handleResponse(arenas, !arenas.isEmpty());
    }

    @Operation(summary = ArenaConstants.GET_BY_CAPACITY_SUMMARY,
            description = ArenaConstants.GET_BY_CAPACITY_DESCRIPTION)
    @CounterAnnotation
    @GetMapping("/search")
    public ResponseEntity<List<ArenaDto>> readArenasByCapacity(
            @Parameter(description = ArenaConstants.MIN_CAPACITY_DESCRIPTION)
            @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = ArenaConstants.MAX_CAPACITY_DESCRIPTION)
            @RequestParam(required = false) Integer maxCapacity) {
        final List<ArenaDto> arenas = arenaService.getArenasByCapacity(minCapacity, maxCapacity);
        return Handler.handleResponse(arenas, !arenas.isEmpty());
    }

    @Operation(summary = ArenaConstants.GET_BY_ID_SUMMARY,
            description = ArenaConstants.GET_BY_ID_DESCRIPTION)
    @CounterAnnotation
    @GetMapping("/{id}")
    public ResponseEntity<ArenaDto> readArenaById(
            @Parameter(description = ArenaConstants.ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id)
            throws ResourcesNotFoundException {
        final ArenaDto arena = arenaService.read(id);
        return Handler.handleResponse(arena, arena != null);
    }

    @Operation(summary = ArenaConstants.UPDATE_SUMMARY,
            description = ArenaConstants.UPDATE_DESCRIPTION)
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateArena(
            @Parameter(description = "ID of the arena to be update data")
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = "New data")
            @Valid @RequestBody final Arena arena)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, arenaService.update(arena, id));
    }

    @Operation(summary = "Delete arena",
            description = "Allow you to delete arena by it ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArena(
            @Parameter(description = "ID of the arena to be delete")
            @PathVariable(name = "id") final Integer id)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, arenaService.delete(id));
    }

    @Operation(summary = "Bulk create arenas",
            description = "Allow you to create multiple arenas at once")
    @PostMapping("/bulk-create")
    public ResponseEntity<Void> createArenasBulk(
            @Parameter(description = "List of arenas to create")
            @RequestBody final List<Arena> arenas) {

        arenaService.createBulk(arenas);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
