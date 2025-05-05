import { useState, useEffect } from "react";
import {
    Button,
    TextField,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    IconButton,
    Chip,
    CircularProgress,
    Snackbar,
    Autocomplete,
    Box
} from "@mui/material";
import {
    Add,
    Close,
    Delete,
    Edit,
    FilterList,
    CheckCircle,
    SportsSoccer
} from "@mui/icons-material";
import { useForm, Controller } from "react-hook-form";
import * as teamApi from "../services/teamService";
import * as playerApi from "../services/playerService"
import * as matchApi from "../services/matchService"
import styled from "@emotion/styled";
import EventIcon from '@mui/icons-material/Event';
const Container = styled.div`
    max-width: 1280px;
    margin: 0 auto;
    padding: 32px 24px;
    background-color: #f8f9fa;
    border-radius: 16px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
`;

const Header = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 40px;
`;

const ActionBar = styled.div`
    display: flex;
    gap: 16px;
    margin-bottom: 32px;
    background: #f8f9fa;
    padding: 16px;
    border-radius: 12px;
`;

const TeamGrid = styled.div`
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 24px;
    margin-top: 24px;
`;

const TeamCard = styled.div`
    background: white;
    border-radius: 16px;
    padding: 24px;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
    transition: transform 0.2s, box-shadow 0.2s;
    position: relative;
    overflow: hidden;

    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 12px rgba(0, 0, 0, 0.1);
    }

    &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 6px;
        height: 100%;
        background-color: #2e7d32;
    }
