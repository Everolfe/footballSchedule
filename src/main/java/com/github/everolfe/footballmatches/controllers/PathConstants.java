package com.github.everolfe.footballmatches.controllers;

public final class PathConstants {
    public static final String ARENAS_PATH = "/arenas";
    public static final String MATCHES_PATH = "/matches";
    public static final String TEAMS_PATH = "/teams";
    public static final String PLAYERS_PATH = "/players";
    public static final String CREATE_PATH = "/create";
    public static final String SEARCH_PATH = "/search";
    public static final String SEARCH_BY_DATE_PATH = "/search/by-date";
    public static final String ID_PATH = "/{id}";
    public static final String UPDATE_PATH = "/update/{id}";
    public static final String LOGS_PATH = "/logs";
    public static final String SET_ARENA_PATH = "/{matchId}/set-arena";
    public static final String SET_TIME_PATH = "/{matchId}/set-time";
    public static final String ADD_TEAM_PATH = "/{matchId}/add-team";
    public static final String REMOVE_TEAM_PATH = "/{matchId}/remove-team";
    public static final String ADD_PLAYER_PATH = "/{teamId}/add-player";
    public static final String REMOVE_PLAYER_PATH = "/{teamId}/remove-player";
    public static final String REMOVE_MATCH_PATH = "/{teamId}/remove-match";
    public static final String ADD_MATCH_PATH = "/{teamId}/add-match";

}
