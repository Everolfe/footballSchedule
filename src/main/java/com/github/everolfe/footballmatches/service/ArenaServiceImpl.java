package com.github.everolfe.footballmatches.service;


import com.github.everolfe.footballmatches.model.Arena;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ArenaServiceImpl implements ServiceInterface<Arena> {

    private static final HashMap<Integer, Arena> ARENA_REPOSITORY_MAP
            = new HashMap<Integer, Arena>();

    private static final AtomicInteger ARENA_ID_HOLDER = new AtomicInteger();

    public ArenaServiceImpl() {
        create(new Arena(1,"Manchester", 53400));
        create(new Arena(2,"Madrid", 81044));
        create(new Arena(3,"Barcelona", 99354));
        create(new Arena(4,"London", 40343));
        create(new Arena(5,"Bayern", 70000));
    }

    @Override
    public void create(Arena obj) {
        final int teamId = ARENA_ID_HOLDER.incrementAndGet();
        obj.setId(teamId);
        ARENA_REPOSITORY_MAP.put(teamId, obj);
    }

    @Override
    public List<Arena> readAll() {
        return new ArrayList<Arena>(ARENA_REPOSITORY_MAP.values());
    }

    @Override
    public Arena read(final Integer id) {
        return ARENA_REPOSITORY_MAP.get(id);
    }

    @Override
    public boolean update(Arena obj, final Integer id) {
        if (ARENA_REPOSITORY_MAP.containsKey(id)) {
            obj.setId(id);
            ARENA_REPOSITORY_MAP.put(id, obj);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final Integer id) {
        return ARENA_REPOSITORY_MAP.remove(id) != null;
    }

    public boolean checkValidCapacity(final Integer minCapacity, final Integer maxCapacity) {
        return (minCapacity == null && maxCapacity == null)
                || (minCapacity != null && maxCapacity != null && minCapacity > maxCapacity);
    }

    public List<Arena> getArenasByCapacity(final Integer minCapacity, final Integer maxCapacity) {
        return ARENA_REPOSITORY_MAP.values().stream()
                .filter(arena ->
                        (minCapacity == null || arena.getCapacity() >= minCapacity)
                                && (maxCapacity == null || arena.getCapacity() <= maxCapacity))
                .toList();
    }
}
