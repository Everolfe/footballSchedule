import { useEffect, useState } from "react";
import {
    TextField,
    Button,
    Paper,
    CircularProgress,
    Grid,
    Card,
    CardContent,
    Typography,
    Avatar,
    Divider,
    Snackbar,
    Alert,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    IconButton,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
    Box
} from "@mui/material";
import {
    Person as PersonIcon,
    Cake as AgeIcon,
    Flag as NationalityIcon,
    SportsSoccer as SoccerIcon,
    Add as AddIcon,
    Delete as DeleteIcon,
    Edit as EditIcon,
    Search as SearchIcon
} from "@mui/icons-material";
import styled from "@emotion/styled";
import * as playerApi from "../services/playerService";
import * as teamApi from "../services/teamService";

const Container = styled.div`
    max-width: 1280px;
    margin: 0 auto;
    padding: 20px;
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

const PlayersList = styled.div`
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 20px;
    margin-top: 24px;
`;

const PlayerCard = styled(Card)`
    border-radius: 12px;
    transition: all 0.3s ease;
    &:hover {
        transform: translateY(-5px);
        box-shadow: 0 10px 20px rgba(0, 0, 0, 0.1);
    }
`;

const PlayerHeader = styled.div`
    display: flex;
    align-items: center;
    gap: 16px;
    margin-bottom: 12px;
`;

const PlayerInfo = styled.div`
    display: flex;
    flex-direction: column;
    flex-grow: 1;
`;

const InfoItem = styled.div`
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    color: #555;
`;

const TeamBadge = styled.div`
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 8px;
    background-color: #f5f5f5;
    border-radius: 8px;
    margin-top: 12px;
