package com.github.everolfe.footballmatches.dto.arena;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Arena")
public class ArenaDto {
    private Integer id;
    private String city;
    private Integer capacity;
}
