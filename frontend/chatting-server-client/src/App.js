import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import Login from './pages/Login';
import Signup from './pages/Signup';
import RoomList from './pages/RoomList';
import ChatRoom from './pages/ChatRoom';
import LoadingSpinner from './components/LoadingSpinner';
import { useAuth } from './contexts/AuthContext';
import { Box, CssBaseline } from '@mui/material';
import './App.css';

// 인증이 필요한 라우트를 위한 컴포넌트
function PrivateRoute({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  if (!user) {
    return <Navigate to="/login" />;
  }

  return children;
}

// 인증된 사용자는 접근할 수 없는 라우트를 위한 컴포넌트
function PublicRoute({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <LoadingSpinner />;
  }

  if (user) {
    return <Navigate to="/" />;
  }

  return children;
}

function AppRoutes() {
  return (
    <Box sx={{ 
      height: '100vh',
      display: 'flex',
      flexDirection: 'column',
      overflow: 'hidden'
    }}>
      <Routes>
        <Route path="/login" element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        } />
        <Route path="/signup" element={
          <PublicRoute>
            <Signup />
          </PublicRoute>
        } />
        <Route path="/" element={
          <PrivateRoute>
            <RoomList />
          </PrivateRoute>
        } />
        <Route path="/room/:roomId" element={
          <PrivateRoute>
            <ChatRoom />
          </PrivateRoute>
        } />
      </Routes>
    </Box>
  );
}

function App() {
  return (
    <AuthProvider>
      <CssBaseline />
      <Router>
        <AppRoutes />
      </Router>
    </AuthProvider>
  );
}

export default App;
