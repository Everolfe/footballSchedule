package com.github.everolfe.footballmatches.dto.player;

import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatches;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Player with team")
public class PlayerDtoWithTeam {
    private Integer id;
    @NotNull(message = "Name cannot be null")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @Min(value = 1, message = "Age must be greater than 0")
    private Integer age;

    @NotNull(message = "Country cannot be null")
    @Size(min = 1, max = 100, message = "Country must be between 1 and 100 characters")
    private String country;

    @Schema(description = "Team where player in")
    private TeamDtoWithMatches teamDtoWithMatches;
}
