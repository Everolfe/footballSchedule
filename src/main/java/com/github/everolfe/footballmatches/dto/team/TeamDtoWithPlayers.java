package com.github.everolfe.footballmatches.dto.team;

import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;


@Data
@Schema(description = "Team with only players")
public class TeamDtoWithPlayers {
    private Integer id;

    @NotNull(message = "Team name cannot be null")
    @Size(min = 1, max = 100, message = "Team name must be between 1 and 100 characters")
    private String teamName;

    @NotNull(message = "Country  cannot be null")
    @Size(min = 1, max = 100, message = "Country must be between 1 and 100 characters")
    private String country;

    @Schema(description = "List of players in team")
    private List<PlayerDto> playerDtoList;
}
