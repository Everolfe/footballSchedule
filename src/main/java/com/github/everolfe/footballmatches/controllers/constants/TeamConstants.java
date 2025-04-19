package com.github.everolfe.footballmatches.controllers.constants;

public final class TeamConstants {
    private TeamConstants() {}

    public static final String TAG_NAME = "Team Controller";
    public static final String TAG_DESCRIPTION = "Provides operations for managing football teams";

    // Operation summaries
    public static final String CREATE_SUMMARY = "Create a team";
    public static final String GET_ALL_SUMMARY = "Get all teams";
    public static final String GET_BY_COUNTRY_SUMMARY = "Get teams by country";
    public static final String GET_BY_ID_SUMMARY = "Get team by ID";
    public static final String UPDATE_SUMMARY = "Update team";
    public static final String ADD_PLAYER_SUMMARY = "Add player to team";
    public static final String REMOVE_PLAYER_SUMMARY = "Remove player from team";
    public static final String ADD_MATCH_SUMMARY = "Add match to team";
    public static final String REMOVE_MATCH_SUMMARY = "Remove match from team";
    public static final String DELETE_SUMMARY = "Delete team";
    public static final String BULK_CREATE_SUMMARY = "Bulk create teams";

    // Operation descriptions
    public static final String CREATE_DESCRIPTION = "Creates a new football team";
    public static final String GET_ALL_DESCRIPTION = "Retrieves all teams with matches and players information";
    public static final String GET_BY_COUNTRY_DESCRIPTION = "Finds teams from specified country";
    public static final String GET_BY_ID_DESCRIPTION = "Retrieves a specific team by its identifier";
    public static final String UPDATE_DESCRIPTION = "Updates an existing team's information";
    public static final String ADD_PLAYER_DESCRIPTION = "Adds a player to an existing team";
    public static final String REMOVE_PLAYER_DESCRIPTION = "Removes a player from an existing team";
    public static final String ADD_MATCH_DESCRIPTION = "Adds a match to an existing team";
    public static final String REMOVE_MATCH_DESCRIPTION = "Removes a match from an existing team";
    public static final String DELETE_DESCRIPTION = "Removes a team from the system";
    public static final String BULK_CREATE_DESCRIPTION = "Creates multiple teams in a single operation";

    // Parameter descriptions
    public static final String TEAM_JSON_DESCRIPTION = "JSON representation of team data";
    public static final String TEAM_ID_DESCRIPTION = "Unique identifier of the team";
    public static final String COUNTRY_DESCRIPTION = "Country of the team";
    public static final String PLAYER_ID_DESCRIPTION = "Unique identifier of the player";
    public static final String MATCH_ID_DESCRIPTION = "Unique identifier of the match";
    public static final String TEAMS_LIST_DESCRIPTION = "List of teams to create";
}
