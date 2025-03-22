package com.github.everolfe.footballmatches.dto.match;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Data;


@Data
@Schema(description = "Match with only Arena")
public class MatchDtoWithArena {
    private Integer id;
    private LocalDateTime dateTime;
    private String tournamentName;
    @Schema(description = "Arena where plays")
    private ArenaDto arenaDto;
}
