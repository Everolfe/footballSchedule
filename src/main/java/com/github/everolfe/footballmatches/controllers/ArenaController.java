package com.github.everolfe.footballmatches.controllers;

import com.github.everolfe.footballmatches.aspect.CounterAnnotation;
import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.service.ArenaService;
import com.github.everolfe.footballmatches.swagger.ArenaDocumentation;
import com.github.everolfe.footballmatches.swagger.ArenaDocumentation.*;
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


@Tag(name = ArenaDocumentation.TAG_NAME,
    description = ArenaDocumentation.TAG_DESCRIPTION)
@RestController
@RequestMapping("/arenas")
@AllArgsConstructor
public class ArenaController {
    private final ArenaService arenaService;
    private final String NEW_DATA = "New Data";

    @Operation(summary = Create.SUMMARY,
            description = Create.DESCRIPTION)
    @PostMapping("/create")
    public ResponseEntity<Void> createArena(
            @Parameter(description = ArenaDocumentation.ARENA_JSON_DESCRIPTION)
            @Valid @RequestBody final Arena arena) {
        arenaService.create(arena);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = GetAll.SUMMARY,
            description = GetAll.DESCRIPTION)
    @CounterAnnotation
    @GetMapping
    public ResponseEntity<List<ArenaDtoWithMatches>> readAllArenas() {
        final List<ArenaDtoWithMatches> arenas = arenaService.readAll();
        return Handler.handleResponse(arenas, !arenas.isEmpty());
    }

    @Operation(summary = GetByCapacity.SUMMARY,
            description = GetByCapacity.DESCRIPTION)
    @CounterAnnotation
    @GetMapping("/search")
    public ResponseEntity<List<ArenaDto>> readArenasByCapacity(
            @Parameter(description = ArenaDocumentation.MIN_CAPACITY_DESCRIPTION)
            @RequestParam(required = false) Integer minCapacity,
            @Parameter(description = ArenaDocumentation.MAX_CAPACITY_DESCRIPTION)
            @RequestParam(required = false) Integer maxCapacity) {
        final List<ArenaDto> arenas = arenaService.getArenasByCapacity(minCapacity, maxCapacity);
        return Handler.handleResponse(arenas, !arenas.isEmpty());
    }

    @Operation(summary = GetById.SUMMARY,
            description = GetById.DESCRIPTION)
    @CounterAnnotation
    @GetMapping("/{id}")
    public ResponseEntity<ArenaDto> readArenaById(
            @Parameter(description = ArenaDocumentation.ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id)
            throws ResourcesNotFoundException {
        final ArenaDto arena = arenaService.read(id);
        return Handler.handleResponse(arena, arena != null);
    }

    @Operation(summary = Update.SUMMARY,
            description = Update.DESCRIPTION)
    @PutMapping("/update/{id}")
    public ResponseEntity<Void> updateArena(
            @Parameter(description = ArenaDocumentation.ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id,
            @Parameter(description = NEW_DATA)
            @Valid @RequestBody final Arena arena)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, arenaService.update(arena, id));
    }

    @Operation(summary = Delete.SUMMARY,
            description = Delete.DESCRIPTION)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArena(
            @Parameter(description = ArenaDocumentation.ID_DESCRIPTION)
            @PathVariable(name = "id") final Integer id)
            throws ResourcesNotFoundException {
        return Handler.handleResponse(null, arenaService.delete(id));
    }

    @Operation(summary = BulkCreate.SUMMARY,
            description = BulkCreate.DESCRIPTION)
    @PostMapping("/bulk-create")
    public ResponseEntity<Void> createArenasBulk(
            @Parameter(description = ArenaDocumentation.ARENAS_LIST_DESCRIPTION)
            @RequestBody final List<Arena> arenas) {

        arenaService.createBulk(arenas);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
