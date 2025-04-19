package com.github.everolfe.footballmatches.controllers.constants;

public final class PlayerConstants {
    private PlayerConstants() {}

    // Tag info
    public static final String TAG_NAME = "Player Controller";
    public static final String TAG_DESCRIPTION = "Provides operations for managing football players";

    // Operation summaries
    public static final String CREATE_SUMMARY = "Create a player";
    public static final String GET_ALL_SUMMARY = "Get all players";
    public static final String GET_BY_ID_SUMMARY = "Get player by ID";
    public static final String GET_BY_AGE_SUMMARY = "Get players by age";
    public static final String UPDATE_SUMMARY = "Update player";
    public static final String DELETE_SUMMARY = "Delete player";
    public static final String BULK_CREATE_SUMMARY = "Bulk create players";

    // Operation descriptions
    public static final String CREATE_DESCRIPTION = "Creates a new football player";
    public static final String GET_ALL_DESCRIPTION = "Retrieves all players with their team information";
    public static final String GET_BY_ID_DESCRIPTION = "Retrieves a specific player by their identifier";
    public static final String GET_BY_AGE_DESCRIPTION = "Finds players of a specific age";
    public static final String UPDATE_DESCRIPTION = "Updates an existing player's information";
    public static final String DELETE_DESCRIPTION = "Removes a player from the system";
    public static final String BULK_CREATE_DESCRIPTION = "Creates multiple players in a single operation";

    // Parameter descriptions
    public static final String PLAYER_JSON_DESCRIPTION = "JSON representation of player data";
    public static final String PLAYER_ID_DESCRIPTION = "Unique identifier of the player";
    public static final String AGE_DESCRIPTION = "Age of the player in years";
    public static final String PLAYERS_LIST_DESCRIPTION = "List of players to create";
}
