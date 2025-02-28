package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.model.Match;
import com.github.everolfe.footballmatches.model.Team;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import com.github.everolfe.footballmatches.repository.PlayerRepository;
import com.github.everolfe.footballmatches.repository.TeamRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@Transactional
@AllArgsConstructor
public class TeamServiceImpl implements ServiceInterface<Team> {

    private final TeamRepository teamRepository;
    private final MatchRepository matchRepository;
    private final PlayerRepository playerRepository;

    @Override
    public void create(Team team) {
        teamRepository.save(team);
    }

    @Override
    public List<Team> readAll() {
        return teamRepository.findAll();
    }

    @Override
    public Team read(final Integer id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }

    @Override
    public boolean update(Team team, final Integer id) {
        if (teamRepository.existsById(id)) {
            team.setId(id);
            teamRepository.save(team);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final Integer id) {
        if (teamRepository.existsById(id)) {
            teamRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean addMatchToTeam(final Integer id, Match match) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
        if (team.getMatches() == null) {
            team.setMatches(new ArrayList<Match>());
        }
        team.getMatches().add(match);
        teamRepository.save(team);
        return true;
    }

    public List<Team> getTeamsByCountry(final String country) {
        return teamRepository.findByCountryIgnoreCase(country);
    }
}
