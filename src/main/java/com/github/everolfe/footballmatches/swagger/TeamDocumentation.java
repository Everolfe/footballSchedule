package com.github.everolfe.footballmatches.swagger;

public interface TeamDocumentation {
    String TAG_NAME = "Team Controller";
    String TAG_DESCRIPTION = "Provides operations for managing football teams";

    String ID_DESCRIPTION = "Unique identifier of the team";
    String TEAM_JSON_DESCRIPTION = "JSON representation of team data";
    String COUNTRY_DESCRIPTION = "Country of the team";
    String PLAYER_ID_DESCRIPTION = "Unique identifier of the player";
    String MATCH_ID_DESCRIPTION = "Unique identifier of the match";
    String TEAMS_LIST_DESCRIPTION = "List of teams to create";

    interface Create {
        String SUMMARY = "Create a team";
        String DESCRIPTION = "Creates a new football team";
    }

    interface GetAll {
        String SUMMARY = "Get all teams";
        String DESCRIPTION = "Retrieves all teams with matches and players information";
    }

    interface GetByCountry {
        String SUMMARY = "Get teams by country";
        String DESCRIPTION = "Finds teams from specified country";
    }

    interface GetById {
        String SUMMARY = "Get team by ID";
        String DESCRIPTION = "Retrieves a specific team by its identifier";
    }

    interface Update {
        String SUMMARY = "Update team";
        String DESCRIPTION = "Updates an existing team's information";
    }

    interface AddPlayer {
        String SUMMARY = "Add player to team";
        String DESCRIPTION = "Adds a player to an existing team";
    }

    interface RemovePlayer {
        String SUMMARY = "Remove player from team";
        String DESCRIPTION = "Removes a player from an existing team";
    }

    interface AddMatch {
        String SUMMARY = "Add match to team";
        String DESCRIPTION = "Adds a match to an existing team";
    }

    interface RemoveMatch {
        String SUMMARY = "Remove match from team";
        String DESCRIPTION = "Removes a match from an existing team";
    }

    interface Delete {
        String SUMMARY = "Delete team";
        String DESCRIPTION = "Removes a team from the system";
    }

    interface BulkCreate {
        String SUMMARY = "Bulk create teams";
        String DESCRIPTION = "Creates multiple teams in a single operation";
    }
}