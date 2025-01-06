import { useState, useEffect } from 'react';
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
  IconButton
} from '@mui/material';
import LogoutIcon from '@mui/icons-material/Logout';
import RefreshIcon from '@mui/icons-material/Refresh';
import { useAuth } from '../contexts/AuthContext';
import axios from '../api/axios';
import LoadingSpinner from '../components/LoadingSpinner';
import { useError } from '../hooks/useError';
import AlertSnackbar from '../components/AlertSnackbar';

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

  useEffect(() => {
    fetchRooms();
    fetchMyRooms();
  }, []);

  const fetchRooms = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/room/list');
      setRooms(response.data.data);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  const fetchMyRooms = async () => {
    try {
      setLoading(true);
      const response = await axios.get('/room/myList');
      setMyRooms(response.data.data);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleCreateRoom = async () => {
    try {
      setLoading(true);
      await axios.post('/room', { roomName: newRoomName });
      setOpen(false);
      setNewRoomName('');
      await Promise.all([fetchRooms(), fetchMyRooms()]);
    } catch (error) {
      handleError(error);
    } finally {
      setLoading(false);
    }
  };

  const handleJoinRoom = async (roomId) => {
    try {
      setLoading(true);
      await axios.post(`/room/${roomId}/join`);
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

  const handleRefresh = async () => {
    try {
      setLoading(true);
      const [allRoomsResponse, myRoomsResponse] = await Promise.all([
        axios.get('/room/list'),
        axios.get('/room/myList')
      ]);

      const allRoomsData = await allRoomsResponse.data;
      const myRoomsData = await myRoomsResponse.data;

      setRooms(allRoomsData.data);
      setMyRooms(myRoomsData.data);
    } catch (error) {
      handleError(new Error('채팅방 목록을 불러오는데 실패했습니다.'));
    } finally {
      setLoading(false);
    }
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
              {myRooms.map((room) => (
                <ListItem 
                  key={room.id}
                  button 
                  onClick={() => handleJoinRoom(room.id)}
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
              ))}
              {myRooms.length === 0 && (
                <Typography color="text.secondary" sx={{ mt: 2, textAlign: 'center' }}>
                  참여 중인 채팅방이 없습니다.
                </Typography>
              )}
            </List>
          )}

          {tabValue === 1 && (
            <List>
              {rooms.map((room) => (
                <ListItem 
                  key={room.id}
                  button 
                  onClick={() => handleJoinRoom(room.id)}
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
              ))}
              {rooms.length === 0 && (
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