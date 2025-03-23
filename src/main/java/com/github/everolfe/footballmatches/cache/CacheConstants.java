package com.github.everolfe.footballmatches.cache;

public final class CacheConstants {
    private static final String ARENA_CACHE_PREFIX = "arena_";
    private static final String MATCH_CACHE_PREFIX = "match_";
    private static final String TEAM_CACHE_PREFIX = "team_";
    private static final String PLAYER_CACHE_PREFIX = "player_";

    public static String getArenaCacheKey(Integer id) {
        return ARENA_CACHE_PREFIX + id;
    }

    public static String getMatchCacheKey(Integer id) {
        return MATCH_CACHE_PREFIX + id;
    }

    public static String getTeamCacheKey(Integer id) {
        return TEAM_CACHE_PREFIX + id;
    }

    public static String getPlayerCacheKey(Integer id) {
        return PLAYER_CACHE_PREFIX + id;
    }
}
