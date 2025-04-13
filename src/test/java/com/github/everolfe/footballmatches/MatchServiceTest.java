package com.github.everolfe.footballmatches;

import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import com.github.everolfe.footballmatches.service.MatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ArenaRepository arenaRepository;

    @Mock
    private Cache<String, Object> cache;

    @InjectMocks
    private MatchService matchService;

    private Match testMatch;
    private Team testTeam;
    private Arena testArena;
    private MatchDtoWithArenaAndTeams testMatchDto;

    @BeforeEach
    void setUp() {
        testMatch = new Match();
        testMatch.setId(1);
        testMatch.setTournamentName("Champions League");
        testMatch.setDateTime(LocalDateTime.now());

        testArena = new Arena();
        testArena.setId(1);
        testArena.setMatchList(new ArrayList<>());
        testMatch.setArena(testArena);

        testTeam = new Team();
        testTeam.setId(1);
        testTeam.setMatches(new ArrayList<>());
        testMatch.setTeamList(new ArrayList<>(List.of(testTeam)));

        testMatchDto = ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(testMatch);
    }

    @Test
    void testCreateMatch() {
        matchService.create(testMatch);

        verify(matchRepository).save(testMatch);
        verify(cache).put(CacheConstants
                .getMatchCacheKey(testMatch.getId()), testMatch);

        assertThrows(BadRequestException.class,
                () -> matchService.create(null));
    }

    @Test
    void testReadAll() {
        when(matchRepository.findAll()).thenReturn(new ArrayList<>());

        List<MatchDtoWithArenaAndTeams> result = matchService.readAll();

        assertTrue(result.isEmpty());

        when(matchRepository.findAll()).thenReturn(List.of(testMatch));

        List<MatchDtoWithArenaAndTeams> result2 = matchService.readAll();

        assertEquals(1, result2.size());
        assertEquals(testMatchDto, result2.get(0));
    }

    @Test
    void testReadById() {
        when(cache.get(CacheConstants
                .getMatchCacheKey(1))).thenReturn(testMatchDto);

        MatchDtoWithArenaAndTeams result = matchService.read(1);

        assertEquals(testMatchDto, result);
        verify(matchRepository, never()).findById(anyInt());

        when(cache.get(CacheConstants
                .getMatchCacheKey(1))).thenReturn(null);
        when(matchRepository.findById(1))
                .thenReturn(Optional.of(testMatch));

        MatchDtoWithArenaAndTeams result2 = matchService.read(1);
        assertEquals(testMatchDto, result2);
        verify(cache).put(CacheConstants.getMatchCacheKey(1), testMatchDto);

        when(cache.get(CacheConstants.getMatchCacheKey(1)))
                .thenReturn(null);
        when(matchRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourcesNotFoundException.class,
                () -> matchService.read(1));
    }

    @Test
    void testUpdateMatch() {
        Match updatedMatch = new Match();
        updatedMatch.setTournamentName("Updated Tournament");
        updatedMatch.setDateTime(LocalDateTime.now().plusDays(1));

        when(matchRepository.findById(1)).thenReturn(Optional.of(testMatch));

        boolean result = matchService.update(updatedMatch, 1);

        assertTrue(result);
        verify(matchRepository).save(updatedMatch);
        verify(cache).put(CacheConstants.getMatchCacheKey(1), updatedMatch);

        when(matchRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class,
                () -> matchService.update(testMatch, 1));
    }

    @Test
    void testDeleteMatch() {
        when(matchRepository.findById(1)).thenReturn(Optional.of(testMatch));

        boolean result = matchService.delete(1);

        assertTrue(result);
        verify(matchRepository).deleteById(1);
        verify(cache).remove(CacheConstants.getMatchCacheKey(1));
        verify(teamRepository).save(testTeam);
        verify(arenaRepository).save(testArena);

        when(matchRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class,
                () -> matchService.delete(1));
    }

    @Test
    void testAddTeamToMatch() {
        Team newTeam = new Team();
        newTeam.setId(2);
        newTeam.setMatches(new ArrayList<>());

        when(matchRepository.findById(1)).thenReturn(Optional.of(testMatch));
        when(teamRepository.findById(2)).thenReturn(Optional.of(newTeam));

        boolean result = matchService.addTeamToMatch(1, 2);

        assertTrue(result);
        verify(teamRepository).save(newTeam);
        verify(matchRepository).save(testMatch);
        verify(cache).put(CacheConstants.getTeamCacheKey(2), newTeam);
        verify(cache).put(CacheConstants.getMatchCacheKey(1), testMatch);

        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        assertThrows(BadRequestException.class,
                () -> matchService.addTeamToMatch(1, 1));
    }

    @Test
    void testRemoveTeamFromMatch() {
        when(matchRepository.findById(1)).thenReturn(Optional.of(testMatch));
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));

        boolean result = matchService.removeTeamFromMatch(1, 1);

        assertTrue(result);
        verify(teamRepository).save(testTeam);
        verify(matchRepository).save(testMatch);
        verify(cache).put(CacheConstants.getTeamCacheKey(1), testTeam);
        verify(cache).put(CacheConstants.getMatchCacheKey(1), testMatch);

        Team anotherTeam = new Team();
        anotherTeam.setId(3);
        when(teamRepository.findById(3)).thenReturn(Optional.of(anotherTeam));
        assertThrows(BadRequestException.class,
                () -> matchService.removeTeamFromMatch(1, 3));
    }

    @Test
    void testSetNewArena() {
        Arena newArena = new Arena();
        newArena.setId(2);
        newArena.setMatchList(new ArrayList<>());

        when(matchRepository.findById(1)).thenReturn(Optional.of(testMatch));
        when(arenaRepository.findById(2)).thenReturn(Optional.of(newArena));

        boolean result = matchService.setNewArena(1, 2);

        assertTrue(result);
        verify(arenaRepository).save(newArena);
        verify(matchRepository).save(testMatch);
        verify(cache).put(CacheConstants.getArenaCacheKey(2), newArena);
        verify(cache).put(CacheConstants.getMatchCacheKey(1), testMatch);
    }

    @Test
    void testUpdateMatchTime() {
        LocalDateTime newTime = LocalDateTime.now().plusDays(1);

        when(matchRepository.findById(1)).thenReturn(Optional.of(testMatch));

        boolean result = matchService.updateMatchTime(1, newTime);

        assertTrue(result);
        assertEquals(newTime, testMatch.getDateTime());
        verify(matchRepository).save(testMatch);
        verify(cache).put(CacheConstants.getMatchCacheKey(1), testMatch);
    }

    @Test
    void testFindMatchesByDates() {
        LocalDateTime start = LocalDateTime.now().minusDays(2);
        LocalDateTime end = LocalDateTime.now().plusDays(1);

        when(matchRepository.findByDateTimeLessThanEqual(end))
                .thenReturn(List.of(testMatch));
        List<MatchDtoWithArenaAndTeams> result1
                = matchService.findMatchesByDates(null, end);
        assertEquals(1, result1.size());
        assertEquals(testMatchDto, result1.get(0));

        when(matchRepository.findByDateTimeGreaterThanEqual(start))
                .thenReturn(List.of(testMatch));
        List<MatchDtoWithArenaAndTeams> result2 = matchService
                .findMatchesByDates(start, null);
        assertEquals(1, result2.size());
        assertEquals(testMatchDto, result2.get(0));

        when(matchRepository.findByDateTimeBetween(start, end))
                .thenReturn(List.of(testMatch));

        List<MatchDtoWithArenaAndTeams> result
                = matchService.findMatchesByDates(start, end);

        assertEquals(1, result.size());
        assertEquals(testMatchDto, result.get(0));

        List<MatchDtoWithArenaAndTeams> emptyResult = matchService.findMatchesByDates(end, start);
        assertTrue(emptyResult.isEmpty());
    }

    @Test
    void testGetMatchesByTournamentName() {
        when(matchRepository.findByTournamentNameIgnoreCase("Champions League"))
                .thenReturn(List.of(testMatch));

        List<MatchDtoWithArenaAndTeams> result =
                matchService.getMatchesByTournamentName("Champions League");

        assertEquals(1, result.size());
        assertEquals(testMatchDto, result.get(0));
    }

    @Test
    void testCreateBulk() {
        List<Match> matches = List.of(testMatch, testMatch);

        matchService.createBulk(matches);

        verify(matchRepository).saveAll(matches);

        assertThrows(BadRequestException.class, () -> matchService.createBulk(null));
        assertThrows(BadRequestException.class, () -> matchService.createBulk(List.of()));
    }
}