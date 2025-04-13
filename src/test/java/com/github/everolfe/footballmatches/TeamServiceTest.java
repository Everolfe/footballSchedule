package com.github.everolfe.footballmatches;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.github.everolfe.footballmatches.cache.Cache;
import com.github.everolfe.footballmatches.cache.CacheConstants;
import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithMatchesAndPlayers;
import com.github.everolfe.footballmatches.dto.team.TeamDtoWithPlayers;
import com.github.everolfe.footballmatches.exceptions.BadRequestException;
import com.github.everolfe.footballmatches.exceptions.InvalidProperNameException;
import com.github.everolfe.footballmatches.exceptions.NegativeNumberException;
import com.github.everolfe.footballmatches.exceptions.ResourcesNotFoundException;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Player;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.everolfe.footballmatches.service.TeamService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @Mock
    private TeamRepository teamRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private Cache<String, Object> cache;

    @InjectMocks
    private TeamService teamService;

    private Team testTeam;
    private TeamDtoWithPlayers testTeamDtoWithPlayers;
    private TeamDtoWithMatchesAndPlayers testTeamDtoWithMatchesAndPlayers;
    private Player testPlayer;
    private Match testMatch;

    @BeforeEach
    void setUp() {
        testTeam = new Team();
        testTeam.setId(1);
        testTeam.setTeamName("Test Team");
        testTeam.setCountry("Test Country");

        testPlayer = new Player();
        testPlayer.setId(1);
        testPlayer.setName("Test Player");
        testPlayer.setTeam(testTeam);

        testMatch = new Match();
        testMatch.setId(1);
        testMatch.setTeamList(new ArrayList<>(Arrays.asList(testTeam)));

        testTeam.setPlayers(new ArrayList<>(Arrays.asList(testPlayer)));
        testTeam.setMatches(new ArrayList<>(Arrays.asList(testMatch)));

        testTeamDtoWithPlayers = ConvertDtoClasses.convertToTeamDtoWithPlayers(testTeam);
        testTeamDtoWithMatchesAndPlayers = ConvertDtoClasses.convertToTeamDtoWithMatchesAndPlayers(testTeam);
    }

    @Test
    void testCreateTeam() {
        teamService.create(testTeam);

        verify(teamRepository).save(testTeam);
        verify(cache).put(CacheConstants.getTeamCacheKey(testTeam.getId()), testTeam);

        assertThrows(BadRequestException.class, () -> teamService.create(null));

        testTeam.setCountry("invalid country 123");
        assertThrows(InvalidProperNameException.class, () -> teamService.create(testTeam));
    }

    @Test
    void testReadAll() {
        when(teamRepository.findAll()).thenReturn(new ArrayList<>());

        List<TeamDtoWithMatchesAndPlayers> result = teamService.readAll();

        assertTrue(result.isEmpty());

        when(teamRepository.findAll()).thenReturn(Arrays.asList(testTeam));

        List<TeamDtoWithMatchesAndPlayers> result2 = teamService.readAll();

        assertEquals(1, result2.size());
        assertEquals(testTeamDtoWithMatchesAndPlayers, result2.get(0));
    }

    @Test
    void testReadById() {
        when(cache.get(CacheConstants.getTeamCacheKey(1))).thenReturn(testTeamDtoWithPlayers);

        TeamDtoWithPlayers result = teamService.read(1);

        assertEquals(testTeamDtoWithPlayers, result);
        verify(teamRepository, never()).findById(anyInt());

        when(cache.get(CacheConstants.getTeamCacheKey(1))).thenReturn(null);
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));

        TeamDtoWithPlayers result2 = teamService.read(1);
        assertEquals(testTeamDtoWithPlayers, result2);
        verify(cache).put(CacheConstants.getTeamCacheKey(1), testTeamDtoWithPlayers);

        when(cache.get(CacheConstants.getTeamCacheKey(1))).thenReturn(null);
        when(teamRepository.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourcesNotFoundException.class, () -> teamService.read(1));

        assertThrows(NegativeNumberException.class, () -> teamService.read(-1));
    }

    @Test
    void testUpdateTeam() {
        Team updatedTeam = new Team();
        updatedTeam.setTeamName("Updated Team");
        updatedTeam.setCountry("Updated Country");

        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));

        boolean result = teamService.update(updatedTeam, 1);

        assertTrue(result);
        verify(teamRepository).save(updatedTeam);
        verify(cache).put(CacheConstants.getTeamCacheKey(1), updatedTeam);

        when(teamRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> teamService.update(testTeam, 1));
        assertThrows(NegativeNumberException.class, () -> teamService.update(testTeam,-1));
    }

    @Test
    void testDeleteTeam() {
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));

        boolean result = teamService.delete(1);

        assertTrue(result);
        verify(teamRepository).deleteById(1);
        verify(playerRepository).save(testPlayer);
        verify(matchRepository).save(testMatch);
        verify(cache).remove(CacheConstants.getTeamCacheKey(1));
        verify(cache).put(CacheConstants.getPlayerCacheKey(testPlayer.getId()), testPlayer);
        verify(cache).put(CacheConstants.getMatchCacheKey(testMatch.getId()), testMatch);

        when(teamRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> teamService.delete(1));
        assertThrows(NegativeNumberException.class, () -> teamService.delete(-1));



    }

    @Test
    void testDeleteTeam_WhenPlayersIsNull() {
        testTeam.setPlayers(null);
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));

        boolean result = teamService.delete(1);
        assertTrue(result);
        verify(matchRepository).save(testMatch); // Проверяем, что работа с матчами все равно выполняется
    }

    @Test
    void testDeleteTeam_WhenMatchesIsNull() {
        testTeam.setMatches(null);
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));

        boolean result = teamService.delete(1);
        assertTrue(result);
        verify(playerRepository).save(testPlayer); // Проверяем, что работа с игроками все равно выполняется
    }

    @Test
    void testDeleteTeam_WhenTeamListInMatchIsNull() {
        testMatch.setTeamList(null);
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));

        boolean result = teamService.delete(1);
        assertTrue(result);
        // Проверяем, что не было попытки сохранить матч с null teamList
        verify(matchRepository, never()).save(testMatch);
    }

    @Test
    void testAddPlayerToTeam() throws Exception {
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.findById(1)).thenReturn(Optional.of(new Player()));

        boolean result = teamService.addPlayerToTeam(1, 1);

        assertTrue(result);
        verify(teamRepository).save(testTeam);
        verify(playerRepository).save(any(Player.class));
        verify(cache).put(CacheConstants.getTeamCacheKey(1), testTeam);

        when(teamRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> teamService.addPlayerToTeam(1, 1));
        assertThrows(ResourcesNotFoundException.class, () -> teamService.addPlayerToTeam(1, 50));

        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.findById(1)).thenReturn(Optional.of(testPlayer));
        assertThrows(BadRequestException.class, () -> teamService.addPlayerToTeam(1, 1));
        assertThrows(NegativeNumberException.class, () -> teamService.addPlayerToTeam(-1,1));
        assertThrows(NegativeNumberException.class, () -> teamService.addPlayerToTeam(1,-1));
        assertThrows(NegativeNumberException.class, () -> teamService.addPlayerToTeam(-1,-1));
    }

    @Test
    void testDeletePlayerFromTeam() throws Exception {
        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.findById(1)).thenReturn(Optional.of(testPlayer));

        boolean result = teamService.deletePlayerFromTeam(1, 1);

        assertTrue(result);
        verify(teamRepository).save(testTeam);
        verify(playerRepository).save(testPlayer);
        verify(cache).put(CacheConstants.getTeamCacheKey(1), testTeam);

        when(teamRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> teamService.deletePlayerFromTeam(1, 1));

        when(teamRepository.findById(1)).thenReturn(Optional.of(testTeam));
        when(playerRepository.findById(2)).thenReturn(Optional.of(new Player()));
        assertThrows(BadRequestException.class, () -> teamService.deletePlayerFromTeam(1, 2));
        assertThrows(NegativeNumberException.class, () -> teamService.deletePlayerFromTeam(1,-1));
        assertThrows(NegativeNumberException.class, () -> teamService.deletePlayerFromTeam(-1,1));
        assertThrows(NegativeNumberException.class, () -> teamService.deletePlayerFromTeam(-1,-1));
    }

    @Test
    void testAddMatchToTeam() throws Exception {
        // Подготовка тестовых данных
        Team team = new Team();
        team.setId(1);

        Match newMatch = new Match();
        newMatch.setId(2);
        newMatch.setTeamList(new ArrayList<>());

        Match existingMatch = new Match();
        existingMatch.setId(3);
        existingMatch.setTeamList(new ArrayList<>(Arrays.asList(team)));

        Match fullMatch = new Match();
        fullMatch.setId(4);
        fullMatch.setTeamList(new ArrayList<>(Arrays.asList(new Team(), new Team())));

        // Тест 1: Успешное добавление команды к матчу
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(matchRepository.findById(2)).thenReturn(Optional.of(newMatch));

        boolean result1 = teamService.addMatchToTeam(1, 2);
        assertTrue(result1);
        verify(matchRepository).save(newMatch);
        verify(cache).put(CacheConstants.getMatchCacheKey(2), newMatch);

        // Тест 2: Команда не найдена
        when(teamRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> teamService.addMatchToTeam(1, 2));

        // Тест 3: Матч не найден
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(matchRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> teamService.addMatchToTeam(1, 2));

        // Тест 4: Команда уже участвует в матче
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(matchRepository.findById(3)).thenReturn(Optional.of(existingMatch));
        assertThrows(BadRequestException.class, () -> teamService.addMatchToTeam(1, 3));

        // Тест 5: В матче уже 2 команды
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(matchRepository.findById(4)).thenReturn(Optional.of(fullMatch));
        assertThrows(BadRequestException.class, () -> teamService.addMatchToTeam(1, 4));

        assertThrows(NegativeNumberException.class, () -> teamService.addMatchToTeam(1,-1));
        assertThrows(NegativeNumberException.class, () -> teamService.addMatchToTeam(-1,1));
        assertThrows(NegativeNumberException.class, () -> teamService.addMatchToTeam(-1,-1));
    }

    @Test
    void testDeleteMatchFromTeam() throws Exception {
        // Подготовка тестовых данных
        Team team = new Team();
        team.setId(1);
        team.setMatches(new ArrayList<>());

        Match match = new Match();
        match.setId(2);
        match.setTeamList(new ArrayList<>(Arrays.asList(team)));
        team.getMatches().add(match);

        Match otherMatch = new Match();
        otherMatch.setId(3);
        otherMatch.setTeamList(new ArrayList<>());

        // Тест 1: Успешное удаление команды из матча
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(matchRepository.findById(2)).thenReturn(Optional.of(match));

        boolean result1 = teamService.deleteMatchFromTeam(1, 2);
        assertTrue(result1);
        verify(matchRepository).save(match);
        verify(teamRepository).save(team);
        verify(cache).put(CacheConstants.getMatchCacheKey(2), match);
        verify(cache).remove(CacheConstants.getTeamCacheKey(1));

        // Тест 2: Команда не найдена
        when(teamRepository.findById(1)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> teamService.deleteMatchFromTeam(1, 2));

        // Тест 3: Матч не найден
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(matchRepository.findById(2)).thenReturn(Optional.empty());
        assertThrows(ResourcesNotFoundException.class, () -> teamService.deleteMatchFromTeam(1, 2));

        // Тест 4: Команда не участвует в матче
        when(teamRepository.findById(1)).thenReturn(Optional.of(team));
        when(matchRepository.findById(3)).thenReturn(Optional.of(otherMatch));
        assertThrows(IllegalArgumentException.class, () -> teamService.deleteMatchFromTeam(1, 3));

        assertThrows(NegativeNumberException.class, () -> teamService.deleteMatchFromTeam(1,-1));
        assertThrows(NegativeNumberException.class, () -> teamService.deleteMatchFromTeam(-1,1));
        assertThrows(NegativeNumberException.class, () -> teamService.deleteMatchFromTeam(-1,-1));
    }

    @Test
    void testGetTeamsByCountry() {
        when(teamRepository.findByCountryIgnoreCase("Test Country")).thenReturn(Arrays.asList(testTeam));

        List<TeamDtoWithPlayers> result = teamService.getTeamsByCountry("Test Country");

        assertEquals(1, result.size());
        assertEquals(testTeamDtoWithPlayers, result.get(0));

        assertThrows(InvalidProperNameException.class, () -> teamService.getTeamsByCountry("invalid country"));
    }

    @Test
    void testCreateBulk() {
        List<Team> teams = Arrays.asList(testTeam, testTeam);
        when(teamRepository.saveAll(anyList())).thenReturn(teams);
        teamService.createBulk(teams);
        verify(teamRepository).saveAll(anyList());

        assertThrows(BadRequestException.class, () -> teamService.createBulk(null));

        List<Team> invalidTeams = Arrays.asList(new Team(), new Team());
        assertThrows(BadRequestException.class, () -> teamService.createBulk(invalidTeams));
    }
}