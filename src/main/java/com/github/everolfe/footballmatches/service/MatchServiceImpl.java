package com.github.everolfe.footballmatches.service;


import com.github.everolfe.footballmatches.model.Match;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;




@Service
public class MatchServiceImpl implements ServiceInterface<Match> {

    private static final Map<Integer, Match> MATCH_REPOSITORY_MAP = new HashMap<Integer, Match>();

    private static final AtomicInteger MATCH_ID_HOLDER = new AtomicInteger();

    public MatchServiceImpl() {
        create(new Match(1,
                LocalDateTime.of(2025, 2, 15, 17, 30),
                "Premier League"));
        create(new Match(2, LocalDateTime.of(2025, 2, 17, 20, 10),
                "La Liga"));
        create(new Match(3,LocalDateTime.of(2025, 3, 18, 10, 30),
                "Champions League"));
        create(new Match(4,LocalDateTime.of(2025, 3, 8, 12, 50),
                "Championship"));
        create(new Match(5, LocalDateTime.of(2025, 3, 15, 13, 0),
                "Bundesliga"));
    }

    @Override
    public void create(Match match) {
        final int matchId = MATCH_ID_HOLDER.incrementAndGet();
        match.setId(matchId);
        MATCH_REPOSITORY_MAP.put(matchId, match);
    }

    @Override
    public List<Match> readAll() {
        return new ArrayList<Match>(MATCH_REPOSITORY_MAP.values());
    }

    @Override
    public Match read(final Integer matchId) {
        return MATCH_REPOSITORY_MAP.get(matchId);
    }

    @Override
    public boolean update(Match match, final Integer matchId) {
        if (MATCH_REPOSITORY_MAP.containsKey(matchId)) {
            match.setId(matchId);
            MATCH_REPOSITORY_MAP.put(matchId, match);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final Integer matchId) {
        return MATCH_REPOSITORY_MAP.remove(matchId) != null;
    }

    public List<Match> getMatchesByTournamentName(final String tournamentName) {
        return MATCH_REPOSITORY_MAP.values().stream()
                .filter(match -> match.getTournamentName() != null
                        && match.getTournamentName().equalsIgnoreCase(tournamentName))
                .toList();
    }
}
