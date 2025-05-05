import { useEffect, useState } from "react";
import {
    TextField,
    Button,
    Paper,
    IconButton,
    Typography,
    CircularProgress,
    Grid,
    Card,
    CardContent,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Snackbar,
    Alert,
    MenuItem,
    Select,
    InputLabel,
    FormControl,
    Chip,
    Box,
    Divider,
    Tabs,
    Tab
} from "@mui/material";
import {
    Add as AddIcon,
    Delete as DeleteIcon,
    Edit as EditIcon,
    SportsSoccer as MatchIcon,
    Stadium as ArenaIcon,
    Event as DateIcon,
    Groups as TeamsIcon,
    EmojiEvents as TournamentIcon,
    People as PlayersIcon,
    Search as SearchIcon,
    DateRange as DateRangeIcon
} from "@mui/icons-material";
import styled from "@emotion/styled";
import * as matchApi from "../services/matchService";
import * as teamApi from "../services/teamService";
import * as arenaApi from "../services/arenaService";
import { DateTimePicker, DatePicker } from "@mui/x-date-pickers";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { AdapterDateFns } from "@mui/x-date-pickers/AdapterDateFns";
import dayjs from 'dayjs';
import { format } from 'date-fns';
import { ru } from 'date-fns/locale';
const Container = styled.div`
    max-width: 1280px;
    margin: 0 auto;
    padding: 16px;
`;

const Header = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 32px;
`;

const ActionBar = styled(Paper)`
    padding: 24px;
    margin-bottom: 24px;
    border-radius: 12px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
`;

const FormGrid = styled(Grid)`
    gap: 16px;
`;

const MatchesList = styled.div`
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
    gap: 16px;
    margin-top: 24px;
`;

const MatchCard = styled(Card)`
    border-radius: 12px;
    transition: transform 0.2s, box-shadow 0.2s;
    position: relative;

    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 12px rgba(0, 0, 0, 0.1);
    }
`;

const MatchHeader = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
`;

const MatchContent = styled.div`
    padding: 16px;
`;

const TeamInfo = styled.div`
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 12px;
    border-radius: 8px;
    background: ${props => props.isHome ? '#f0f7ff' : '#fff0f0'};
    flex: 1;
`;

const TeamsContainer = styled.div`
    display: flex;
    gap: 16px;
    margin: 16px 0;
`;

const InfoItem = styled.div`
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 12px;
    padding: 8px;
    background: #f8f9fa;
    border-radius: 8px;
`;

const ButtonContainer = styled.div`
    display: flex;
    justify-content: flex-end;
    gap: 8px;
    margin-top: 16px;
`;

const PlayersCount = styled.div`
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 0.875rem;
    color: #666;
    margin-top: 8px;
`;

