package com.github.everolfe.footballmatches.mapper;

import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.model.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

@Mapper(componentModel = "spring")
public interface PlayerMapper {


    PlayerDto toDto(Player player);

    @Mapping(source = "team", target = "teamDtoWithMatches")
    PlayerDtoWithTeam toDtoWithTeam(Player player);

    @Autowired
    default void setTeamMapper(@Lazy TeamMapper teamMapper) {
    }
}