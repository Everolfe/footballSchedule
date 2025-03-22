package com.github.everolfe.footballmatches.service;


import com.github.everolfe.footballmatches.aspect.AspectAnnotaion;
import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class ArenaService {

    private final Cache<String, Object> cache;
    private final ArenaRepository arenaRepository;
    private  final MatchRepository matchRepository;
    private static final String DOESNT_EXIST = "Arena does not exist with ID = ";
    private static final String ARENA_CACHE_PREFIX = "arena_";
    private static final String MATCH_CACHE_PREFIX = "match_";

    @AspectAnnotaion
    public void create(Arena arena) {
        if (arena == null) {
            throw new BadRequestException("Arena is null");
        }
        arenaRepository.save(arena);
        ArenaDto arenaDto = ConvertDtoClasses.convertToArenaDto(arena);
        cache.put(ARENA_CACHE_PREFIX + arenaDto.getId().toString(), arenaDto);
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

    @AspectAnnotaion
    public ArenaDto read(final Integer id) {
        Object cachedArena = cache.get(ARENA_CACHE_PREFIX + id.toString());
        if (cachedArena != null) {
            return (ArenaDto) cachedArena;
        } else {
            ArenaDto arenaDto =  ConvertDtoClasses.convertToArenaDto(arenaRepository.findById(id)
                            .orElseThrow(() -> new ResourcesNotFoundException(DOESNT_EXIST + id)));
            cache.put(ARENA_CACHE_PREFIX + id.toString(), arenaDto);
            return arenaDto;
        }
    }

    @AspectAnnotaion
    public boolean update(Arena arena, final Integer id) {
        if (arenaRepository.existsById(id)) {
            arena.setId(id);
            arenaRepository.save(arena);
            cache.put(ARENA_CACHE_PREFIX + id.toString(), arena);
            return true;
        }
        return false;
    }

    @AspectAnnotaion
    public boolean delete(final Integer id) {
        if (arenaRepository.existsById(id)) {
            Arena arena = arenaRepository.findById(id).orElseThrow(()
                -> new ResourcesNotFoundException(DOESNT_EXIST + id));
            List<Match> matchList = arena.getMatchList();
            if (matchList != null) {
                for (Match match : matchList) {
                    match.setArena(null);
                    cache.put(MATCH_CACHE_PREFIX + match.getId().toString(), match);
                    matchRepository.save(match);
                }
            }
            arenaRepository.deleteById(id);
            cache.remove(ARENA_CACHE_PREFIX + id.toString());
            return true;
        }
        return false;
    }

    public boolean checkValidCapacity(final Integer minCapacity, final Integer maxCapacity) {
        return (minCapacity == null && maxCapacity == null)
                || (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity);
    }

    @AspectAnnotaion
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
