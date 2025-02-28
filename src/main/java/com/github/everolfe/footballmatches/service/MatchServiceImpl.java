package com.github.everolfe.footballmatches.service;


import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;




@Service
@Transactional
@AllArgsConstructor
public class MatchServiceImpl implements ServiceInterface<Match> {

    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;
    private final ArenaRepository  arenaRepository;


    @Override
    public void create(Match match) {
        matchRepository.save(match);
    }

    @Override
    public List<Match> readAll() {
        return matchRepository.findAll();
    }

    @Override
    public Match read(final Integer id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found"));
    }

    @Override
    public boolean update(Match match, final Integer id) {
        if (matchRepository.existsById(id)) {
            match.setId(id);
            matchRepository.save(match);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final Integer matchId) {
        if (matchRepository.existsById(matchId)) {
            matchRepository.deleteById(matchId);
            return true;
        }
        return false;
    }

    public List<Match> getMatchesByTournamentName(final String tournamentName) {
        return matchRepository.findByTournamentNameIgnoreCase(tournamentName);
    }
}
