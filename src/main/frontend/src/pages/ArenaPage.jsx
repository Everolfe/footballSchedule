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
    Chip,
    Dialog,
    DialogActions,
    DialogContent,
    DialogTitle,
    Snackbar,
    Alert,
    Divider
} from "@mui/material";
import {
    Add as AddIcon,
    Delete as DeleteIcon,
    Edit as EditIcon,
    Place as PlaceIcon,
    EventSeat as CapacityIcon,
    DateRange as YearIcon,
    Search as SearchIcon,
    Stadium as StadiumIcon
} from "@mui/icons-material";
import styled from "@emotion/styled";
import * as arenaApi from "../services/arenaService";
import * as matchApi from "../services/matchService";
import dayjs from "dayjs";
const Container = styled.div`
    max-width: 1280px;
    margin: 0 auto;
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

const ArenasList = styled.div`
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 16px;
    margin-top: 24px;
`;

const ArenaCard = styled(Card)`
    border-radius: 12px;
    transition: transform 0.2s, box-shadow 0.2s;
    position: relative;

    &:hover {
        transform: translateY(-4px);
        box-shadow: 0 8px 12px rgba(0, 0, 0, 0.1);
    }
`;

const ArenaCapacityWrapper = styled.div`
    position: absolute;
    bottom: 16px;
    right: 16px;
`;


const ArenaHeader = styled.div`
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;
`;

const ArenaCapacity = styled(Chip)`
    font-size: 16px;
    font-weight: bold;
    padding: 8px 0;
    width: 100px;
`;

const InfoItem = styled.div`
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    color: #555;
`;

const SearchBar = styled.div`
    display: flex;
    gap: 16px;
    margin-bottom: 24px;
