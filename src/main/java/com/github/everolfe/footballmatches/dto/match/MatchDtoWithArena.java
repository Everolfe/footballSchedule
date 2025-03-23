package com.github.everolfe.footballmatches.dto.match;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Data;


@Data
@Schema(description = "Match with only Arena")
public class MatchDtoWithArena {
    private Integer id;

    @NotNull(message = "Date and time cannot be null")
    private LocalDateTime dateTime;

    @NotNull(message = "Tournament name cannot be null")
    @Size(min = 1, max = 100, message = "Tournament name must be between 1 and 100 characters")
    private String tournamentName;

    @Schema(description = "Arena where plays")
    private ArenaDto arenaDto;
}
