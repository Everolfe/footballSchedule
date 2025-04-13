package com.github.everolfe.footballmatches;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.player.PlayerDto;
import com.github.everolfe.footballmatches.dto.player.PlayerDtoWithTeam;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.NegativeNumberException;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import com.github.everolfe.footballmatches.service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class PlayerServiceTest {
    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private Cache<String, Object> cache;

    @InjectMocks
    private PlayerService playerService;

    private Player testPlayer;
    private PlayerDto testPlayerDto;
    private PlayerDtoWithTeam testPlayerDtoWithTeam;
    private Team testTeam;

    @BeforeEach
    void setUp() {
        testTeam = new Team();
        testTeam.setId(1);
        testTeam.setTeamName("Test Team");

        testPlayer = new Player();
        testPlayer.setId(1);
        testPlayer.setName("Test Player");
        testPlayer.setAge(25);
        testPlayer.setCountry("Test Country");
        testPlayer.setTeam(testTeam);

        testTeam.setPlayers(new ArrayList<>(Arrays.asList(testPlayer)));

        testPlayerDto = ConvertDtoClasses.convertToPlayerDto(testPlayer);
        testPlayerDtoWithTeam = ConvertDtoClasses.convertToPlayerDtoWithTeam(testPlayer);
    }

    @Test
    void testCreatePlayer() {
        playerService.create(testPlayer);

        verify(playerRepository).save(testPlayer);
        verify(cache).put(CacheConstants.getPlayerCacheKey(testPlayer.getId()), testPlayerDto);

        assertThrows(BadRequestException.class, () -> playerService.create(null));
    }

    @Test
    void testReadAll() {
        when(playerRepository.findAll()).thenReturn(new ArrayList<>());

        List<PlayerDtoWithTeam> result = playerService.readAll();

        assertTrue(result.isEmpty());

        List<Player> players = Arrays.asList(testPlayer);
        when(playerRepository.findAll()).thenReturn(players);

        List<PlayerDtoWithTeam> result2 = playerService.readAll();

        assertEquals(1, result2.size());
        assertEquals(testPlayerDtoWithTeam, result2.get(0));
    }

    @Test
    void testReadById() {
        when(cache.get(CacheConstants.getPlayerCacheKey(1))).thenReturn(testPlayerDto);

        PlayerDto result = playerService.read(1);

        assertEquals(testPlayerDto, result);
        verify(playerRepository, never()).findById(anyInt());

        when(cache.get(CacheConstants.getPlayerCacheKey(1))).thenReturn(null);
        when(playerRepository.findById(1)).thenReturn(Optional.of(testPlayer));

        PlayerDto result2 = playerService.read(1);
        assertEquals(testPlayerDto, result2);
        verify(cache).put(CacheConstants.getPlayerCacheKey(1), testPlayerDto);

        when(cache.get(CacheConstants.getPlayerCacheKey(1))).thenReturn(null);
        when(playerRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourcesNotFoundException.class, () -> playerService.read(1));
    }

    @Test
    void testUpdatePlayer() {
        Player updatedPlayer = new Player();
        updatedPlayer.setName("Updated Player");
        updatedPlayer.setAge(26);
        updatedPlayer.setCountry("Updated Country");

        when(playerRepository.findById(1)).thenReturn(Optional.of(testPlayer));

        boolean result = playerService.update(updatedPlayer, 1);

        assertTrue(result);
        verify(playerRepository).save(updatedPlayer);
        verify(cache).put(CacheConstants.getPlayerCacheKey(1), updatedPlayer);

        when(playerRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> playerService.update(testPlayer, 1));
    }

    @Test
    void testDeletePlayer() {
        when(playerRepository.findById(1)).thenReturn(Optional.of(testPlayer));

        boolean result = playerService.delete(1);

        assertTrue(result);
        verify(playerRepository).deleteById(1);
        verify(teamRepository).save(testTeam);
        verify(cache).remove(CacheConstants.getPlayerCacheKey(1));
        verify(cache).put(CacheConstants.getTeamCacheKey(testTeam.getId()), testTeam);

        when(playerRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> playerService.delete(1));
    }

    @Test
    void testGetPlayersByAge() {
        when(playerRepository.findByAge(25)).thenReturn(new ArrayList<>());

        List<PlayerDto> result = playerService.getPlayersByAge(25);
        assertTrue(result.isEmpty());

        when(playerRepository.findByAge(25)).thenReturn(Arrays.asList(testPlayer));
        List<PlayerDto> result2 = playerService.getPlayersByAge(25);
        assertEquals(1, result2.size());
        assertEquals(testPlayerDto, result2.get(0));

        assertThrows(NegativeNumberException.class, () -> playerService.getPlayersByAge(-1));
    }

    @Test
    void createBulk_ShouldSaveAllValidPlayers() {
        List<Player> players = Arrays.asList(testPlayer, testPlayer);
        when(playerRepository.saveAll(anyList())).thenReturn(players);
        playerService.createBulk(players);
        verify(playerRepository).saveAll(anyList());

        assertThrows(BadRequestException.class, () -> playerService.createBulk(null));

        List<Player> invalidPlayers = Arrays.asList(new Player(), new Player());
        assertThrows(BadRequestException.class, () -> playerService.createBulk(invalidPlayers));
    }
}
