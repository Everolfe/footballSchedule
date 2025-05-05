import axios from "axios";

const api = axios.create({
    baseURL: process.env.NODE_ENV === 'development'
        ? 'http://localhost:8080'  // Для разработки - прямое обращение к бэкенду
        : '/api',                      // Для продакшена - относительный путь через прокси
    timeout: 10000,                  // 10 секунд таймаута
    headers: {
        "Content-Type": "application/json",
        "Accept": "application/json"
    },
    withCredentials: true            // Для работы с куками и сессиями
});

export default api;