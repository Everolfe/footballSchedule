package com.github.everolfe.footballmatches.service;


import com.github.everolfe.footballmatches.model.Arena;
import com.github.everolfe.footballmatches.repository.ArenaRepository;
import com.github.everolfe.footballmatches.repository.MatchRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@Transactional
@AllArgsConstructor
public class ArenaServiceImpl implements ServiceInterface<Arena> {

    private final ArenaRepository arenaRepository;

    private  final MatchRepository matchRepository;


    @Override
    public void create(Arena arena) {
        arenaRepository.save(arena);
    }

    @Override
    public List<Arena> readAll() {
        return arenaRepository.findAll();
    }

    @Override
    public Arena read(final Integer id) {
        //return arenaRepository.findById(id)
        //       .orElseThrow(() -> new RuntimeException("Arena not found"));
        return arenaRepository.findById(id).orElse(null);
    }

    @Override
    public boolean update(Arena arena, final Integer id) {
        if (arenaRepository.existsById(id)) {
            arena.setId(id);
            arenaRepository.save(arena);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final Integer id) {
        if (arenaRepository.existsById(id)) {
            arenaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public boolean checkValidCapacity(final Integer minCapacity, final Integer maxCapacity) {
        return (minCapacity == null && maxCapacity == null)
                || (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity);
    }

    public List<Arena> getArenasByCapacity(final Integer minCapacity, final Integer maxCapacity) {
        if (checkValidCapacity(minCapacity, maxCapacity)) {
            return new ArrayList<>();
        } else if (minCapacity == null) {
            return arenaRepository.findByCapacityLessThanEqual(maxCapacity);
        } else if (maxCapacity == null) {
            return arenaRepository.findByCapacityGreaterThanEqual(minCapacity);
        } else {
            return arenaRepository.findByCapacityBetween(minCapacity, maxCapacity);
        }
    }
}
