package com.github.everolfe.footballmatches.controllers.constants;

public final class ArenaConstants {
    private ArenaConstants() {}

    // Tag info
    public static final String TAG_NAME = "Arena Controller";
    public static final String TAG_DESCRIPTION = "Provides operations for managing football arenas";

    // Operation summaries
    public static final String CREATE_SUMMARY = "Create an arena";
    public static final String GET_ALL_SUMMARY = "Get all arenas";
    public static final String GET_BY_CAPACITY_SUMMARY = "Get arenas by capacity range";
    public static final String GET_BY_ID_SUMMARY = "Get arena by ID";
    public static final String UPDATE_SUMMARY = "Update arena";
    public static final String DELETE_SUMMARY = "Delete arena";
    public static final String BULK_CREATE_SUMMARY = "Bulk create arenas";

    // Operation descriptions
    public static final String CREATE_DESCRIPTION = "Creates a new football arena";
    public static final String GET_ALL_DESCRIPTION = "Retrieves all arenas with their matches information";
    public static final String GET_BY_CAPACITY_DESCRIPTION = "Finds arenas within specified capacity range";
    public static final String GET_BY_ID_DESCRIPTION = "Retrieves a specific arena by its identifier";
    public static final String UPDATE_DESCRIPTION = "Updates an existing arena's information";
    public static final String DELETE_DESCRIPTION = "Removes an arena from the system";
    public static final String BULK_CREATE_DESCRIPTION = "Creates multiple arenas in a single operation";

    // Parameter descriptions
    public static final String ID_DESCRIPTION = "Unique identifier of the arena";
    public static final String ARENA_JSON_DESCRIPTION = "JSON representation of arena data";
    public static final String MIN_CAPACITY_DESCRIPTION = "Minimum capacity of arenas to search for";
    public static final String MAX_CAPACITY_DESCRIPTION = "Maximum capacity of arenas to search for";
    public static final String ARENAS_LIST_DESCRIPTION = "List of arenas to create";
}
