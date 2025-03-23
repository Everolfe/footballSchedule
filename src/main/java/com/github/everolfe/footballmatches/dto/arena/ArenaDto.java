package com.github.everolfe.footballmatches.dto.arena;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Arena")
public class ArenaDto {
    private Integer id;

    @NotNull(message = "City cannot be null")
    @Size(min = 1, max = 100, message = "City name must be between 1 and 100 characters")
    private String city;

    @NotNull(message = "Capacity cannot be null")
    @Min(value = 1, message = "Capacity must be greater than 0")
    private Integer capacity;
}
