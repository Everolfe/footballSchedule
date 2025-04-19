package com.github.everolfe.footballmatches.swagger;

public interface ArenaDocumentation {
    String TAG_NAME = "Arena Controller";
    String TAG_DESCRIPTION = "Provides operations for managing football arenas";

    String ID_DESCRIPTION = "Unique identifier of the arena";
    String ARENA_JSON_DESCRIPTION = "JSON representation of arena data";
    String MIN_CAPACITY_DESCRIPTION = "Minimum capacity of arenas to search for";
    String MAX_CAPACITY_DESCRIPTION = "Maximum capacity of arenas to search for";
    String ARENAS_LIST_DESCRIPTION = "List of arenas to create";

    interface Create {
        String SUMMARY = "Create an arena";
        String DESCRIPTION = "Creates a new football arena";
    }

    interface GetAll {
        String SUMMARY = "Get all arenas";
        String DESCRIPTION = "Retrieves all arenas with their matches information";
    }

    interface GetByCapacity {
        String SUMMARY = "Get arenas by capacity range";
        String DESCRIPTION = "Finds arenas within specified capacity range";
    }

    interface GetById {
        String SUMMARY = "Get arena by ID";
        String DESCRIPTION = "Retrieves a specific arena by its identifier";
    }

    interface Update {
        String SUMMARY = "Update arena";
        String DESCRIPTION = "Updates an existing arena's information";
    }

    interface Delete {
        String SUMMARY = "Delete arena";
        String DESCRIPTION = "Removes an arena from the system";
    }

    interface BulkCreate {
        String SUMMARY = "Bulk create arenas";
        String DESCRIPTION = "Creates multiple arenas in a single operation";
    }
}