package com.github.everolfe.footballmatches.exceptions;

public final class NotExistMessage {
    private static final String ARENA_DOESNT_EXIST = "Arena does not exist with ID = ";
    private static final String TEAM_DOESNT_EXIST = "Team does not exist with ID = ";
    private static final String MATCH_DOESNT_EXIST = "Match does not exist with ID = ";
    private static final String PLAYER_DOESNT_EXIST = "Player does not exist with ID = ";

    private NotExistMessage() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getArenaNotExistMessage(Integer id) { return ARENA_DOESNT_EXIST + id;}

    public static String getTeamNotExistMessage(Integer id) {
        return TEAM_DOESNT_EXIST + id;
    }

    public static String getMatchNotExistMessage(Integer id) {
        return MATCH_DOESNT_EXIST + id;
    }

    public static String getPlayerNotExistMessage(Integer id) {
        return PLAYER_DOESNT_EXIST + id;
    }

}
