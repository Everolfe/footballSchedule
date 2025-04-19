package com.github.everolfe.footballmatches.swagger;

public interface PlayerDocumentation {
    String TAG_NAME = "Player Controller";
    String TAG_DESCRIPTION = "Provides operations for managing football players";

    String ID_DESCRIPTION = "Unique identifier of the player";
    String PLAYER_JSON_DESCRIPTION = "JSON representation of player data";
    String AGE_DESCRIPTION = "Age of the player in years";
    String PLAYERS_LIST_DESCRIPTION = "List of players to create";

    interface Create {
        String SUMMARY = "Create a player";
        String DESCRIPTION = "Creates a new player record in the system";
    }

    interface GetAll {
        String SUMMARY = "Get all players";
        String DESCRIPTION = "Retrieves all players with their team information";
    }

    interface GetById {
        String SUMMARY = "Get player by ID";
        String DESCRIPTION = "Retrieves a specific player by their identifier";
    }

    interface GetByAge {
        String SUMMARY = "Get players by age";
        String DESCRIPTION = "Finds players of a specific age";
    }

    interface Update {
        String SUMMARY = "Update player";
        String DESCRIPTION = "Updates an existing player's information";
    }

    interface Delete {
        String SUMMARY = "Delete player";
        String DESCRIPTION = "Removes a player from the system";
    }

    interface BulkCreate {
        String SUMMARY = "Bulk create players";
        String DESCRIPTION = "Creates multiple players in a single operation";
    }
}
