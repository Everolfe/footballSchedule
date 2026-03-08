package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.aspect.AspectAnnotation;
import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ExceptionMessages;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.exceptions.ValidationUtils;
import com.github.everolfe.footballmatches.mapper.ArenaMapper;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@AllArgsConstructor
public class ArenaService {

    private static final String ID_FIELD = "id";
    private static final String CAPACITY_FIELD = "capacity";
    private static final String CACHE_NAME = "arenas";
    private static final String CACHE_NAME_WITH_MATCHES = "arenasWithMatches";

    private final ArenaRepository arenaRepository;
    private final MatchRepository matchRepository;

    private final ArenaMapper arenaMapper;

    @AspectAnnotation
    @CachePut(value = CACHE_NAME, key = "#result.id")
    public ArenaDto create(Arena arena) {
        if (arena == null) {
            throw new BadRequestException("Arena is null");
        }
        ValidationUtils.validateProperName(arena.getCity());
        ValidationUtils.validateNonNegative(CAPACITY_FIELD, arena.getCapacity());
        arenaRepository.save(arena);
        return arenaMapper.toDto(arena);
    }

    @AspectAnnotation
    @Cacheable(value = CACHE_NAME_WITH_MATCHES)
    @Transactional(readOnly = true)
    public List<ArenaDtoWithMatches> readAll() {
        List<ArenaDtoWithMatches> arenaDtoWithMatches = new ArrayList<>();
        List<Arena> arenas = arenaRepository.findAll();
        if (!arenas.isEmpty()) {
            for (Arena arena : arenas) {
                arenaDtoWithMatches.add(arenaMapper.toDtoWithMatches(arena));
            }
        }
        return arenaDtoWithMatches;
    }

    @AspectAnnotation
    @Cacheable(value = CACHE_NAME, key = "#id")
    @Transactional(readOnly = true)
    public ArenaDto read(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        return arenaMapper.toDto(arenaRepository.findById(id)
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getArenaNotExistMessage(id))));
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME_WITH_MATCHES, allEntries = true),
            @CacheEvict(value = CACHE_NAME, key = "#id")
    })
    @Transactional
    public boolean update(Arena arena, final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        ValidationUtils.validateProperName(arena.getCity());
        ValidationUtils.validateNonNegative(CAPACITY_FIELD, arena.getCapacity());
        return arenaRepository.findById(id)
                .map(existingArena -> {
                    arena.setId(id);
                    arenaRepository.save(arena);
                    return true;
                })
                .orElseThrow(() -> new ResourcesNotFoundException(
                        ExceptionMessages.getArenaNotExistMessage(id)));
    }

    @AspectAnnotation
    @Caching(evict = {
            @CacheEvict(value = CACHE_NAME, key = "#id"),
            @CacheEvict(value = CACHE_NAME_WITH_MATCHES, allEntries = true)
    })
    @Transactional
    public boolean delete(final Integer id) {
        ValidationUtils.validateNonNegative(ID_FIELD, id);
        Optional<Arena> arenaOptional = arenaRepository.findById(id);
        if (arenaOptional.isPresent()) {
            Arena arena = arenaOptional.get();
            List<Match> matchList = arena.getMatchList();
            if (matchList != null) {
                matchList.forEach(match -> {
                    match.setArena(null);
                    matchRepository.save(match);
                });
            }
            arenaRepository.deleteById(id);
            return true;
        } else {
            throw new ResourcesNotFoundException(ExceptionMessages.getArenaNotExistMessage(id));
        }
    }

    public boolean checkValidCapacity(final Integer minCapacity, final Integer maxCapacity) {
        return (minCapacity == null && maxCapacity == null)
                || (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity);
    }

    @AspectAnnotation
    @Cacheable(value = "arenasByCapacity", key = "{#minCapacity, #maxCapacity}")
    @Transactional(readOnly = true)
    public List<ArenaDto> getArenasByCapacity(
            final Integer minCapacity, final Integer maxCapacity) {
        ValidationUtils.validateNonNegative(CAPACITY_FIELD, minCapacity);
        ValidationUtils.validateNonNegative(CAPACITY_FIELD, maxCapacity);
        List<ArenaDto> arenaDto = new ArrayList<>();
        if (checkValidCapacity(minCapacity, maxCapacity)) {
            return arenaDto;
        } else if (minCapacity == null) {
            for (Arena arena : arenaRepository.findByCapacityLessThanEqual(maxCapacity)) {
                arenaDto.add(arenaMapper.toDto(arena));
            }
        } else if (maxCapacity == null) {
            for (Arena arena : arenaRepository.findByCapacityGreaterThanEqual(minCapacity)) {
                arenaDto.add(arenaMapper.toDto(arena));
            }
        } else {
            for (Arena arena : arenaRepository.findByCapacityBetween(minCapacity, maxCapacity)) {
                arenaDto.add(arenaMapper.toDto(arena));
            }
        }
        return arenaDto;
    }

    @AspectAnnotation
    @CacheEvict(value = CACHE_NAME_WITH_MATCHES, allEntries = true)
    @Transactional
    public void createBulk(List<Arena> arenas) {
        if (arenas == null) {
            throw new BadRequestException("Arena list cannot be null");
        }
        List<Arena> validArenas = arenas.stream()
                .filter(Objects::nonNull)
                .map(arena -> {
                    ValidationUtils.validateProperName(arena.getCity());
                    ValidationUtils.validateNonNegative(CAPACITY_FIELD, arena.getCapacity());
                    return arena;
                })
                .toList();
        if (validArenas.isEmpty()) {
            throw new BadRequestException("No valid arenas provided");
        }
        arenaRepository.saveAll(validArenas);
    }
}
