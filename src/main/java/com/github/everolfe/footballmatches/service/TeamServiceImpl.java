package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Team;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;



@Service
public class TeamServiceImpl implements ServiceInterface<Team> {

    private static final Map<Integer, Team> TEAM_REPOSITORY_MAP = new HashMap<Integer, Team>();

    private static final AtomicInteger TEAM_ID_HOLDER = new AtomicInteger();

    public TeamServiceImpl() {
        create(new Team(1,"Barcelona","Spain"));
        create(new Team(2,"Real Madrid","Spain"));
        create(new Team(3,"Chelsea","London"));
        create(new Team(4,"PSG","France"));
        create(new Team(5,"Bayer 04","Germany"));
    }

    @Override
    public void create(Team obj) {
        final int teamId = TEAM_ID_HOLDER.incrementAndGet();
        obj.setId(teamId);
        TEAM_REPOSITORY_MAP.put(teamId, obj);

    }

    @Override
    public List<Team> readAll() {
        return new ArrayList<Team>(TEAM_REPOSITORY_MAP.values());
    }

    @Override
    public Team read(final Integer id) {
        return TEAM_REPOSITORY_MAP.get(id);
    }

    @Override
    public boolean update(Team obj, final Integer id) {
        if (TEAM_REPOSITORY_MAP.containsKey(id)) {
            obj.setId(id);
            TEAM_REPOSITORY_MAP.put(id, obj);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final Integer id) {
        return TEAM_REPOSITORY_MAP.remove(id) != null;
    }

    public boolean addMatchToTeam(final Integer id, Match match) {
        Team team = TEAM_REPOSITORY_MAP.get(id);
        if (team != null) {
            if (team.getMatches() == null) {
                team.setMatches(new ArrayList<Match>());
            }
            team.getMatches().add(match);
            return true;
        }
        return false;
    }

    public List<Team> getTeamsByCountry(final String country) {
        return TEAM_REPOSITORY_MAP.values().stream()
                .filter(team -> team.getCountry() != null && team.getCountry()
                        .equalsIgnoreCase(country)).collect(Collectors.toList());
    }
}
