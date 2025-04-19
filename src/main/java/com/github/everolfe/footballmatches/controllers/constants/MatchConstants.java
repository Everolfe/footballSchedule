package com.github.everolfe.footballmatches.controllers.constants;

public final class MatchConstants {
    private MatchConstants() {}

    public static final String TAG_NAME = "Match Controller";
    public static final String TAG_DESCRIPTION = "Provides operations for managing football matches";

    public static final String CREATE_SUMMARY = "Create a match";
    public static final String GET_ALL_SUMMARY = "Get all matches";
    public static final String GET_BY_TOURNAMENT_SUMMARY = "Get matches by tournament";
    public static final String GET_BY_DATE_SUMMARY = "Get matches by date range";
    public static final String GET_BY_ID_SUMMARY = "Get match by ID";
    public static final String UPDATE_SUMMARY = "Update match";
    public static final String SET_ARENA_SUMMARY = "Set arena for match";
    public static final String SET_TIME_SUMMARY = "Set time for match";
    public static final String DELETE_SUMMARY = "Delete match";
    public static final String ADD_TEAM_SUMMARY = "Add team to match";
    public static final String REMOVE_TEAM_SUMMARY = "Remove team from match";
    public static final String BULK_CREATE_SUMMARY = "Bulk create matches";

    public static final String CREATE_DESCRIPTION = "Creates a new football match";
    public static final String GET_ALL_DESCRIPTION = "Retrieves all matches with arena and teams information";
    public static final String GET_BY_TOURNAMENT_DESCRIPTION = "Finds matches for specified tournament";
    public static final String GET_BY_DATE_DESCRIPTION = "Finds matches within specified date range";
    public static final String GET_BY_ID_DESCRIPTION = "Retrieves a specific match by its identifier";
    public static final String UPDATE_DESCRIPTION = "Updates an existing match's information";
    public static final String SET_ARENA_DESCRIPTION = "Updates the arena for a specific match";
    public static final String SET_TIME_DESCRIPTION = "Updates the date/time for a specific match";
    public static final String DELETE_DESCRIPTION = "Removes a match from the system";
    public static final String ADD_TEAM_DESCRIPTION = "Adds a team to an existing match";
    public static final String REMOVE_TEAM_DESCRIPTION = "Removes a team from an existing match";
    public static final String BULK_CREATE_DESCRIPTION = "Creates multiple matches in a single operation";

    public static final String MATCH_JSON_DESCRIPTION = "JSON representation of match data";
    public static final String MATCH_ID_DESCRIPTION = "Unique identifier of the match";
    public static final String TOURNAMENT_DESCRIPTION = "Name of the tournament";
    public static final String START_DATE_DESCRIPTION = "Start of date range (inclusive)";
    public static final String END_DATE_DESCRIPTION = "End of date range (inclusive)";
    public static final String ARENA_ID_DESCRIPTION = "Unique identifier of the arena";
    public static final String TIME_DESCRIPTION = "New date and time for the match";
    public static final String TEAM_ID_DESCRIPTION = "Unique identifier of the team";
    public static final String MATCHES_LIST_DESCRIPTION = "List of matches to create";
}
