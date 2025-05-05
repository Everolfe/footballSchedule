import { BrowserRouter as Router, Routes, Route, Link, useLocation, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import ArenaPage from "./pages/ArenaPage";
import MatchPage from "./pages/MatchPage";
import TeamPage from "./pages/TeamPage";
import PlayerPage from "./pages/PlayerPage";
import {
  Stadium as ArenaIcon,
  SportsSoccer as MatchIcon,
  Groups as TeamIcon,
  Person as PlayerIcon,
  Dashboard as DashboardIcon,
  Menu as MenuIcon,
  Close as CloseIcon
} from "@mui/icons-material";
import styled from "@emotion/styled";

// Стилевые компоненты
const AppContainer = styled.div`
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background-color: #f8f9fa;
`;

const Sidebar = styled.aside`
  width: 240px;
  background: #1c5519;
  color: white;
  padding: 1.5rem 0;
  height: 100vh;
  position: fixed;
  left: ${props => props.isOpen ? '0' : '-240px'};
  top: 0;
  z-index: 1000;
  transition: left 0.3s ease;
  box-shadow: 2px 0 10px rgba(0, 0, 0, 0.1);

  @media (min-width: 1024px) {
    left: 0;
  }
`;

const Logo = styled.div`
  padding: 0 1.5rem 1.5rem;
  margin-bottom: 1.5rem;
  font-size: 1.5rem;
  font-weight: bold;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  display: flex;
  align-items: center;
  gap: 0.75rem;
  cursor: pointer;
  transition: opacity 0.2s ease;

  &:hover {
    opacity: 0.8;
  }
`;

const NavMenu = styled.nav`
  display: flex;
  flex-direction: column;
`;

const NavItem = styled(Link)`
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.75rem 1.5rem;
  color: ${props => props.active ? 'white' : 'rgba(255, 255, 255, 0.7)'};
  background: ${props => props.active ? 'rgba(255, 255, 255, 0.1)' : 'transparent'};
  border-left: 4px solid ${props => props.active ? '#4fc3f7' : 'transparent'};
  text-decoration: none;
  transition: all 0.2s ease;
  font-weight: ${props => props.active ? 'bold' : 'normal'};

  &:hover {
    background: rgba(255, 255, 255, 0.1);
    color: white;
  }
`;

const Content = styled.main`
  flex: 1;
  padding: 2rem;
  margin-left: 0;
  transition: margin-left 0.3s ease;

  @media (min-width: 1024px) {
    margin-left: 240px;
  }
`;

const TopBar = styled.header`
  display: flex;
  align-items: center;
  padding: 1rem;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 10;
  margin-left: 0;
  transition: margin-left 0.3s ease;

  @media (min-width: 1024px) {
    margin-left: 240px;
  }
`;

const MenuButton = styled.button`
  background: none;
  border: none;
  color: #15491b;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;

  @media (min-width: 1024px) {
    display: none;
  }
`;

const PageTitle = styled.h1`
  margin: 0 0 0 1rem;
  font-size: 1.25rem;
  color: #1b531a;
`;

const MobileOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  z-index: 999;
  display: ${props => props.isOpen ? 'block' : 'none'};

  @media (min-width: 1024px) {
    display: none;
  }
`;

function NavigationItem({ to, icon, label }) {
  const location = useLocation();
  const isActive = location.pathname === to;

  return (
      <NavItem to={to} active={isActive ? 1 : 0}>
        {icon}
        {label}
      </NavItem>
  );
}

function MainLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const location = useLocation();
  const navigate = useNavigate();

  const toggleSidebar = () => {
    setIsSidebarOpen(!isSidebarOpen);
  };

  const closeSidebar = () => {
    setIsSidebarOpen(false);
  };

  const goToHome = () => {
    navigate("/");
  };

  useEffect(() => {
    closeSidebar();
  }, [location]);

  const getPageTitle = () => {
    return 'Расписание футбольных матчей';
  };

  return (
      <AppContainer>
        <Sidebar isOpen={isSidebarOpen}>
          <Logo onClick={goToHome}>
            <DashboardIcon />
            Расписание Футбольных Матчей
          </Logo>
          <NavMenu>
            <NavigationItem to="/arenas" icon={<ArenaIcon />} label="Арены" />
            <NavigationItem to="/teams" icon={<TeamIcon />} label="Команды" />
            <NavigationItem to="/players" icon={<PlayerIcon />} label="Игроки" />
          </NavMenu>
        </Sidebar>

        <MobileOverlay isOpen={isSidebarOpen} onClick={closeSidebar} />

        <TopBar>
          <MenuButton onClick={toggleSidebar}>
            {isSidebarOpen ? <CloseIcon /> : <MenuIcon />}
          </MenuButton>
          <PageTitle>{getPageTitle()}</PageTitle>
        </TopBar>

        <Content>
          <Routes>
            <Route path="/" element={<MatchPage />} />
            <Route path="/arenas" element={<ArenaPage />} />
            <Route path="/teams" element={<TeamPage />} />
            <Route path="/players" element={<PlayerPage />} />
          </Routes>
        </Content>
      </AppContainer>
  );
}

export default function App() {
  return (
      <Router>
        <Routes>
          <Route path="/*" element={<MainLayout />} />
        </Routes>
      </Router>
  );
}