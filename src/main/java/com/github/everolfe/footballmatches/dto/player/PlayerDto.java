package com.github.everolfe.footballmatches.dto.player;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Player")
public class PlayerDto {
    private Integer id;
    private String name;
    private Integer age;
    private String country;
}
