package com.github.everolfe.footballmatches.controllers.constants;

public final class UrlConstants {
    private UrlConstants() {}


    public static final String PLAYERS_URL = "/players";
    public static final String TEAMS_URL = "/teams";
    public static final String MATCHES_URL = "/matches";
    public static final String ARENAS_URL = "/arenas";

    public static final String BULK_CREATE ="/bulk-create";
    public static final String CREATE_URL = "/create";
    public static final String SEARCH_URL = "/search";
    public static final String ID_URL = "/{id}";

    public static final String SEARCH_BY_DATE_URL = SEARCH_URL + "/by-date";

    public static final String SET_ARENA_URL = ID_URL + "/set-arena";
    public static final String SET_TIME_URL = ID_URL + "/set-time";

    public static final String ADD_TEAM_URL = ID_URL + "/add-team";
    public static final String REMOVE_TEAM_URL = ID_URL + "/remove-team";
    public static final String ADD_MATCH_URL = ID_URL + "/add-matche";
    public static final String REMOVE_MATCH_URL = ID_URL + "/remove-matche";
    public static final String ADD_PLAYER_URL = ID_URL + "/add-player";
    public static final String REMOVE_PLAYER_URL = ID_URL + "/remove-player";
}