`;

function TeamMatchesDialog({ open, matches, team, onClose }) {
    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                bgcolor: '#2e7d32',
                color: 'white'
            }}>
                Матчи команды {team?.teamName}
                <IconButton onClick={onClose} sx={{ color: 'white' }}>
                    <Close />
                </IconButton>
            </DialogTitle>

            <DialogContent sx={{ py: 3 }}>
                {matches?.length > 0 ? (
                    matches.map(match => {

                        // Проверяем наличие списка команд
                        const teams = match.teamDtoWithPlayersList || [];

                        // Находим индекс текущей команды
                        const currentTeamIndex = teams.findIndex(t => t.id === team?.id);
                        const opponentIndex = currentTeamIndex === 0 ? 1 : 0;
                        const opponent = teams[opponentIndex];

                        const matchDate = new Date(match.date || match.dateTime);

                        return (
                            <Box key={match.id} sx={{
                                display: 'flex',
                                flexDirection: 'column',
                                mb: 2,
                                p: 2,
                                bgcolor: '#f5f5f5',
                                borderRadius: 1
                            }}>
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    marginBottom: '8px'
                                }}>
                                    <span style={{ fontWeight: 600 }}>
                                        {team.teamName} vs {opponent?.teamName || 'Неизвестный соперник'}
                                    </span>
                                    <Chip
                                        label={matchDate.toLocaleDateString()}
                                        size="small"
                                        color="primary"
                                    />
                                </div>
                                <div style={{ fontSize: '0.9rem' }}>
                                    <div>Время: {matchDate.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</div>
                                    {match.tournamentName && <div>Турнир: {match.tournamentName}</div>}
                                    {match.arenaDto && (
                                        <div>Стадион: {match.arenaDto.city || match.arenaDto.name}
                                            {match.arenaDto.capacity && ` (${match.arenaDto.capacity} мест)`}
                                        </div>
                                    )}
                                </div>
                            </Box>
                        );
                    })
                ) : (
                    <Box sx={{ textAlign: 'center', py: 2 }}>
                        Нет матчей для этой команды
                    </Box>
                )}
            </DialogContent>
        </Dialog>
    );
}

function TeamPlayersDialog({ open, players, onClose, onRemovePlayer }) {
    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                bgcolor: '#2e7d32',
                color: 'white'
            }}>
                Игроки команды
                <IconButton onClick={onClose} sx={{ color: 'white' }}>
                    <Close />
                </IconButton>
            </DialogTitle>

            <DialogContent sx={{ py: 3 }}>
                {players?.length > 0 ? (
                    players.map(player => (
                        <Box key={player.id} sx={{
                            display: 'flex',
                            justifyContent: 'space-between',
                            alignItems: 'center',
                            mb: 1,
                            p: 1,
                            bgcolor: '#f5f5f5',
                            borderRadius: 1
                        }}>
                            <div>
                                <div style={{ fontWeight: 500 }}>{player.name}</div>
                                <div style={{ fontSize: '0.8rem', color: '#666' }}>
                                             {player.age} лет
                                </div>
                            </div>
                            <IconButton
                                color="error"
                                onClick={() => onRemovePlayer(player.id)}
                                size="small"
                            >
                                <Delete fontSize="small" />
                            </IconButton>
                        </Box>
                    ))
                ) : (
                    <Box sx={{ textAlign: 'center', py: 2 }}>
                        Нет игроков в команде
                    </Box>
                )}
            </DialogContent>
        </Dialog>
    );
}

function AddPlayerDialog({ open, players, team, onClose, onAddPlayer }) {
    const [selectedPlayer, setSelectedPlayer] = useState(null);
    const [loading, setLoading] = useState(false);  // Инициализация состояния загрузки

    const handleAdd = async () => {
        if (!selectedPlayer) return;

        setLoading(true);  // Включаем состояние загрузки

        try {
            // Пропускаем проверку, так как функция onAddPlayer предполагает, что она работает с id игрока
            await onAddPlayer(selectedPlayer.id);
            setLoading(false);  // Отключаем состояние загрузки
            onClose();  // Закрываем диалог после добавления игрока
        } catch (error) {
            console.error("Не удалось добавить игрока:", error);
            setLoading(false);  // Отключаем состояние загрузки при ошибке
        }
    };

    return (
        <Dialog open={open} onClose={onClose} fullWidth maxWidth="sm">
            <DialogTitle sx={{
                display: 'flex',
                justifyContent: 'space-between',
                alignItems: 'center',
                bgcolor: '#2e7d32',
                color: 'white'
            }}>
                Добавить игрока в {team?.teamName}
                <IconButton onClick={onClose} sx={{ color: 'white' }}>
                    <Close />
                </IconButton>
            </DialogTitle>
            <DialogContent sx={{ py: 3 }}>
                <Autocomplete
                    options={players}  // Массив игроков
                    getOptionLabel={(player) => `${player.name} ( ${player.age} лет)`}  // Выводим информацию об игроке
                    renderInput={(params) => (
                        <TextField {...params} label="Выберите игрока" fullWidth />
                    )}
                    onChange={(event    , newValue) => setSelectedPlayer(newValue)}  // Устанавливаем выбранного игрока
                    sx={{ mt: 2 }}
                    noOptionsText="Нет доступных игроков"
                />
            </DialogContent>
            <DialogActions sx={{ px: 3, pb: 2 }}>
                <Button onClick={onClose} sx={{ mr: 2 }}>Отмена</Button>
                <Button
                    variant="contained"
                    onClick={handleAdd}  // Вызываем добавление игрока
                    disabled={!selectedPlayer || loading}  // Блокируем кнопку, если не выбран игрок или идет загрузка
                    startIcon={loading ? <CircularProgress size={20} /> : <Add />}
                    sx={{
                        bgcolor: '#2e7d32',
                        '&:hover': { bgcolor: '#1b5e20' },
                        borderRadius: '8px',
                        px: 3
                    }}
                >
                    Добавить
                </Button>
            </DialogActions>
        </Dialog>
    );
}



export default function TeamPage() {
    const { control, handleSubmit, reset } = useForm();
    const [teams, setTeams] = useState([]);
    const [openAddPlayerDialog, setOpenAddPlayerDialog] = useState(false);
    const [selectedTeamForAddPlayer, setSelectedTeamForAddPlayer] = useState(null);
    const [players, setPlayers] = useState([]);
    const [matches, setMatches] = useState([]);
    const [openMatchesDialog, setOpenMatchesDialog] = useState(false);
    const [currentTeamMatches, setCurrentTeamMatches] = useState([]);
    const [openPlayersDialog, setOpenPlayersDialog] = useState(false);
    const [currentTeamPlayers, setCurrentTeamPlayers] = useState([]);
    const [currentTeamId, setCurrentTeamId] = useState(null);
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedTeam, setSelectedTeam] = useState(null);
    const [snackbar, setSnackbar] = useState({ open: false, message: '' });
    const [searchTerm, setSearchTerm] = useState('');
    const [openEditDialog, setOpenEditDialog] = useState(false);
    const [teamToEdit, setTeamToEdit] = useState(null);
    const fetchData = async () => {
        setLoading(true);
        try {
            const [playersRes, teamsRes,matchRes] = await Promise.all([
                playerApi.getAllPlayers(),
                teamApi.getAllTeams(),
                matchApi.getAllMatches()
            ]);
            setPlayers(playersRes.data);
            setTeams(teamsRes.data);
            setMatches(matchRes.data);
        } catch (error) {
            showError("Ошибка при загрузке данных");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleOpenAddPlayer = (teamId) => {
        setSelectedTeamForAddPlayer(teamId);
        setOpenAddPlayerDialog(true);
    };

    const handleCloseAddPlayer = () => {
        setOpenAddPlayerDialog(false);
        setSelectedTeamForAddPlayer(null);
    };



    const handleViewMatches = (teamId) => {
        const team = teams.find(t => t.id === teamId);
        // Фильтруем матчи, где участвует текущая команда
        const teamMatches = matches.filter(match =>
            match.teamDtoWithPlayersList?.some(team => team.id === teamId)
        );
        setCurrentTeamMatches(teamMatches);
        setSelectedTeam(team);
        setOpenMatchesDialog(true);
    };

    const handleViewPlayers = (teamId, players) => {
        setCurrentTeamPlayers(players || []);
        setCurrentTeamId(teamId);
        setOpenPlayersDialog(true);
    };

    const handleRemovePlayer = async (playerId) => {
        try {
            await teamApi.removePlayerFromTeam(currentTeamId, playerId);
            setSnackbar({ open: true, message: 'Игрок удален из команды' });


            // Обновляем список команд
            await fetchData();
        } catch (error) {
            showError('Ошибка удаления игрока из команды');
        }
    };

    const handleCreateTeam = async (data) => {
        try {
            const response = await teamApi.createTeam({
                teamName: data.teamName, // Убедитесь, что используете правильные имена полей
                country: data.country
            });
            //setTeams(prev => [...prev, response.data]);
            await fetchData();
            setOpenDialog(false);
            reset();
            setSnackbar({ open: true, message: 'Команда успешно создана' });
        } catch (error) {
            console.error("Ошибка создания команды:", error);
            showError(error.response?.data?.message || 'Ошибка создания команды');
        }
    };
    const handleUpdateTeam = async (data) => {
        try {
            await teamApi.updateTeam(teamToEdit.id, {
                teamName: data.teamName,
                country: data.country
            });
            await fetchData();
            setOpenEditDialog(false);
            setTeamToEdit(null);
            setSnackbar({ open: true, message: 'Команда успешно обновлена' });
        } catch (error) {
            console.error("Ошибка обновления команды:", error);
            showError(error.response?.data?.message || 'Ошибка обновления команды');
        }
    };

    const handleDeleteTeam = async (teamId) => {
        try {
            await teamApi.deleteTeam(teamId);
            await fetchData();
            //setTeams(prev => prev.filter(t => t.id !== teamId));
            setSnackbar({ open: true, message: 'Команда удалена' });
        } catch (error) {
            showError('Ошибка удаления команды');
        }
    };

    const handleViewTeam = async (teamId) => {
        try {
            const response = await teamApi.getTeam(teamId);
            setSelectedTeam({
                ...response.data,
                players: response.data.playerDtoList || [] // Защита от undefined
            });
        } catch (error) {
            showError('Ошибка получения данных команды');
        }
    };

    const handleAddPlayer = async (playerId) => {
        if (!selectedTeamForAddPlayer) {
            console.error("Не выбрана команда для добавления игрока");
            return; // Если нет выбранной команды, прерываем выполнение
        }
        try {
            await teamApi.addPlayerToTeam(
                Number(selectedTeamForAddPlayer),
                Number(playerId)
            );
            await fetchData(); // Обновляем данные
            setOpenAddPlayerDialog(false);
        } catch (error) {
            console.error("Ошибка добавления игрока:", error.response?.data);

            setSnackbar({
                open: true,
                message: error.response?.data?.message || "Ошибка сервера",
                severity: "error" // Добавляем красный цвет для ошибок
            });
        }
    };


    const showError = (message) => {
        setSnackbar({ open: true, message });
    };

    return (
        <Container>
            <Header>
                <h1 style={{ margin: 0, fontSize: '32px', color: '#2e7d32' }}>Управление командами</h1>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => setOpenDialog(true)}
                    sx={{
                        bgcolor: '#2e7d32',
                        '&:hover': { bgcolor: '#1b5e20' },
                        borderRadius: '8px',
                        py: 1.5,
                        px: 3
                    }}
                >
                    Новая команда
                </Button>
            </Header>

            <ActionBar>
                <TextField
                    variant="outlined"
                    size="small"
                    placeholder="Поиск команд..."
                    InputProps={{
                        startAdornment: <FilterList color="action" sx={{ mr: 1 }} />
                    }}
                    sx={{ flex: 1 }}
                    onChange={(e) => setSearchTerm(e.target.value)}
                />
            </ActionBar>

            {loading ? (
                <div style={{ textAlign: 'center', padding: 40 }}>
                    <CircularProgress size={60} thickness={4} />
                </div>
            ) : (
                <TeamGrid>
                    {teams
                        .filter(team =>
                            team.teamName.toLowerCase().includes(searchTerm.toLowerCase()) ||
                            team.country.toLowerCase().includes(searchTerm.toLowerCase())
                        )
                        .map(team => (
                            <TeamCard key={team.id}>
                                <div style={{
                                    display: 'flex',
                                    justifyContent: 'space-between',
                                    alignItems: 'center',
                                    marginBottom: 16
                                }}>
                                    <h3 style={{
                                        margin: 0,
                                        color: '#2e7d32',
                                        fontSize: '1.5rem',
                                        fontWeight: 600,
                                        position: 'relative',
                                        paddingLeft: '12px'
                                    }}>
                                        {team.teamName}
                                        <span style={{
                                            position: 'absolute',
                                            left: 0,
                                            top: '50%',
                                            transform: 'translateY(-50%)',
                                            width: '6px',
                                            height: '60%',
                                            backgroundColor: '#2e7d32',
                                            borderRadius: '3px'
                                        }}></span>
                                    </h3>

                                    <div style={{
                                        display: 'flex',
                                        justifyContent: 'flex-end', // Выравнивание по правому краю
                                        gap: '4px', // Отступ между кнопками
                                        position: 'absolute', // Абсолютное позиционирование
                                        top: '10px', // Отступ сверху
                                        right: '5px' // Отступ справа
                                    }}>
                                        <IconButton
                                            color="primary"
                                            onClick={() => {
                                                setTeamToEdit(team);
                                                setOpenEditDialog(true);
                                            }}
                                            size="small"
                                            sx={{
                                                backgroundColor: 'rgba(46, 125, 50, 0.1)',
                                                '&:hover': {backgroundColor: 'rgba(46, 125, 50, 0.2)'}
                                            }}
                                        >
                                            <Edit fontSize="small"/>
                                        </IconButton>
                                        <IconButton
                                            color="error"
                                            onClick={() => handleDeleteTeam(team.id)}
                                            size="small"
                                            sx={{
                                                backgroundColor: 'rgba(211, 47, 47, 0.1)',
                                                '&:hover': {backgroundColor: 'rgba(211, 47, 47, 0.2)'}
                                            }}
                                        >
                                            <Delete fontSize="small"/>
                                        </IconButton>
                                    </div>
                                </div>

                                <div style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 8,
                                    marginBottom: 8,
                                    backgroundColor: '#f0f7f0',
                                    padding: '8px 12px',
                                    borderRadius: '8px'
                                }}>
                                    <Chip
                                        label={team.country}
                                        size="small"
                                        color="primary"
                                        sx={{fontWeight: 500}}
                                    />
                                </div>
                                <div style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 8,
                                    padding: '8px 12px',
                                    backgroundColor: '#f5f5f5',
                                    borderRadius: '8px',
                                    cursor: 'pointer'
                                }}>
                                    <SportsSoccer fontSize="small" color="action"/>

                                    <span
                                        style={{fontSize: '0.875rem', fontWeight: 500}}
                                        onClick={(e) => {
                                            e.stopPropagation();  // предотвращаем всплытие события
                                            handleViewPlayers(team.id, team.playerDtoList);
                                        }}
                                    >
                                    {team.playerDtoList?.length || 0} игроков
                                    </span>

                                    <div style={{display: 'flex', gap: 8, marginLeft: 'auto'}}>
                                        <IconButton
                                            size="small"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleOpenAddPlayer(team.id);
                                            }}
                                        >
                                            <Add fontSize="small"/>
                                        </IconButton>
                                        <Button
                                            size="small"
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                handleViewPlayers(team.id, team.playerDtoList);
                                            }}
                                        >
                                            ПРОСМОТР
                                        </Button>
                                    </div>
                                </div>

                                <div style={{
                                    display: 'flex',
                                    alignItems: 'center',
                                    gap: 8,
                                    padding: '8px 12px',
                                    backgroundColor: '#e3f5fd',
                                    borderRadius: '8px',
                                    marginTop: '8px',
                                    cursor: 'pointer'
                                }} onClick={() => handleViewMatches(team.id)}>
                                    <EventIcon fontSize="small" color="action"/>
                                    <span style={{fontSize: '0.875rem', fontWeight: 500}}>
                                        Матчи: {team.matchDtoWithArenaList?.length || 0}
                                     </span>
                                    <Button
                                        size="small"
                                        sx={{ml: 'auto'}}
                                        onClick={(e) => {
                                            e.stopPropagation();
                                            handleViewMatches(team.id, team.matchDtoWithArenaList);
                                        }}
                                    >
                                        Просмотр
                                    </Button>
                                </div>
                            </TeamCard>
                        ))}
                </TeamGrid>
            )}
            <Dialog
                open={openEditDialog}
                onClose={() => {
                    setOpenEditDialog(false);
                    setTeamToEdit(null);
                }}
                fullWidth
                maxWidth="sm"
            >
                <DialogTitle sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                    Редактирование команды
                    <IconButton onClick={() => {
                        setOpenEditDialog(false);
                        setTeamToEdit(null);
                    }}>
                        <Close/>
                    </IconButton>
                </DialogTitle>

                <form onSubmit={handleSubmit(handleUpdateTeam)}>
                    <DialogContent sx={{py: 3}}>
                        <Controller
                            name="teamName"
                            control={control}
                            defaultValue={teamToEdit?.teamName || ""}
                            rules={{required: "Название обязательно"}}
                            render={({field, fieldState}) => (
                                <TextField
                                    {...field}
                                    label="Название команды"
                                    variant="outlined"
                                    fullWidth
                                    margin="normal"
                                    required
                                    error={!!fieldState.error}
                                    helperText={fieldState.error?.message}
                                />
                            )}
                        />

                        <Controller
                            name="country"
                            control={control}
                            defaultValue={teamToEdit?.country || ""}
                            rules={{ required: "Страна обязательна" }}
                            render={({ field, fieldState }) => (
                                <TextField
                                    {...field}
                                    label="Страна"
                                    variant="outlined"
                                    fullWidth
                                    margin="normal"
                                    required
                                    error={!!fieldState.error}
                                    helperText={fieldState.error?.message}
                                />
                            )}
                        />
                    </DialogContent>

                    <DialogActions sx={{ px: 3, pb: 2 }}>
                        <Button
                            type="submit"
                            variant="contained"
                            startIcon={<CheckCircle />}
                            sx={{
                                bgcolor: '#2e7d32',
                                '&:hover': { bgcolor: '#1b5e20' },
                                borderRadius: '8px',
                                px: 3
                            }}
                        >
                            Сохранить изменения
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>
            <Dialog
                open={openDialog}
                onClose={() => setOpenDialog(false)}
                fullWidth
                maxWidth="sm"
            >
                <DialogTitle sx={{display: 'flex', justifyContent: 'space-between', alignItems: 'center'}}>
                    Создание новой команды
                    <IconButton onClick={() => setOpenDialog(false)}>
                        <Close/>
                    </IconButton>
                </DialogTitle>

                <form onSubmit={handleSubmit(handleCreateTeam)}>
                    <DialogContent sx={{py: 3}}>
                        <Controller
                            name="teamName" // Измените с "name" на "teamName"
                            control={control}
                            defaultValue=""
                            rules={{required: "Название обязательно"}}
                            render={({field, fieldState}) => (
                                <TextField
                                    {...field}
                                    label="Название команды"
                                    variant="outlined"
                                    fullWidth
                                    margin="normal"
                                    required
                                    error={!!fieldState.error}
                                    helperText={fieldState.error?.message}
                                />
                            )}
                        />

                        <Controller
                            name="country"
                            control={control}
                            defaultValue=""
                            rules={{ required: "Страна обязательна" }}
                            render={({ field, fieldState }) => (
                                <TextField
                                    {...field}
                                    label="Страна"
                                    variant="outlined"
                                    fullWidth
                                    margin="normal"
                                    required
                                    error={!!fieldState.error}
                                    helperText={fieldState.error?.message}
                                />
                            )}
                        />
                    </DialogContent>

                    <DialogActions sx={{ px: 3, pb: 2 }}>
                        <Button
                            type="submit"
                            variant="contained"
                            startIcon={<CheckCircle />}
                            sx={{
                                bgcolor: '#2e7d32',
                                '&:hover': { bgcolor: '#1b5e20' },
                                borderRadius: '8px',
                                px: 3
                            }}
                        >
                            Создать команду
                        </Button>
                    </DialogActions>
                </form>
            </Dialog>


            <TeamPlayersDialog
                open={openPlayersDialog}
                players={currentTeamPlayers}
                onClose={() => setOpenPlayersDialog(false)}
                onRemovePlayer={handleRemovePlayer}
            />
            <TeamMatchesDialog
                open={openMatchesDialog}
                matches={currentTeamMatches}
                team={selectedTeam}
                onClose={() => setOpenMatchesDialog(false)}
            />
            <AddPlayerDialog
                open={openAddPlayerDialog}
                players={players.filter(player => {
                    const team = teams.find(t => t.id === Number(selectedTeamForAddPlayer));
                    if (!team) return false;
                    return !team.playerDtoList?.some(teamPlayer => teamPlayer.id === player.id);
                })}
                team={teams.find(t => t.id === Number(selectedTeamForAddPlayer))}
                onClose={handleCloseAddPlayer}
                onAddPlayer={handleAddPlayer}
            />
            <Snackbar
                open={snackbar.open}
                autoHideDuration={4000}
                onClose={() => setSnackbar(s => ({ ...s, open: false }))}
                message={snackbar.message}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
                action={
                    <IconButton
                        size="small"
                        aria-label="close"
                        color="inherit"
                        onClick={() => setSnackbar(s => ({ ...s, open: false }))}
                    >
                        <Close fontSize="small" />
                    </IconButton>
                }
            />
        </Container>
    );
}