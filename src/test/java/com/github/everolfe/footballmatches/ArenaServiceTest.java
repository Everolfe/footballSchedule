package com.github.everolfe.footballmatches;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.arena.ArenaDto;
import com.github.everolfe.footballmatches.dto.arena.ArenaDtoWithMatches;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.github.everolfe.footballmatches.service.ArenaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ArenaServiceTest {
    @Mock
    private ArenaRepository arenaRepository;

    @Mock
    private Cache<String, Object> cache;

    @InjectMocks
    private ArenaService arenaService;

    private Arena testArena;
    private ArenaDto testArenaDto;
    private ArenaDtoWithMatches testArenaDtoWithMatches;

    @BeforeEach
    void setUp() {
        testArena = new Arena();
        testArena.setId(1);
        testArena.setCity("Test City");
        testArena.setCapacity(50000);

        testArenaDto = ConvertDtoClasses.convertToArenaDto(testArena);
        testArenaDtoWithMatches = ConvertDtoClasses.convertToArenaDtoWithMatches(testArena);
    }

    @Test
    void testCreateArena() {
        arenaService.create(testArena);

        verify(arenaRepository).save(testArena);
        verify(cache).put(CacheConstants.getArenaCacheKey(testArena.getId()), testArenaDto);

        assertThrows(BadRequestException.class, () -> arenaService.create(null));
    }


    @Test
    void testReadAll() {
        when(arenaRepository.findAll()).thenReturn(new ArrayList<>());

        List<ArenaDtoWithMatches> result = arenaService.readAll();

        assertTrue(result.isEmpty());

        List<Arena> arenas = Arrays.asList(testArena);
        when(arenaRepository.findAll()).thenReturn(arenas);

        List<ArenaDtoWithMatches> result2 = arenaService.readAll();

        assertEquals(1, result2.size());
        assertEquals(testArenaDtoWithMatches, result2.get(0));
    }


    @Test
    void testReadById() {
        when(cache.get(CacheConstants.getArenaCacheKey(1))).thenReturn(testArenaDto);

        ArenaDto result = arenaService.read(1);

        assertEquals(testArenaDto, result);
        verify(arenaRepository, never()).findById(anyInt());

        when(cache.get(CacheConstants.getArenaCacheKey(1))).thenReturn(null);
        when(arenaRepository.findById(1)).thenReturn(Optional.of(testArena));

        ArenaDto result2 = arenaService.read(1);
        assertEquals(testArenaDto, result2);
        verify(cache).put(CacheConstants.getArenaCacheKey(1), testArenaDto);

        when(cache.get(CacheConstants.getArenaCacheKey(1))).thenReturn(null);
        when(arenaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourcesNotFoundException.class, () -> arenaService.read(1));
    }


    @Test
    void testUpdateArena() {
        Arena updatedArena = new Arena();
        updatedArena.setCity("Updated City");
        updatedArena.setCapacity(60000);

        when(arenaRepository.findById(1)).thenReturn(Optional.of(testArena));

        boolean result = arenaService.update(updatedArena, 1);

        assertTrue(result);
        verify(arenaRepository).save(updatedArena);
        verify(cache).put(CacheConstants.getArenaCacheKey(1), updatedArena);


        when(arenaRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> arenaService.update(testArena, 1));
    }

    @Test
    void testDeleteArena() {
        when(arenaRepository.findById(1)).thenReturn(Optional.of(testArena));

        boolean result = arenaService.delete(1);

        assertTrue(result);
        verify(arenaRepository).deleteById(1);
        verify(cache).remove(CacheConstants.getArenaCacheKey(1));


        when(arenaRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourcesNotFoundException.class, () -> arenaService.delete(1));
    }

    @Test
    void testGetArenasByCapacity() {
        List<ArenaDto> result = arenaService.getArenasByCapacity(100, 50);
        assertTrue(result.isEmpty());

        when(arenaRepository.findByCapacityLessThanEqual(50000)).thenReturn(Arrays.asList(testArena));
        List<ArenaDto> result2 = arenaService.getArenasByCapacity(null, 50000);
        assertEquals(1, result2.size());
        assertEquals(testArenaDto, result2.get(0));

        when(arenaRepository.findByCapacityGreaterThanEqual(40000)).thenReturn(Arrays.asList(testArena));
        List<ArenaDto> result3 = arenaService.getArenasByCapacity(40000, null);
        assertEquals(1, result3.size());
        assertEquals(testArenaDto, result3.get(0));

        when(arenaRepository.findByCapacityBetween(40000, 60000)).thenReturn(Arrays.asList(testArena));
        List<ArenaDto> result4 = arenaService.getArenasByCapacity(40000, 60000);
        assertEquals(1, result4.size());
        assertEquals(testArenaDto, result4.get(0));

    }

    @Test
    void testCreateBulk() {

        List<Arena> arenas = Arrays.asList(testArena, testArena);
        when(arenaRepository.saveAll(anyList())).thenReturn(arenas);
        arenaService.createBulk(arenas);
        verify(arenaRepository).saveAll(anyList());

        assertThrows(BadRequestException.class, () -> arenaService.createBulk(null));

        List<Arena> arenas2 = Arrays.asList(new Arena(), new Arena());
        assertThrows(BadRequestException.class, () -> arenaService.createBulk(arenas2));
    }
}
