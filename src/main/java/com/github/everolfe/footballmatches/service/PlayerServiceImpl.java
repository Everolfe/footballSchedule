package com.github.everolfe.footballmatches.service;

import com.github.everolfe.footballmatches.model.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;


@Service
public class PlayerServiceImpl implements ServiceInterface<Player> {
    private static final HashMap<Integer, Player> PLAYER_REPOSITORY_MAP
            = new HashMap<Integer, Player>();

    private static final AtomicInteger PLAYER_ID_HOLDER = new AtomicInteger();

    public PlayerServiceImpl() {
        create(new Player(1, "Lionel Messi", 35, "Argentina"));
        create(new Player(2, "Erling Haaland", 24, "Norway"));
        create(new Player(3, "Antony", 24, "Brazil"));
        create(new Player(4, "Kai Havertz", 25, "Germany"));
        create(new Player(5, "Frenkie de Jong", 27, "Netherlands"));
    }

    @Override
    public void create(Player obj) {
        final int teamId = PLAYER_ID_HOLDER.incrementAndGet();
        obj.setId(teamId);
        PLAYER_REPOSITORY_MAP.put(teamId, obj);
    }

    @Override
    public List<Player> readAll() {
        return new ArrayList<Player>(PLAYER_REPOSITORY_MAP.values());
    }

    @Override
    public Player read(final Integer id) {
        return PLAYER_REPOSITORY_MAP.get(id);
    }

    @Override
    public boolean update(Player obj, final Integer id) {
        if (PLAYER_REPOSITORY_MAP.containsKey(id)) {
            obj.setId(id);
            PLAYER_REPOSITORY_MAP.put(id, obj);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(final Integer id) {
        return PLAYER_REPOSITORY_MAP.remove(id) != null;
    }

    public List<Player> getPlayersByAge(final Integer age) {
        return PLAYER_REPOSITORY_MAP.values().stream()
                .filter(player -> player.getAge() != null && player.getAge()
                        .equals(age)).toList();
    }

}
