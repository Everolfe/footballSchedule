import api from './api';

// Основные CRUD операции
export const createArena = (arenaData) => api.post('/arenas/create  ', arenaData);
export const getAllArenas = () => api.get('/arenas');
export const updateArena = (id, arenaData) => api.put(`/arenas/${id}`, arenaData);
export const deleteArena = (id) => api.delete(`/arenas/${id}`);

// Поиск по вместимости
export const getArenasByCapacity = (minCapacity, maxCapacity) =>
    api.get('/arenas/search', { params: { minCapacity, maxCapacity } });
