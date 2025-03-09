package com.github.everolfe.footballmatches.service;


import com.github.everolfe.footballmatches.dto.ConvertDtoClasses;
import com.github.everolfe.footballmatches.dto.match.MatchDtoWithArenaAndTeams;
import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;




@Service
@Transactional
@AllArgsConstructor
public class MatchService  {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final ArenaRepository  arenaRepository;
    private static final String DOESNT_EXIST = "Match does not exist with ID = ";

    public void create(Match match) {
        matchRepository.save(match);
    }

    public List<MatchDtoWithArenaAndTeams> readAll() {
        List<Match> matches = matchRepository.findAll();
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList = new ArrayList<>();
        if (matches != null) {
            for (Match match : matches) {
                matchDtoWithArenaAndTeamsList
                        .add(ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
            }
        }
        return matchDtoWithArenaAndTeamsList;
    }

    public MatchDtoWithArenaAndTeams read(final Integer id) throws ResourceNotFoundException {
        return ConvertDtoClasses
                .convertToMatchDtoWithArenaAndTeams(matchRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + id)));

    }

    public boolean update(Match match, final Integer id) {
        if (matchRepository.existsById(id)) {
            match.setId(id);
            matchRepository.save(match);
            return true;
        }
        return false;
    }

    public boolean delete(final Integer matchId) {
        if (matchRepository.existsById(matchId)) {
            matchRepository.deleteById(matchId);
            return true;
        }
        return false;
    }

    public boolean setNewArena(final Integer matchId, final Integer arenaId) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + matchId));
        Arena newArena = arenaRepository.findById(arenaId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + arenaId));
        match.setArena(newArena);
        matchRepository.save(match);
        return true;
    }

    public boolean updateMatchTime(
            final Integer matchId, final LocalDateTime time) throws Exception {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException(DOESNT_EXIST + matchId));
        match.setDateTime(time);
        matchRepository.save(match);
        return true;
    }

    public List<MatchDtoWithArenaAndTeams> getMatchesByTournamentName(final String tournamentName) {
        List<MatchDtoWithArenaAndTeams> matchDtoWithArenaAndTeamsList = new ArrayList<>();
        for (Match match : matchRepository.findByTournamentNameIgnoreCase(tournamentName)) {
            matchDtoWithArenaAndTeamsList
                    .add(ConvertDtoClasses.convertToMatchDtoWithArenaAndTeams(match));
        }
        return matchDtoWithArenaAndTeamsList;
    }
}
