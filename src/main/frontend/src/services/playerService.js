import api from './api';

export const createPlayer = (playerData) => {
    // Удаляем teamId из тела запроса, если он null или undefined
    const { teamId, ...data } = playerData;

    return api.post('/players/create', data, {
        params: teamId ? { teamId } : {} // Передаем teamId только если он есть
    });
};

export const getAllPlayers = () => api.get('/players');

export const updatePlayer = (id, playerData) => {
    const { teamId, ...data } = playerData;
    return api.put(`/players/${id}`, data, {
        params: teamId ? { teamId } : {}
    });
};

export const deletePlayer = (id) => api.delete(`/players/${id}`);

export const getPlayersByAge = (age) => api.get(`/players/search?age=${age}`);;