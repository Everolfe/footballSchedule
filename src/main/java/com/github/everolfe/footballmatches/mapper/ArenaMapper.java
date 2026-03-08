package com.github.everolfe.footballmatches.mapper;

import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.model.Arena;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {MatchMapper.class})
public interface ArenaMapper {

    ArenaDto toDto(Arena arena);

    ArenaDtoWithMatches toDtoWithMatches(Arena arena);

    // если понадобится обратное преобразование
    Arena toEntity(ArenaDto arenaDto);
}