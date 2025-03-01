package com.github.everolfe.footballmatches.service;


import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;


@Service
@Transactional
@AllArgsConstructor
public class ArenaService {

    private final ArenaRepository arenaRepository;
    private  final MatchRepository matchRepository;

    public void create(Arena arena) {
        arenaRepository.save(arena);
    }

    public List<ArenaDtoWithMatches> readAll() {
        List<ArenaDtoWithMatches> arenaDtoWithMatches = new ArrayList<>();
        List<Arena> arenas = arenaRepository.findAll();
        if (arenas != null) {
            for (Arena arena : arenas) {
                arenaDtoWithMatches.add(ConvertDtoClasses.convertToArenaDtoWithMatches(arena));
            }
        }
        return arenaDtoWithMatches;
    }

    public ArenaDto read(final Integer id) {
        return ConvertDtoClasses
                .convertToArenaDto(arenaRepository.findById(id)
                        .orElseThrow(() -> new ResourceAccessException("Doesn't exist " + id)));
    }

    public boolean update(Arena arena, final Integer id) {
        if (arenaRepository.existsById(id)) {
            arena.setId(id);
            arenaRepository.save(arena);
            return true;
        }
        return false;
    }


    public boolean delete(final Integer id) {
        if (arenaRepository.existsById(id)) {
            arenaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean checkValidCapacity(final Integer minCapacity, final Integer maxCapacity) {
        return (minCapacity == null && maxCapacity == null)
                || (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity);
    }

    public List<ArenaDto> getArenasByCapacity(
            final Integer minCapacity, final Integer maxCapacity) {
        List<ArenaDto> arenaDto = new ArrayList<>();
        if (checkValidCapacity(minCapacity, maxCapacity)) {
            return arenaDto;
        } else if (minCapacity == null) {
            for (Arena arena : arenaRepository.findByCapacityLessThanEqual(maxCapacity)) {
                arenaDto.add(ConvertDtoClasses.convertToArenaDto(arena));
            }
        } else if (maxCapacity == null) {
            for (Arena arena : arenaRepository.findByCapacityGreaterThanEqual(minCapacity)) {
                arenaDto.add(ConvertDtoClasses.convertToArenaDto(arena));
            }
        } else {
            for (Arena arena : arenaRepository.findByCapacityBetween(minCapacity, maxCapacity)) {
                arenaDto.add(ConvertDtoClasses.convertToArenaDto(arena));
            }
        }
        return arenaDto;

    }
}
