import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { 
  Container, 
  List, 
  ListItem, 
  ListItemText, 
  Button, 
  TextField,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Tabs,
  Tab,
  Box,
  Typography,
  AppBar,
  Toolbar,
  IconButton,
  CircularProgress
} from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useAuth } from '../contexts/AuthContext';
import { useError } from '../hooks/useError';
import AlertSnackbar from '../components/AlertSnackbar';

const LoadingSpinner = () => (
  <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
    <CircularProgress />
  </Box>
);

function RoomList() {
  const navigate = useNavigate();
  const { logout, user } = useAuth();
  const { error, handleError, setError } = useError();
  const [loading, setLoading] = useState(false);
  const [rooms, setRooms] = useState([]);
  const [myRooms, setMyRooms] = useState([]);
  const [open, setOpen] = useState(false);
  const [newRoomName, setNewRoomName] = useState('');
  const [tabValue, setTabValue] = useState(0);

  const fetchRooms = useCallback(async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        console.error('토큰이 없습니다');
        navigate('/login');
        return;
      }

      const headers = new Headers({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      });

      const response = await fetch('http://localhost:8080/api/room/list', { 
        method: 'GET',
        headers: headers,
        mode: 'cors',
        credentials: 'include'
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const responseData = await response.json();
      console.log('Rooms response:', responseData);
      
      // data 필드에서 배열 추출
      const rooms = responseData.data || [];
      setRooms(Array.isArray(rooms) ? rooms : []);

    } catch (error) {
      console.error('Fetch error:', error);
      handleError(error);
    }
  }, [handleError, navigate]);

  const fetchMyRooms = useCallback(async () => {
    try {
      const token = localStorage.getItem('token');
      if (!token) {
        console.error('토큰이 없습니다');
        navigate('/login');
        return;
      }

      const headers = new Headers({
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      });

      const response = await fetch('http://localhost:8080/api/room/myList', { 
        method: 'GET',
        headers: headers,
        mode: 'cors',
        credentials: 'include'
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }
      
      const responseData = await response.json();
      console.log('MyRooms response:', responseData);
      
      // data 필드에서 배열 추출
      const myRooms = responseData.data || [];
      setMyRooms(Array.isArray(myRooms) ? myRooms : []);

    } catch (error) {
      console.error('Fetch error:', error);
      handleError(error);
    }
  }, [handleError, navigate]);

  useEffect(() => {
    Promise.all([fetchRooms(), fetchMyRooms()]).catch(error => {
      console.error('Error fetching data:', error);
    });
  }, [fetchRooms, fetchMyRooms]);

  const handleRefresh = useCallback(async () => {
    await Promise.all([fetchRooms(), fetchMyRooms()]);
  }, [fetchRooms, fetchMyRooms]);

  useEffect(() => {
    handleRefresh();
  }, [handleRefresh]);

  const handleCreateRoom = async () => {
    try {
      setLoading(true);
      await fetch('http://localhost:8080/api/room', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({ roomName: newRoomName })
      });
      setOpen(false);
      setNewRoomName('');
      await Promise.all([fetchRooms(), fetchMyRooms()]);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleJoinRoom = async (roomId, isMyRoom) => {
    try {
      setLoading(true);
      if (!isMyRoom) {
        await fetch(`http://localhost:8080/api/room/${roomId}/join`, {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
      }
      navigate(`/room/${roomId}`);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  if (loading) return <LoadingSpinner />;

  return (
    <>
      <AppBar position="static">
        <Toolbar>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            채팅방 목록
          </Typography>
          <Button color="inherit" onClick={handleLogout} startIcon={<LogoutIcon />}>
            로그아웃
          </Button>
        </Toolbar>
      </AppBar>
      <Container maxWidth="md">
        <Box sx={{ width: '100%', mt: 2 }}>
          <Box sx={{ borderBottom: 1, borderColor: 'divider', mb: 2, display: 'flex', alignItems: 'center' }}>
            <Tabs value={tabValue} onChange={(e, newValue) => setTabValue(newValue)}>
              <Tab label="내 채팅방" />
              <Tab label="전체 채팅방" />
            </Tabs>
            <IconButton 
              onClick={handleRefresh} 
              disabled={loading}
              sx={{ ml: 'auto' }}
            >
              <RefreshIcon />
            </IconButton>
          </Box>

          <Button 
            variant="contained" 
            onClick={() => setOpen(true)}
            sx={{ my: 2 }}
          >
            새 채팅방 만들기
          </Button>

          {tabValue === 0 && (
            <List>
              {Array.isArray(myRooms) && myRooms.length > 0 ? (
                myRooms.map((room) => (
                  <ListItem 
                    key={room.id}
                    button 
                    onClick={() => handleJoinRoom(room.id, true)}
                    sx={{ 
                      mb: 1,
                      border: 1,
                      borderColor: 'divider',
                      borderRadius: 1
                    }}
                  >
                    <ListItemText 
                      primary={room.name} 
                      secondary={`참여자 수: ${room.participantCount}`} 
                    />
                  </ListItem>
                ))
              ) : (
                <Typography color="text.secondary" sx={{ mt: 2, textAlign: 'center' }}>
                  참여 중인 채팅방이 없습니다.
                </Typography>
              )}
            </List>
          )}

          {tabValue === 1 && (
            <List>
              {Array.isArray(rooms) && rooms.length > 0 ? (
                rooms.map((room) => (
                  <ListItem 
                    key={room.id}
                    button 
                    onClick={() => handleJoinRoom(room.id, false)}
                    sx={{ 
                      mb: 1,
                      border: 1,
                      borderColor: 'divider',
                      borderRadius: 1
                    }}
                  >
                    <ListItemText 
                      primary={room.name} 
                      secondary={`참여자 수: ${room.participantCount}`} 
                    />
                  </ListItem>
                ))
              ) : (
                <Typography color="text.secondary" sx={{ mt: 2, textAlign: 'center' }}>
                  생성된 채팅방이 없습니다.
                </Typography>
              )}
            </List>
          )}
        </Box>

        <Dialog open={open} onClose={() => setOpen(false)}>
          <DialogTitle>새로운 채팅방 만들기</DialogTitle>
          <DialogContent>
            <TextField
              autoFocus
              margin="dense"
              label="방 이름"
              fullWidth
              value={newRoomName}
              onChange={(e) => setNewRoomName(e.target.value)}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setOpen(false)}>취소</Button>
            <Button onClick={handleCreateRoom}>만들기</Button>
          </DialogActions>
        </Dialog>

        <AlertSnackbar
          open={Boolean(error)}
          message={error}
          severity="error"
          onClose={() => setError(null)}
        />
      </Container>
    </>
  );
}

export default RoomList; 