`;

export default function ArenaPage() {
    const [arenas, setArenas] = useState([]);
    const [city, setCity] = useState("");
    const [capacity, setCapacity] = useState("");
    const [loading, setLoading] = useState(true);
    const [openDialog, setOpenDialog] = useState(false);
    const [arenaToDelete, setArenaToDelete] = useState(null);
    const [matches, setMatches] = useState([]);
    const [snackbar, setSnackbar] = useState({
        open: false,
        message: "",
        severity: "success"
    });
    const [searchTerm, setSearchTerm] = useState("");
    const [minCapacity, setMinCapacity] = useState("");
    const [maxCapacity, setMaxCapacity] = useState("");
    const [editingArena, setEditingArena] = useState(null);
    const [editCity, setEditCity] = useState("");
    const [editCapacity, setEditCapacity] = useState("");


    const fetchArenas = async () => {
        setLoading(true);
        try {
            let response;
            if (minCapacity || maxCapacity) {
                response = await arenaApi.getArenasByCapacity(
                    minCapacity || 0,
                    maxCapacity || 100000
                );
            } else {
                response = await arenaApi.getAllArenas();
            }
            setArenas(Array.from(response.data));
        } catch (error) {
            showSnackbar("Ошибка при загрузке арен", "error");
        } finally {
            setLoading(false);
        }
    };

    const fetchMatches = async () => {
        try {
            const res = await matchApi.getAllMatches();
            setMatches(res.data);
        } catch (error) {
            showSnackbar("Ошибка при загрузке матчей", "error");
        }
    };

    useEffect(() => {
        fetchArenas();
        fetchMatches(); // загружаем матчи одновременно с аренами
    }, [minCapacity, maxCapacity]);


    const handleAddArena = async () => {
        if (!city || !capacity) {
            showSnackbar("Заполните обязательные поля", "warning");
            return;
        }

        const tempId = Date.now();

        try {
            const newArena = {
                city,
                capacity: parseInt(capacity),
            };

            setArenas(prev => [
                ...prev,
                {
                    ...newArena,
                    id: tempId
                }
            ]);

            const res = await arenaApi.createArena(newArena);

            setArenas(prev =>
                prev.map(a =>
                    a.id === tempId ? res.data : a
                )
            );

            setCity("");
            setCapacity("");

            await fetchArenas();
            showSnackbar("Арена успешно добавлена", "success");
        } catch (error) {
            setArenas(prev => prev.filter(a => a.id !== tempId));
            showSnackbar("Ошибка при добавлении арены", "error");
        }
    };

    const handleUpdateArena = async (arenaId, updatedData) => {
        try {
            const res = await arenaApi.updateArena(arenaId, updatedData);

            setArenas(prev =>
                prev.map(a =>
                    a.id === arenaId ? res.data : a
                )
            );

            await fetchArenas();
            showSnackbar("Данные арены обновлены", "success");
        } catch (error) {
            showSnackbar("Ошибка при обновлении данных арены", "error");
        }
    };

    const confirmDelete = (id) => {
        setArenaToDelete(id);
        setOpenDialog(true);
    };

    const handleDelete = async () => {
        try {
            await arenaApi.deleteArena(arenaToDelete);
            setArenas(arenas.filter(a => a.id !== arenaToDelete));
            setOpenDialog(false);
            await fetchArenas();
            showSnackbar("Арена удалена", "success");
        } catch (error) {
            showSnackbar("Ошибка при удалении арены", "error");
        }
    };

    const getArenaMatches = (arenaId) => {
        return matches.filter(match => match.arenaDto?.id === arenaId);
    };

    const handleSearch = () => {
        fetchArenas();
    };

    const handleClearFilters = () => {
        setSearchTerm("");
        setMinCapacity("");
        setMaxCapacity("");
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleCloseSnackbar = () => {
        setSnackbar({ ...snackbar, open: false });
    };

    const getCapacityColor = (capacity) => {
        if (capacity >= 50000) return "success";
        if (capacity >= 30000) return "primary";
        if (capacity >= 10000) return "warning";
        return "error";
    };

    const filteredArenas = arenas.filter(arena =>
        (arena.city?.toLowerCase().includes(searchTerm.toLowerCase()))
    );



    return (
        <Container>
            <Header>
                <Typography variant="h4" sx={{ fontWeight: "bold", color: "#2e7d32" }}>
                    Управление аренами
                </Typography>
            </Header>

            <ActionBar elevation={0}>
                <Typography variant="h6" sx={{ marginBottom: 2, color: "#2e7d32" }}>
                    Добавить новую арену
                </Typography>

                <FormGrid container spacing={2}>

                    <Grid item xs={12} sm={6} md={3}>
                        <TextField
                            fullWidth
                            label="Местоположение"
                            value={city}
                            onChange={(e) => setCity(e.target.value)}
                            variant="outlined"
                            size="small"
                            InputProps={{
                                startAdornment: <PlaceIcon color="action" sx={{ mr: 1 }} />,
                            }}
                        />
                    </Grid>

                    <Grid item xs={12} sm={6} md={2}>
                        <TextField
                            fullWidth
                            label="Вместимость"
                            type="number"
                            value={capacity}
                            onChange={(e) => setCapacity(e.target.value)}
                            variant="outlined"
                            size="small"
                            InputProps={{
                                startAdornment: <CapacityIcon color="action" sx={{ mr: 1 }} />,
                            }}
                        />
                    </Grid>

                    <Grid item xs={12} sm={6} md={2}>
                        <Button
                            variant="contained"
                            startIcon={<AddIcon />}
                            onClick={handleAddArena}
                            fullWidth
                            sx={{
                                bgcolor: '#2e7d32',
                                '&:hover': { bgcolor: '#2e7d32' },
                                borderRadius: '8px',
                                height: '40px'
                            }}
                        >
                            Добавить арену
                        </Button>
                    </Grid>
                </FormGrid>
            </ActionBar>

            <SearchBar>
                <TextField
                    fullWidth
                    variant="outlined"
                    size="small"
                    placeholder="Поиск по названию или местоположению"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    InputProps={{
                        startAdornment: <SearchIcon color="action" sx={{ mr: 1 }} />,
                    }}
                    sx={{ width: 400 }}
                />

                <TextField
                    variant="outlined"
                    size="small"
                    placeholder="Мин. вместимость"
                    type="number"
                    value={minCapacity}
                    onChange={(e) => setMinCapacity(e.target.value)}
                    sx={{ width: 200 }}
                />

                <TextField
                    variant="outlined"
                    size="small"
                    placeholder="Макс. вместимость"
                    type="number"
                    value={maxCapacity}
                    onChange={(e) => setMaxCapacity(e.target.value)}
                    sx={{ width: 200 }}
                />

                <Button
                    variant="outlined"
                    onClick={handleSearch}
                    sx={{
                        borderColor: '#2e7d32',
                        color: '#2e7d32',
                        '&:hover': { borderColor: '#3e2723' },
                    }}
                >
                    Применить
                </Button>

                <Button
                    variant="outlined"
                    onClick={handleClearFilters}
                    sx={{
                        borderColor: '#b31e1e',
                        color: '#b31e1e',
                        '&:hover': { borderColor: '#b31e1e' },
                    }}
                >
                    Сбросить
                </Button>
            </SearchBar>

            {loading ? (
                <div style={{ textAlign: "center", padding: 40 }}>
                    <CircularProgress size={60} thickness={4} sx={{ color: "#2e7d32" }} />
                </div>
            ) : (
                <>
                    <Typography variant="h6" sx={{ marginBottom: 2, color: "#555" }}>
                        Все арены ({filteredArenas.length})
                    </Typography>

                    {filteredArenas.length === 0 ? (
                        <Paper elevation={0} sx={{ p: 4, textAlign: "center", borderRadius: 3, bgcolor: "#f8f9fa" }}>
                            <Typography variant="body1" color="textSecondary">
                                Арены не найдены. Измените параметры поиска или добавьте новую арену.
                            </Typography>
                        </Paper>
                    ) : (
                        <ArenasList>
                            {filteredArenas.map(arena => (
                                <ArenaCard key={arena.id}>
                                    <CardContent>
                                        <div style={{marginTop: 16}}>
                                            {getArenaMatches(arena.id).length > 0 ? (
                                                getArenaMatches(arena.id).map(match => (
                                                    <Typography>
                                                        🕒 Матч: {dayjs(match.dateTime).format("DD.MM.YYYY HH:mm")}
                                                    </Typography>
                                                ))
                                            ) : (
                                                <Typography variant="caption" color="textSecondary">
                                                    Нет назначенных матчей
                                                </Typography>
                                            )}
                                        </div>
                                        <ArenaHeader>
                                            <StadiumIcon sx={{fontSize: 32, color: "#131212"}}/>
                                            <div>
                                                <IconButton
                                                    color="primary"
                                                    onClick={() => {
                                                        setEditingArena(arena);
                                                        setEditCity(arena.city);
                                                        setEditCapacity(arena.capacity);
                                                    }}
                                                    size="small"
                                                >
                                                    <EditIcon />
                                                </IconButton>

                                                <IconButton
                                                    color="error"
                                                    onClick={() => confirmDelete(arena.id)}
                                                    size="small"
                                                >
                                                    <DeleteIcon/>
                                                </IconButton>
                                            </div>
                                        </ArenaHeader>

                                        <Divider sx={{my: 1}}/>

                                        <InfoItem>
                                            <PlaceIcon fontSize="small"/>
                                            <Typography variant="body2">
                                                {arena.city}
                                            </Typography>
                                        </InfoItem>

                                        <ArenaCapacityWrapper>
                                            <ArenaCapacity
                                                label={`${arena.capacity.toLocaleString()}`}
                                                color={getCapacityColor(arena.capacity)}
                                            />
                                        </ArenaCapacityWrapper>

                                    </CardContent>
                                </ArenaCard>
                            ))}
                        </ArenasList>
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
                        Вы уверены, что хотите удалить эту арену?
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
                open={Boolean(editingArena)}
                onClose={() => setEditingArena(null)}
            >
                <DialogTitle>Редактировать арену</DialogTitle>
                <DialogContent>
                    <TextField
                        label="Местоположение"
                        fullWidth
                        margin="normal"
                        value={editCity}
                        onChange={(e) => setEditCity(e.target.value)}
                    />
                    <TextField
                        label="Вместимость"
                        type="number"
                        fullWidth
                        margin="normal"
                        value={editCapacity}
                        onChange={(e) => setEditCapacity(e.target.value)}
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setEditingArena(null)}>Отмена</Button>
                    <Button
                        variant="contained"
                        onClick={async () => {
                            try {
                                const updated = {
                                    city: editCity,
                                    capacity: parseInt(editCapacity),
                                };
                                await handleUpdateArena(editingArena.id, updated);
                                setEditingArena(null);
                            } catch (error) {
                                showSnackbar("Ошибка при обновлении", "error");
                            }
                        }}
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
    );
}