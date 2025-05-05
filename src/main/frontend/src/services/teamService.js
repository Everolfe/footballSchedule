import api from './api';

// Основные CRUD операции
export const createTeam = (teamData) => api.post('/teams/create', teamData);
export const getTeam = (id) => api.get(`/teams/${id}`);
export const getAllTeams = () => api.get('/teams');
export const updateTeam = (id, teamData) => api.put(`/teams/${id}`, teamData);
export const deleteTeam = (id) => api.delete(`/teams/${id}`);

// Операции с игроками
export const addPlayerToTeam = (id, playerId) => {
    if (!id || !playerId) {
        return Promise.reject(new Error('teamId и playerId обязательны для добавления игрока.'));
    }

    return api.patch(`/teams/${id}/add-player`, null, {
        params: { playerId }
    });
};


export const removePlayerFromTeam = (teamId, playerId) =>
    api.patch(`/teams/${teamId}/remove-player?playerId=${playerId}`);

// Пакетное создание
export const createTeamsBulk = (teamsList) => api.post('/teams/bulk', teamsList);