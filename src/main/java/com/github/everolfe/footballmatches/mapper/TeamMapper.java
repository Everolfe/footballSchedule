package com.github.everolfe.footballmatches.mapper;

import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatches;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatchesAndPlayers;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import com.github.everolfe.footballmatches.model.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;


@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(source = "players", target = "playerDtoList")
    TeamDtoWithPlayers toDtoWithPlayers(Team team);

    @Mapping(source = "matches", target = "matchDtoWithArenaList")
    @Mapping(source = "players", target = "playerDtoList")
    TeamDtoWithMatchesAndPlayers toDtoWithMatchesAndPlayers(Team team);

    @Autowired
    default void setMatchMapper(@Lazy MatchMapper matchMapper) {
        // Пусто, но Spring поймёт
    }

    @Autowired
    default void setPlayerMapper(@Lazy PlayerMapper playerMapper) {
    }
}