`;

export default function PlayerPage() {
    const [players, setPlayers] = useState([]);
    const [teams, setTeams] = useState([]);
    const [loading, setLoading] = useState(true);
    const [formData, setFormData] = useState({
        name: '',
        age: '',
        country: '',
        teamId: ''
    });
    const resetFilters = () => {
        setSearchTerm("");
        setAgeFilter("");
        fetchData(); // Загружаем всех игроков заново
    };
    const [editingPlayer, setEditingPlayer] = useState(null);
    const [openDialog, setOpenDialog] = useState(false);
    const [playerToDelete, setPlayerToDelete] = useState(null);
    const [ageFilter, setAgeFilter] = useState('');
    const [snackbar, setSnackbar] = useState({
        open: false,
        message: "",
        severity: "success"
    });
    const [searchTerm, setSearchTerm] = useState("");

    const fetchData = async () => {
        setLoading(true);
        try {
            const [playersRes, teamsRes] = await Promise.all([
                playerApi.getAllPlayers(),
                teamApi.getAllTeams()
            ]);
            setPlayers(playersRes.data);
            setTeams(teamsRes.data);
        } catch (error) {
            showSnackbar("Ошибка при загрузке данных", "error");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    const handleAgeFilterChange = (e) => {
        setAgeFilter(e.target.value);
    };

    const handleSearchByAge = async () => {
        if (!ageFilter) {
            fetchData(); // Если фильтр пустой, загружаем всех игроков
            return;
        }

        setLoading(true);
        try {
            const res = await playerApi.getPlayersByAge(parseInt(ageFilter));
            setPlayers(res.data);
        } catch (error) {
            showSnackbar("Ошибка при поиске по возрасту", "error");
        } finally {
            setLoading(false);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        // Если поле name - это teamId, то мы превращаем его в целое число
        setFormData(prev => ({
            ...prev,
            [name] : value
        }));
    };

    const handleSubmit = async () => {
        const { name, age, teamId } = formData;

        if (!name || !age) {
            showSnackbar("Заполните обязательные поля", "warning");
            return;
        }

        try {
            const playerData = {
                name: formData.name,
                age: parseInt(formData.age),
                country: formData.country,
                teamId: teamId ? parseInt(teamId) : null // Просто передаем ID
            };

            if (editingPlayer) {
                await playerApi.updatePlayer(editingPlayer.id, playerData);
                showSnackbar("Данные игрока обновлены", "success");
            } else {
                await playerApi.createPlayer(playerData);
                showSnackbar("Игрок успешно добавлен", "success");
            }

            await fetchData();
            resetForm();
        } catch (error) {
            console.error(error);
            showSnackbar(error.response?.data?.message || "Ошибка при сохранении", "error");
        }
    };



    const handleEdit = (player) => {
        setEditingPlayer(player);
        setFormData({
            name: player.name,
            age: player.age.toString(),
            country: player.country || '',
            teamId: player.teamDtoWithMatches?.id?.toString() || ''
        });
    };

    const resetForm = () => {
        setFormData({
            name: '',
            age: '',
            country: '',
            teamId: ''
        });
        setEditingPlayer(null);
    };

    const confirmDelete = (id) => {
        setPlayerToDelete(id);
        setOpenDialog(true);
    };

    const handleDelete = async () => {
        try {
            await playerApi.deletePlayer(playerToDelete); // Убедитесь, что API ожидает число или строку
            await fetchData();
            setOpenDialog(false);
            showSnackbar("Игрок удален", "success");
        } catch (error) {
            console.log("Deleting player with ID:", playerToDelete, "Type:", typeof playerToDelete);
            showSnackbar(`Ошибка при удалении игрока: ${error.message}`, "error");
        } finally {
            setPlayerToDelete(null); // Сбрасываем ID после удаления
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleCloseSnackbar = () => {
        setSnackbar(prev => ({ ...prev, open: false }));
    };

    const filteredPlayers = players.filter(player => {
        const searchLower = searchTerm.toLowerCase();
        const matchesSearch = player.name?.toLowerCase().includes(searchLower) ||
            (player.country?.toLowerCase().includes(searchLower)) ||
            (player.teamDtoWithMatches?.teamName?.toLowerCase().includes(searchLower));

        const matchesAge = ageFilter ? player.age === parseInt(ageFilter) : true;

        return matchesSearch && matchesAge;
    });

    return (
        <Container>
            <Header>
                <Typography variant="h4" sx={{ fontWeight: "bold", color: "#2e7d32" }}>
                    Управление игроками
                </Typography>
                <Box display="flex" gap={2}>
                    <TextField
                        variant="outlined"
                        size="small"
                        placeholder="Поиск игроков..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        InputProps={{
                            startAdornment: <SearchIcon color="action" sx={{ mr: 1 }} />,
                        }}
                        sx={{ width: 260 }}
                    />
                    <Box display="flex" gap={1}>
                        <TextField
                            variant="outlined"
                            size="small"
                            placeholder="Возраст"
                            type="number"
                            value={ageFilter}
                            onChange={handleAgeFilterChange}
                            InputProps={{
                                startAdornment: <AgeIcon color="action" sx={{ mr: 1 }} />,
                            }}
                            sx={{ width: 140 }}
                        />
                        <Button
                            variant="contained"
                            onClick={handleSearchByAge}
                            sx={{
                                bgcolor: '#2e7d32',
                                '&:hover': { bgcolor: '#1b5e20' }
                            }}
                        >
                            Найти
                        </Button>
                        <Button
                            variant="outlined"
                            onClick={resetFilters}
                            sx={{
                                borderColor: '#b31e1e',
                                color: '#b31e1e',
                                '&:hover': { borderColor: '#b31e1e', color: '#b31e1e' },
                                minWidth: '100px'
                            }}
                        >
                            Сбросить
                        </Button>
                    </Box>
                </Box>
            </Header>

            <ActionBar elevation={0}>
                <Typography variant="h6" sx={{ marginBottom: 2, color: "#2e7d32" }}>
                    {editingPlayer ? "Редактировать игрока" : "Добавить нового игрока"}
                </Typography>

                <Grid container spacing={2} alignItems="center">
                    {/* Поле для имени игрока */}
                    <Grid item xs={12} sm={6} md={3} lg={2.4}>
                        <TextField
                            fullWidth
                            label="Имя игрока"
                            name="name"
                            value={formData.name}
                            onChange={handleInputChange}
                            variant="outlined"
                            size="small"
                            InputProps={{
                                startAdornment: <PersonIcon color="action" sx={{ mr: 1 }} />,
                            }}
                        />
                    </Grid>

                    {/* Поле для возраста */}
                    <Grid item xs={12} sm={6} md={3} lg={2.4}>
                        <TextField
                            fullWidth
                            label="Возраст"
                            name="age"
                            type="number"
                            value={formData.age}
                            onChange={handleInputChange}
                            variant="outlined"
                            size="small"
                            InputProps={{
                                startAdornment: <AgeIcon color="action" sx={{ mr: 1 }} />,
                            }}
                        />
                    </Grid>

                    {/* Поле для страны */}
                    <Grid item xs={12} sm={6} md={3} lg={2.4}>
                        <TextField
                            fullWidth
                            label="Страна"
                            name="country"
                            value={formData.country}
                            onChange={handleInputChange}
                            variant="outlined"
                            size="small"
                            InputProps={{
                                startAdornment: <NationalityIcon color="action" sx={{ mr: 1 }} />,
                            }}
                        />
                    </Grid>

                    {/* Поле для команды */}
                    <Grid item xs={12} sm={6} md={3} lg={2.4}>
                        <FormControl size="small" sx={{ width: '200px' }}>
                            <InputLabel>Команда</InputLabel>
                            <Select
                                name="teamId"
                                value={formData.teamId}
                                onChange={handleInputChange}
                                label="Команда"
                                renderValue={(selected) => {
                                    if (!selected) return "Без команды";
                                    const team = teams.find(t => t.id.toString() === selected);
                                    return team ? team.teamName : "Без команды";
                                }}
                            >
                                <MenuItem value="">
                                    <em>Без команды</em>
                                </MenuItem>
                                {teams.map(team => (
                                    <MenuItem key={team.id} value={team.id.toString()}>
                                        {team.teamName}
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </Grid>

                    {/* Кнопки действий */}
                    <Grid item xs={12} sm={6} md={3} lg={2.4}>
                        <Box display="flex" gap={1}>
                            <Button
                                variant="contained"
                                startIcon={<AddIcon />}
                                onClick={handleSubmit}
                                fullWidth
                                sx={{
                                    bgcolor: '#2e7d32',
                                    '&:hover': { bgcolor: '#2e7d32' },
                                    borderRadius: '8px',
                                    height: '40px',
                                    minWidth: '120px'
                                }}
                            >
                                {editingPlayer ? "Обновить" : "Добавить"}
                            </Button>
                            <Button
                                variant="outlined"
                                onClick={resetForm}
                                sx={{
                                    borderRadius: '8px',
                                    height: '40px',
                                    minWidth: '100px',
                                    borderColor: '#b31e1e',
                                    color: '#b31e1e',
                                    '&:hover': {
                                        borderColor: '#b31e1e',
                                        color: '#b31e1e'
                                    }
                                }}
                            >
                                {editingPlayer ? "Отмена" : "Сбросить"}
                            </Button>
                        </Box>
                    </Grid>
                </Grid>
            </ActionBar>

            {loading ? (
                <div style={{ textAlign: "center", padding: 40 }}>
                    <CircularProgress size={60} thickness={4} sx={{ color: "#2e7d32" }} />
                </div>
            ) : (
                <>
                    <Typography variant="h6" sx={{ marginBottom: 2, color: "#555" }}>
                        Все игроки ({filteredPlayers.length})
                    </Typography>

                    {filteredPlayers.length === 0 ? (
                        <Paper elevation={0} sx={{ p: 4, textAlign: "center", borderRadius: 3, bgcolor: "#f8f9fa" }}>
                            <Typography variant="body1" color="textSecondary">
                                Игроки не найдены. Измените параметры поиска или добавьте нового игрока.
                            </Typography>
                        </Paper>
                    ) : (
                        <PlayersList>
                            {filteredPlayers.map(player => (
                                <PlayerCard key={player.id}>
                                    <CardContent>
                                        <PlayerHeader>
                                            <Avatar sx={{ bgcolor: '#2e7d32' }}>
                                                <PersonIcon />
                                            </Avatar>
                                            <PlayerInfo>
                                                <Typography variant="h6" sx={{ fontWeight: 500 }}>
                                                    {player.name}
                                                </Typography>
                                                <Box sx={{ display: 'flex', gap: 1 }}>
                                                    <IconButton
                                                        size="small"
                                                        onClick={() => handleEdit(player)}
                                                        color="primary"
                                                    >
                                                        <EditIcon fontSize="small" />
                                                    </IconButton>
                                                    <IconButton
                                                        size="small"
                                                        onClick={() => confirmDelete(player.id)}
                                                        color="error"
                                                    >
                                                        <DeleteIcon fontSize="small" />
                                                    </IconButton>
                                                </Box>
                                            </PlayerInfo>
                                        </PlayerHeader>

                                        <Divider sx={{ my: 1 }} />

                                        <InfoItem>
                                            <AgeIcon fontSize="small" />
                                            <Typography variant="body2">
                                                Возраст: {player.age} лет
                                            </Typography>
                                        </InfoItem>

                                        {player.country && (
                                            <InfoItem>
                                                <NationalityIcon fontSize="small" />
                                                <Typography variant="body2">
                                                    Страна: {player.country}
                                                </Typography>
                                            </InfoItem>
                                        )}

                                        {player.teamDtoWithMatches && (
                                            <TeamBadge>
                                                <SoccerIcon fontSize="small" />
                                                <Typography variant="body2" sx={{ fontWeight: 500 }}>
                                                    {player.teamDtoWithMatches.teamName}
                                                </Typography>
                                            </TeamBadge>
                                        )}
                                    </CardContent>
                                </PlayerCard>
                            ))}
                        </PlayersList>
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
                        Вы уверены, что хотите удалить этого игрока?
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
    );
}