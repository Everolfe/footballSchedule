import api from './api';

// Основные CRUD операции
export const createMatch = (matchData) => api.post('/matches/create', matchData);
export const getAllMatches = () => api.get('/matches');
export const updateMatch = (id, matchData) => api.put(`/matches/${id}`, matchData);
export const deleteMatch = (id) => api.delete(`/matches/${id}`);

// Блок операций с ареной
export const setMatchArena = (matchId, arenaId) =>
    api.patch(`/matches/${matchId}/set-arena?arenaId=${arenaId}`);

// Блок операций со временем
export const updateMatchTime = (matchId, newTime) =>
    api.patch(`/matches/${matchId}/set-time?time=${newTime}`);

// Блок операций с командами
export const addTeamToMatch = (matchId, teamId) =>
    api.patch(`/matches/${matchId}/add-team?teamId=${teamId}`);

export const removeTeamFromMatch = (matchId, teamId) =>
    api.patch(`/matches/${matchId}/remove-team?teamId=${teamId}`);

// Поисковые операции
export const getMatchesByTournament = (tournamentName) =>
    api.get(`/matches/search?tournament=${tournamentName}`);

export const getMatchesByDateRange = (params) =>
    api.get('/matches/search/by-date', { params });

