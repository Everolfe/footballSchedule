package com.github.everolfe.footballmatches.swagger;

public interface MatchDocumentation {
    String TAG_NAME = "Match Controller";
    String TAG_DESCRIPTION = "Provides operations for managing football matches";

    String ID_DESCRIPTION = "Unique identifier of the match";
    String MATCH_JSON_DESCRIPTION = "JSON representation of match data";
    String TOURNAMENT_DESCRIPTION = "Name of the tournament";
    String TEAM_ID_DESCRIPTION = "Unique identifier of the team";
    String ARENA_ID_DESCRIPTION = "Unique identifier of the arena";
    String DATE_TIME_DESCRIPTION = "Date and time in ISO format (yyyy-MM-dd'T'HH:mm:ss)";
    String MATCHES_LIST_DESCRIPTION = "List of matches to create";

    interface Create {
        String SUMMARY = "Create a match";
        String DESCRIPTION = "Creates a new football match";
    }

    interface GetAll {
        String SUMMARY = "Get all matches";
        String DESCRIPTION = "Retrieves all matches with arena and teams information";
    }

    interface GetByTournament {
        String SUMMARY = "Get matches by tournament";
        String DESCRIPTION = "Finds matches for specified tournament";
    }

    interface GetByDate {
        String SUMMARY = "Get matches by date range";
        String DESCRIPTION = "Finds matches within specified date/time range";
    }

    interface GetById {
        String SUMMARY = "Get match by ID";
        String DESCRIPTION = "Retrieves a specific match by its identifier";
    }

    interface Update {
        String SUMMARY = "Update match";
        String DESCRIPTION = "Updates an existing match's information";
    }

    interface SetArena {
        String SUMMARY = "Set arena for match";
        String DESCRIPTION = "Updates the arena for a specific match";
    }

    interface SetTime {
        String SUMMARY = "Set time for match";
        String DESCRIPTION = "Updates the date/time for a specific match";
    }

    interface Delete {
        String SUMMARY = "Delete match";
        String DESCRIPTION = "Removes a match from the system";
    }

    interface AddTeam {
        String SUMMARY = "Add team to match";
        String DESCRIPTION = "Adds a team to an existing match";
    }

    interface RemoveTeam {
        String SUMMARY = "Remove team from match";
        String DESCRIPTION = "Removes a team from an existing match";
    }

    interface BulkCreate {
        String SUMMARY = "Bulk create matches";
        String DESCRIPTION = "Creates multiple matches in a single operation";
    }
}
