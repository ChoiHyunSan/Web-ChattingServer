import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { debounce } from 'lodash';
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
import axios from '../api/axios';

const LoadingSpinner = () => (
  <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
    <CircularProgress />
  </Box>
);

function RoomList() {
  const navigate = useNavigate();
  const { logout, user } = useAuth();
  const { error, handleError, setError } = useError();
  const [rooms, setRooms] = useState([]);
  const [myRooms, setMyRooms] = useState([]);
  const [loading, setLoading] = useState(false);
  const [refreshing, setRefreshing] = useState(false);
  const [open, setOpen] = useState(false);
  const [newRoomName, setNewRoomName] = useState('');
  const [tabValue, setTabValue] = useState(0);
  const [errorCount, setErrorCount] = useState(0);

  const fetchData = useCallback(async () => {
    try {
      if (errorCount > 3) {
        console.log('Too many errors, stopping requests');
        return;
      }

      setLoading(true);
      const roomsResponse = await axios.get('/api/room/list');
      const myRoomsResponse = await axios.get('/api/room/myList');
      
      setRooms(roomsResponse.data.data || []);
      setMyRooms(myRoomsResponse.data.data || []);
      setErrorCount(0);
    } catch (error) {
      handleError(error);
      setErrorCount(prev => prev + 1);
    } finally {
      setLoading(false);
    }
  }, [handleError, errorCount]);

  const debouncedFetchData = useCallback(
    debounce(() => {
      fetchData();
    }, 1000),
    [fetchData]
  );

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  const handleRefresh = useCallback(async () => {
    try {
      setRefreshing(true);
      setErrorCount(0);
      await fetchData();
    } finally {
      setRefreshing(false);
    }
  }, [fetchData]);

  const handleCreateRoom = async () => {
    try {
      setLoading(true);
      await axios.post('/api/room', { roomName: newRoomName });
      setOpen(false);
      setNewRoomName('');
      await fetchData();
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
        await axios.post(`/api/room/${roomId}/join`);
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
              disabled={loading || refreshing}
              sx={{ ml: 'auto' }}
            >
              {refreshing ? <CircularProgress size={24} /> : <RefreshIcon />}
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
                  {error ? '데이터를 불러오는데 실패했습니다.' : '참여 중인 채팅방이 없습니다.'}
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
                  {error ? '데이터를 불러오는데 실패했습니다.' : '생성된 채팅방이 없습니다.'}
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