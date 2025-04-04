package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.NotExistMessage;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.exceptions.ValidationUtils;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class ArenaService {

    private static final String ID_FIELD = "id";
    private static final String CAPACITY_FIELD = "capacity";

    private final Cache<String, Object> cache;
    private final ArenaRepository arenaRepository;
    private  final MatchRepository matchRepository;

    @AspectAnnotation
    public void create(Arena arena) {
        if (arena == null) {
            throw new BadRequestException("Arena is null");
        }
        ValidationUtils.validateProperName(arena.getCity());
        ValidationUtils.validateNonNegative(CAPACITY_FIELD, arena.getCapacity());
        arenaRepository.save(arena);
        ArenaDto arenaDto = ConvertDtoClasses.convertToArenaDto(arena);
        cache.put(CacheConstants.getArenaCacheKey(arenaDto.getId()), arenaDto);
    }

    @AspectAnnotation
    public List<ArenaDtoWithMatches> readAll() {
        List<ArenaDtoWithMatches> arenaDtoWithMatches = new ArrayList<>();
        List<Arena> arenas = arenaRepository.findAll();
        if (!arenas.isEmpty()) {
            for (Arena arena : arenas) {
                arenaDtoWithMatches.add(ConvertDtoClasses.convertToArenaDtoWithMatches(arena));
            }
        }
        return arenaDtoWithMatches;
    }

    @AspectAnnotation
    public ArenaDto read(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Object cachedArena = cache.get(CacheConstants.getArenaCacheKey(id));
        if (cachedArena != null) {
            return (ArenaDto) cachedArena;
        } else {
            ArenaDto arenaDto =  ConvertDtoClasses.convertToArenaDto(arenaRepository.findById(id)
                            .orElseThrow(() -> new ResourcesNotFoundException(
                                    NotExistMessage.getArenaNotExistMessage(id))));
            cache.put(CacheConstants.getArenaCacheKey(id), arenaDto);
            return arenaDto;
        }
    }

    @AspectAnnotation
    public boolean update(Arena arena, final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        ValidationUtils.validateProperName(arena.getCity());
        ValidationUtils.validateNonNegative(CAPACITY_FIELD, arena.getCapacity());
        return arenaRepository.findById(id)
                .map(existingArena -> {
                    arena.setId(id);
                    arenaRepository.save(arena);
                    cache.put(CacheConstants.getArenaCacheKey(id), arena);
                    return true;
                })
                .orElseThrow(() -> new ResourcesNotFoundException(
                        NotExistMessage.getArenaNotExistMessage(id)));
    }

    @AspectAnnotation
    public boolean delete(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Optional<Arena> arenaOptional = arenaRepository.findById(id);
        if (arenaOptional.isPresent()) {
            Arena arena = arenaOptional.get();
            List<Match> matchList = arena.getMatchList();
            if (matchList != null) {
                matchList.forEach(match -> {
                    match.setArena(null);
                    cache.put(CacheConstants.getMatchCacheKey(match.getId()), match);
                    matchRepository.save(match);
                });
            }
            arenaRepository.deleteById(id);
            cache.remove(CacheConstants.getArenaCacheKey(id));
            return true;
        } else {
            throw new ResourcesNotFoundException(NotExistMessage.getArenaNotExistMessage(id));
        }
    }

    public boolean checkValidCapacity(final Integer minCapacity, final Integer maxCapacity) {
        return (minCapacity == null && maxCapacity == null)
                || (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity);
    }

    @AspectAnnotation
    public List<ArenaDto> getArenasByCapacity(
            final Integer minCapacity, final Integer maxCapacity) {
        ValidationUtils.validateNonNegative(CAPACITY_FIELD, minCapacity);
        ValidationUtils.validateNonNegative(CAPACITY_FIELD, maxCapacity);
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