export default function MatchPage() {
    const [matches, setMatches] = useState([]);
    const [filteredMatches, setFilteredMatches] = useState([]);
    const [teams, setTeams] = useState([]);
    const [arenas, setArenas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);
    const [matchToDelete, setMatchToDelete] = useState(null);
    const [snackbar, setSnackbar] = useState({
        open: false,
        message: "",
        severity: "success"
    });
    const [searchTab, setSearchTab] = useState(0);
    const [tournamentSearch, setTournamentSearch] = useState("");
    const [startDate, setStartDate] = useState(null);
    const [endDate, setEndDate] = useState(null);
    const [editDialogOpen, setEditDialogOpen] = useState(false);
    const [currentMatch, setCurrentMatch] = useState(null);
    const [editHomeTeamId, setEditHomeTeamId] = useState("");
    const [editAwayTeamId, setEditAwayTeamId] = useState("");
    const [editArenaId, setEditArenaId] = useState("");
    const [editMatchDate, setEditMatchDate] = useState(new Date());
    const [editTournament, setEditTournament] = useState("");
    // Форма создания матча
    const [homeTeamId, setHomeTeamId] = useState("");
    const [awayTeamId, setAwayTeamId] = useState("");
    const [arenaId, setArenaId] = useState("");
    const [matchDate, setMatchDate] = useState(new Date());
    const [tournament, setTournament] = useState("");

    const fetchData = async () => {
        setLoading(true);
        try {
            const [matchesRes, teamsRes, arenasRes] = await Promise.all([
                matchApi.getAllMatches(),
                teamApi.getAllTeams(),
                arenaApi.getAllArenas()
            ]);

            setMatches(matchesRes.data);
            setFilteredMatches(matchesRes.data);
            setTeams(teamsRes.data);
            setArenas(arenasRes.data);
        } catch (error) {
            showSnackbar("Ошибка при загрузке данных", "error");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const resetAddMatchForm = () => {
        setHomeTeamId("");
        setAwayTeamId("");
        setArenaId("");
        setMatchDate(new Date());
        setTournament("");
        showSnackbar("Форма сброшена", "info");
    };

    // Функция для открытия диалога редактирования
    const handleEditClick = (match) => {
        setCurrentMatch(match);
        setEditHomeTeamId(match.teamDtoWithPlayersList?.[0]?.id || "");
        setEditAwayTeamId(match.teamDtoWithPlayersList?.[1]?.id || "");
        setEditArenaId(match.arenaDto?.id || "");
        setEditMatchDate(new Date(match.dateTime));
        setEditTournament(match.tournamentName || "");
        setEditDialogOpen(true);
    };

    const handleSaveChanges = async () => {
        try {
            // Форматируем дату для бэкенда
            const formatDateForBackend = (date) => {
                return dayjs(date).format('YYYY-MM-DDTHH:mm:ss'); // Формат без миллисекунд и 'Z'
            };

            // Обновляем время матча (используем новый формат)
            await matchApi.updateMatchTime(
                currentMatch.id,
                formatDateForBackend(editMatchDate)
            );

            // Обновляем арену
            if (editArenaId !== currentMatch.arenaDto?.id) {
                await matchApi.setMatchArena(currentMatch.id, editArenaId);
            }

            // Обновляем команды
            const currentHomeTeamId = currentMatch.teamDtoWithPlayersList?.[0]?.id;
            const currentAwayTeamId = currentMatch.teamDtoWithPlayersList?.[1]?.id;

            if (editHomeTeamId !== currentHomeTeamId) {
                if (currentHomeTeamId) {
                    await matchApi.removeTeamFromMatch(currentMatch.id, currentHomeTeamId);
                }
                await matchApi.addTeamToMatch(currentMatch.id, editHomeTeamId);
            }

            if (editAwayTeamId !== currentAwayTeamId) {
                if (currentAwayTeamId) {
                    await matchApi.removeTeamFromMatch(currentMatch.id, currentAwayTeamId);
                }
                await matchApi.addTeamToMatch(currentMatch.id, editAwayTeamId);
            }

            // Обновляем турнир
            if (editTournament !== currentMatch.tournamentName) {
                await matchApi.updateMatch(currentMatch.id, {
                    tournamentName: editTournament
                });
            }

            showSnackbar("Изменения сохранены", "success");
            fetchData(); // Обновляем данные
            setEditDialogOpen(false);
        } catch (error) {
            console.error("Ошибка при обновлении матча:", error);
            showSnackbar("Ошибка при обновлении матча: " + (error.response?.data?.message || error.message), "error");
        }
    };

    const handleSearchByTournament = async () => {
        if (!tournamentSearch.trim()) {
            showSnackbar("Введите название турнира для поиска", "warning");
            return;
        }

        setLoading(true);
        try {
            const res = await matchApi.getMatchesByTournament(tournamentSearch);
            setFilteredMatches(res.data);
            showSnackbar(`Найдено ${res.data.length} матчей`, "success");
        } catch (error) {
            showSnackbar("Ошибка при поиске матчей", "error");
            setFilteredMatches([]);
        } finally {
            setLoading(false);
        }
    };

    const formatToLocalDateTimeString = (date) => {
        const pad = (n) => String(n).padStart(2, '0');
        return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
    };

    const handleSearchByDateRange = async () => {
        if (!startDate && !endDate) {
            showSnackbar("Выберите хотя бы одну дату", "warning");
            return;
        }

        try {
            setLoading(true);
            const params = {};

            if (startDate instanceof Date && !isNaN(startDate)) {
                const start = new Date(startDate);
                start.setHours(0, 0, 0, 0);
                params.startDate = formatToLocalDateTimeString(start); // без 'Z'
            }

            if (endDate instanceof Date && !isNaN(endDate)) {
                const end = new Date(endDate);
                end.setHours(23, 59, 59, 999);
                params.endDate = formatToLocalDateTimeString(end); // без 'Z'
            }

            const res = await matchApi.getMatchesByDateRange(params);
            setFilteredMatches(res.data);
            showSnackbar(`Найдено ${res.data.length} матчей`, "success");
        } catch (error) {
            console.error("Ошибка поиска:", error);
            showSnackbar("Ошибка при поиске: " + (error.response?.data?.message || error.message), "error");
            setFilteredMatches([]);
        } finally {
            setLoading(false);
        }
    };




    const resetSearch = () => {
        setFilteredMatches(matches);
        setTournamentSearch("");
        setStartDate(null);
        setEndDate(null);
        showSnackbar("Поиск сброшен", "info");
    };

    const handleAddMatch = async () => {
        if (!homeTeamId || !awayTeamId || !arenaId || !matchDate) {
            showSnackbar("Заполните все обязательные поля", "warning");
            return;
        }

        if (homeTeamId === awayTeamId) {
            showSnackbar("Команды должны быть разными", "warning");
            return;
        }

        const formatTournamentName = (name) => {
            if (!name || !name.trim()) return "Дружеский матч";
            return name
                .trim()
                .split(/\s+/)
                .map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
                .join(" ");
        };

        try {
            const matchRequest = {
                dateTime: dayjs(matchDate).format('YYYY-MM-DDTHH:mm:ss'),  // Преобразуем в ISO строку в UTC
                tournamentName: formatTournamentName(tournament),
                homeTeamId: parseInt(homeTeamId),
                awayTeamId: parseInt(awayTeamId),
                arenaId: parseInt(arenaId)
            };

            await matchApi.createMatch(matchRequest);

            fetchData();

            setHomeTeamId("");
            setAwayTeamId("");
            setArenaId("");
            setMatchDate(new Date());  // Это сохранит локальное время
            setTournament("");

            showSnackbar("Матч успешно добавлен", "success");
        } catch (error) {
            console.error("Ошибка при добавлении матча:", error);
            showSnackbar("Ошибка при добавлении матча: " + (error.response?.data?.message || error.message), "error");
        }
    };



    const formatDisplayDate = (dateString) => {
        const date = new Date(dateString);
        if (isNaN(date.getTime())) return "Неизвестная дата";

        // Преобразуем в локальное время с нужным форматом
        return format(date, 'dd.MM.yyyy HH:mm');  // Локализованный формат
    };

    const confirmDelete = (id) => {
        setMatchToDelete(id);
        setOpenDialog(true);
    };

    const handleDelete = async () => {
        try {
            await matchApi.deleteMatch(matchToDelete);

            fetchData();
            setOpenDialog(false);
            showSnackbar("Матч удален", "success");
        } catch (error) {
            showSnackbar("Ошибка при удалении матча", "error");
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleCloseSnackbar = () => {
        setSnackbar({ ...snackbar, open: false });
    };

    const getTeamById = (id) => {
        return teams.find(t => t.id === id) || { teamName: "Неизвестная команда", country: "Неизвестно" };
    };

    return (
        <LocalizationProvider dateAdapter={AdapterDateFns} adapterLocale={ru}>
            <Container>
                <Header>
                    <Typography variant="h4" sx={{ fontWeight: "bold", color: "#2e7d32" }}>
                        Управление матчами
                    </Typography>
                    <Button
                        variant="contained"
                        startIcon={<AddIcon />}
                        onClick={() => window.scrollTo(0, 0)}
                        sx={{
                            bgcolor: '#2e7d32',
                            '&:hover': { bgcolor: '#2e7d32' },
                            borderRadius: '8px',
                        }}
                    >
                        Добавить матч
                    </Button>
                </Header>

                <Paper elevation={0} sx={{ p: 3, mb: 3, borderRadius: 3 }}>
                    <Typography variant="h6" sx={{ mb: 2, color: "#2e7d32" }}>
                        Поиск матчей
                    </Typography>

                    <Tabs
                        value={searchTab}
                        onChange={(e, newValue) => setSearchTab(newValue)}
                        sx={{ mb: 2 }}
                    >
                        <Tab label="По турниру"
                             icon={<TournamentIcon />}
                        />
                        <Tab label="По датам" icon={<DateRangeIcon />} />
                    </Tabs>

                    {searchTab === 0 ? (
                        <Box sx={{ display: 'flex', gap: 2 }}>
                            <TextField
                                fullWidth
                                label="Название турнира"
                                value={tournamentSearch}
                                onChange={(e) => setTournamentSearch(e.target.value)}
                                variant="outlined"
                                size="small"
                                InputProps={{
                                    startAdornment: <TournamentIcon color="action" sx={{ mr: 1 }} />,
                                }}
                            />
                            <Button
                                variant="contained"
                                startIcon={<SearchIcon />}
                                onClick={handleSearchByTournament}
                                sx={{
                                    bgcolor: '#2e7d32',
                                    '&:hover': { bgcolor: '#2e7d32' },
                                    borderRadius: '8px',
                                    minWidth: '120px'
                                }}
                            >
                                Найти
                            </Button>
                        </Box>
                    ) : (
                        <Box sx={{ display: 'flex', gap: 2, alignItems: 'center' }}>
                            <DatePicker
                                label="Начальная дата"
                                value={startDate}
                                onChange={(newValue) => setStartDate(newValue)}
                                inputFormat="dd.MM.yyyy"
                                mask="__.__.____"
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        size="small"
                                        InputProps={{
                                            startAdornment: <DateIcon color="action" sx={{ mr: 1 }} />,
                                        }}
                                    />
                                )}
                            />
                            <Typography variant="body1">—</Typography>
                            <DatePicker
                                label="Конечная дата"
                                value={endDate}
                                onChange={(newValue) => setEndDate(newValue)}
                                minDate={startDate}
                                inputFormat="dd.MM.yyyy"
                                mask="__.__.____"
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        size="small"
                                        InputProps={{
                                            startAdornment: <DateIcon color="action" sx={{ mr: 1 }} />,
                                        }}
                                    />
                                )}
                            />
                            <Button
                                variant="contained"
                                startIcon={<SearchIcon />}
                                onClick={handleSearchByDateRange}
                                sx={{
                                    bgcolor: '#2e7d32',
                                    '&:hover': { bgcolor: '#2e7d32' },
                                    borderRadius: '8px',
                                    minWidth: '120px'
                                }}
                            >
                                Найти
                            </Button>
                        </Box>
                    )}

                    <Button
                        variant="outlined"
                        onClick={resetSearch}
                        sx={{ mt: 2,
                            borderColor: '#b31e1e',
                            color: '#b31e1e',
                            '&:hover': {
                                borderColor: '#b31e1e',
                                color: '#b31e1e'
                            }}}
                    >
                        Сбросить поиск
                    </Button>
                </Paper>



                <ActionBar elevation={0}>
                    <Typography variant="h6" sx={{ marginBottom: 2, color: "#2e7d32" }}>
                        Добавить новый матч
                    </Typography>

                    <FormGrid container spacing={2}>
                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Хозяева</InputLabel>
                                <Select
                                    value={homeTeamId}
                                    onChange={(e) => setHomeTeamId(e.target.value)}
                                    label="Хозяева"
                                    startAdornment={<TeamsIcon color="action" sx={{ mr: 1 }} />}
                                    sx={{ height: 56,
                                    width : 230}} // Устанавливаем одинаковую высоту для всех Select
                                >
                                    {teams.map(team => (
                                        <MenuItem key={team.id} value={team.id}>
                                            {team.teamName}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Гости</InputLabel>
                                <Select
                                    value={awayTeamId}
                                    onChange={(e) => setAwayTeamId(e.target.value)}
                                    label="Гости"
                                    startAdornment={<TeamsIcon color="action" sx={{ mr: 1 }} />}
                                    sx={{ height: 56,
                                        width : 230}} // Устанавливаем одинаковую высоту для всех Select
                                >
                                    {teams.map(team => (
                                        <MenuItem key={team.id} value={team.id}>
                                            {team.teamName}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        <Grid item xs={12} sm={6} md={3}>
                            <FormControl fullWidth size="small">
                                <InputLabel>Арена</InputLabel>
                                <Select
                                    value={arenaId}
                                    onChange={(e) => setArenaId(e.target.value)}
                                    label="Арена"
                                    startAdornment={<ArenaIcon color="action" sx={{ mr: 1 }} />}
                                    sx={{ height: 56,
                                        width : 230}} // Устанавливаем одинаковую высоту для всех Select
                                >
                                    {arenas.map(arena => (
                                        <MenuItem key={arena.id} value={arena.id}>
                                            {arena.city} ({arena.capacity})
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>

                        <Grid item xs={12} sm={6} md={3}>
                            <DateTimePicker
                                label="Дата и время"
                                value={matchDate}
                                onChange={setMatchDate}
                                renderInput={(params) => (
                                    <TextField
                                        {...params}
                                        fullWidth
                                        size="small"
                                        InputProps={{
                                            startAdornment: <DateIcon color="action" sx={{ mr: 1 }} />,
                                        }}
                                        sx={{ height: 56 }} // Устанавливаем одинаковую высоту для DateTimePicker
                                    />
                                )}
                            />
                        </Grid>

                        <Grid item xs={12} sm={8} md={4}>
                            <TextField
                                fullWidth
                                label="Турнир"
                                value={tournament}
                                onChange={(e) => setTournament(e.target.value)}
                                variant="outlined"
                                size="small"
                                InputProps={{
                                    startAdornment: <TournamentIcon color="action" sx={{ mr: 1 }} />,
                                }}
                                sx={{
                                    height: 56, // Устанавливаем одинаковую высоту для всех TextField
                                    '& .MuiInputBase-root': {
                                        height: '56px', // Устанавливаем высоту для ввода в TextField
                                        width : 230
                                    }
                                }}
                            />
                        </Grid>

                        <Grid item xs={12} sm={2} md={1}> {/* Уменьшил размер колонки */}
                            <Button
                                variant="outlined"
                                onClick={resetAddMatchForm}
                                fullWidth
                                sx={{
                                    borderRadius: '8px',
                                    height: '56px',
                                    width: 90,
                                    borderColor: '#b31e1e',
                                    color: '#b31e1e',
                                    '&:hover': {
                                        borderColor: '#b31e1e',
                                        color: '#b31e1e'
                                    }
                                }}
                            >
                                Сбросить
                            </Button>
                        </Grid>

                        <Grid item xs={12} sm={2} md={1}> {/* Уменьшил размер колонки */}
                            <Button
                                variant="contained"
                                startIcon={<AddIcon />}
                                onClick={handleAddMatch}
                                fullWidth
                                sx={{
                                    bgcolor: '#2e7d32',
                                    '&:hover': { bgcolor: '#2e7d32' },
                                    borderRadius: '8px',
                                    height: '56px',
                                    width: 122
                                }}
                            >
                                Добавить
                            </Button>
                        </Grid>
                    </FormGrid>
                </ActionBar>



                {loading ? (
                    <div style={{ textAlign: "center", padding: 40 }}>
                        <CircularProgress size={60} thickness={4} sx={{ color: "#2e7d32" }} />
                    </div>
                ) : (
                    <>
                        <Typography variant="h6" sx={{ marginBottom: 2, color: "#555" }}>
                            Найдено матчей: {filteredMatches.length}
                        </Typography>

                        {filteredMatches.length === 0 ? (
                            <Paper elevation={0} sx={{ p: 4, textAlign: "center", borderRadius: 3, bgcolor: "#f8f9fa" }}>
                                <Typography variant="body1" color="textSecondary">
                                    Матчи не найдены. Измените параметры поиска.
                                </Typography>
                            </Paper>
                        ) : (
                            <MatchesList>
                                {filteredMatches.map(match => {
                                    const matchDate = new Date(match.dateTime);
                                    const homeTeam = match.teamDtoWithPlayersList?.[0] || {};
                                    const awayTeam = match.teamDtoWithPlayersList?.[1] || {};

                                    return (
                                        <MatchCard key={match.id}>
                                            <MatchContent>
                                                <MatchHeader>
                                                    <Typography variant="subtitle1" sx={{ fontWeight: 600 }}>
                                                        {match.tournamentName}
                                                    </Typography>
                                                    <IconButton
                                                        color="error"
                                                        onClick={() => confirmDelete(match.id)}
                                                        size="small"
                                                    >
                                                        <DeleteIcon />
                                                    </IconButton>
                                                </MatchHeader>

                                                <TeamsContainer>
                                                    <TeamInfo isHome>
                                                        <Typography variant="h6" sx={{ fontWeight: 600 }}>
                                                            {homeTeam.teamName || "Неизвестно"}
                                                        </Typography>
                                                        <Typography variant="body2" color="textSecondary">
                                                            {homeTeam.country}
                                                        </Typography>
                                                        <PlayersCount>
                                                            <PlayersIcon fontSize="small" />
                                                            {homeTeam.playerDtoList?.length || 0} игроков
                                                        </PlayersCount>
                                                        <Chip
                                                            label="Хозяева"
                                                            color="primary"
                                                            size="small"
                                                            sx={{ mt: 1 }}
                                                        />
                                                    </TeamInfo>

                                                    <Divider orientation="vertical" flexItem />

                                                    <TeamInfo>
                                                        <Typography variant="h6" sx={{ fontWeight: 600 }}>
                                                            {awayTeam.teamName || "Неизвестно"}
                                                        </Typography>
                                                        <Typography variant="body2" color="textSecondary">
                                                            {awayTeam.country}
                                                        </Typography>
                                                        <PlayersCount>
                                                            <PlayersIcon fontSize="small" />
                                                            {awayTeam.playerDtoList?.length || 0} игроков
                                                        </PlayersCount>
                                                        <Chip
                                                            label="Гости"
                                                            color="secondary"
                                                            size="small"
                                                            sx={{ mt: 1 }}
                                                        />
                                                    </TeamInfo>
                                                </TeamsContainer>

                                                <InfoItem>
                                                    <ArenaIcon fontSize="small" />
                                                    <Typography variant="body1">
                                                        {match.arenaDto?.city || "Неизвестно"} ({match.arenaDto?.name})
                                                        {match.arenaDto?.capacity && `, ${match.arenaDto.capacity} мест`}
                                                    </Typography>
                                                </InfoItem>

                                                <InfoItem>
                                                    <DateIcon fontSize="small" />
                                                    <Typography variant="body1">
                                                        {formatDisplayDate(match.dateTime)}
                                                    </Typography>
                                                </InfoItem>

                                                <ButtonContainer>
                                                    <Button
                                                        variant="outlined"
                                                        color="primary"
                                                        startIcon={<EditIcon />}
                                                        onClick={() => handleEditClick(match)}
                                                    >
                                                        Редактировать
                                                    </Button>
                                                </ButtonContainer>
                                            </MatchContent>
                                        </MatchCard>
                                    );
                                })}
                            </MatchesList>
                        )}
                    </>
                )}

                <Dialog
                    open={openDialog}
                    onClose={() => setOpenDialog(false)}
                >
                    <DialogTitle>Подтверждение удаления</DialogTitle>
                    <DialogContent>
                        <Typography variant="body1">
                            Вы уверены, что хотите удалить этот матч?
                        </Typography>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setOpenDialog(false)}>
                            Отмена
                        </Button>
                        <Button
                            onClick={handleDelete}
                            color="error"
                            variant="contained"
                        >
                            Удалить
                        </Button>
                    </DialogActions>
                </Dialog>
                <Dialog
                    open={editDialogOpen}
                    onClose={() => setEditDialogOpen(false)}
                    maxWidth="md"
                    fullWidth
                >
                    <DialogTitle>Редактирование матча</DialogTitle>
                    <DialogContent>
                        <Grid container spacing={2} sx={{ mt: 1 }}>
                            <Grid item xs={12} sm={6}>
                                <FormControl fullWidth size="small">
                                    <InputLabel>Хозяева</InputLabel>
                                    <Select
                                        value={editHomeTeamId}
                                        onChange={(e) => setEditHomeTeamId(e.target.value)}
                                        label="Хозяева"
                                    >
                                        {teams.map(team => (
                                            <MenuItem key={team.id} value={team.id}>
                                                {team.teamName}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <FormControl fullWidth size="small">
                                    <InputLabel>Гости</InputLabel>
                                    <Select
                                        value={editAwayTeamId}
                                        onChange={(e) => setEditAwayTeamId(e.target.value)}
                                        label="Гости"
                                    >
                                        {teams.map(team => (
                                            <MenuItem key={team.id} value={team.id}>
                                                {team.teamName}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <FormControl fullWidth size="small">
                                    <InputLabel>Арена</InputLabel>
                                    <Select
                                        value={editArenaId}
                                        onChange={(e) => setEditArenaId(e.target.value)}
                                        label="Арена"
                                    >
                                        {arenas.map(arena => (
                                            <MenuItem key={arena.id} value={arena.id}>
                                                {arena.city} ({arena.capacity})
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <DateTimePicker
                                    label="Дата и время"
                                    value={editMatchDate}
                                    onChange={setEditMatchDate}
                                    renderInput={(params) => (
                                        <TextField {...params} fullWidth size="small" />
                                    )}
                                />
                            </Grid>

                            <Grid item xs={12}>
                                <TextField
                                    fullWidth
                                    label="Турнир"
                                    value={editTournament}
                                    onChange={(e) => setEditTournament(e.target.value)}
                                    variant="outlined"
                                    size="small"
                                />
                            </Grid>
                        </Grid>
                    </DialogContent>
                    <DialogActions>
                        <Button onClick={() => setEditDialogOpen(false)}>Отмена</Button>
                        <Button
                            onClick={handleSaveChanges}
                            variant="contained"
                            color="primary"
                        >
                            Сохранить
                        </Button>
                    </DialogActions>
                </Dialog>

                <Snackbar
                    open={snackbar.open}
                    autoHideDuration={4000}
                    onClose={handleCloseSnackbar}
                    anchorOrigin={{ vertical: "bottom", horizontal: "right" }}
                >
                    <Alert
                        onClose={handleCloseSnackbar}
                        severity={snackbar.severity}
                        sx={{ width: "100%" }}
                    >
                        {snackbar.message}
                    </Alert>
                </Snackbar>
            </Container>
        </LocalizationProvider>
    );
}