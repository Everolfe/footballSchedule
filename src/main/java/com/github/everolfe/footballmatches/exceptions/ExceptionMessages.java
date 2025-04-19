package com.github.everolfe.footballmatches.exceptions;

public final class ExceptionMessages {
    private static final String DOESNT_EXIST_MESSAGE = " does not exist with ID = ";

    private ExceptionMessages() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static String getArenaNotExistMessage(Integer id) {
        return "Arena" + DOESNT_EXIST_MESSAGE + id;
    }

    public static String getTeamNotExistMessage(Integer id) {
        return "Team" + DOESNT_EXIST_MESSAGE + id;
    }

    public static String getMatchNotExistMessage(Integer id) {
        return "Match" + DOESNT_EXIST_MESSAGE + id;
    }

    public static String getPlayerNotExistMessage(Integer id) {
        return "Player" + DOESNT_EXIST_MESSAGE + id;
    }

}
