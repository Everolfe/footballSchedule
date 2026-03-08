package com.github.everolfe.footballmatches.mapper;

import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArena;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithTeams;
import com.github.everolfe.footballmatches.model.Match;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;


@Mapper(componentModel = "spring", uses = {ArenaMapper.class})
public interface MatchMapper {

    @Mapping(source = "arena", target = "arenaDto")
    @Mapping(source = "teamList", target = "teamDtoWithPlayersList")
    MatchDtoWithArenaAndTeams toDtoWithArenaAndTeams(Match match);

    @Mapping(source = "teamList", target = "teamDtoWithPlayersList")
    MatchDtoWithTeams toDtoWithTeams(Match match);

    @Autowired
    default void setTeamMapper(@Lazy TeamMapper teamMapper) {
        // Пусто, но Spring поймёт, что нужно создать бин
    }
}