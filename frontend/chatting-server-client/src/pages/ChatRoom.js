import { useState, useEffect, useRef, useCallback } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import { 
  Container, 
  TextField, 
  Button, 
  Box, 
  Typography,
  AppBar,
  Toolbar,
  IconButton 
} from '@mui/material';
import ArrowBackIcon from '@mui/icons-material/ArrowBack';
import ExitToAppIcon from '@mui/icons-material/ExitToApp';
import { useError } from '../hooks/useError';
import AlertSnackbar from '../components/AlertSnackbar';
import LoadingSpinner from '../components/LoadingSpinner';

function ChatRoom() {
  const navigate = useNavigate();
  const { roomId } = useParams();
  const { error, handleError, setError } = useError();
  const [messages, setMessages] = useState([]);
  const [message, setMessage] = useState('');
  const [connecting, setConnecting] = useState(true);
  const clientRef = useRef(null);
  const username = localStorage.getItem('username');
  const messageContainerRef = useRef(null);

  const scrollToBottom = useCallback(() => {
    if (messageContainerRef.current) {
      setTimeout(() => {
        const { scrollHeight } = messageContainerRef.current;
        messageContainerRef.current.scrollTo({
          top: scrollHeight,
          behavior: 'smooth'
        });
      }, 0);
    }
  }, []);

  useEffect(() => {
    if (messages.length > 0) {
      scrollToBottom();
    }
  }, [messages, scrollToBottom]);

  useEffect(() => {
    let mounted = true;
    let reconnectAttempts = 0;
    const maxReconnectAttempts = 3;

    const client = new Client({
      brokerURL: 'ws://localhost:8080/ws-stomp',
      connectHeaders: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      debug: function (str) {
        console.log('STOMP: ' + str);
      },
      onConnect: () => {
        if (!mounted) return;
        console.log('WebSocket Connected');
        reconnectAttempts = 0;
        
        client.subscribe(`/sub/chat/room/${roomId}`, (message) => {
          if (!mounted) return;
          const receivedMessage = JSON.parse(message.body);
          setMessages((prev) => [...prev, receivedMessage]);
        });
        setConnecting(false);
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame);
        handleError(new Error('STOMP 프로토콜 에러가 발생했습니다.'));
        setConnecting(false);
      },
      onWebSocketError: (event) => {
        console.error('WebSocket Error:', event);
        reconnectAttempts++;
        if (reconnectAttempts >= maxReconnectAttempts) {
          setConnecting(false);
          handleError(new Error('서버 연결에 실패했습니다. 잠시 후 다시 시도해주세요.'));
          if (client.active) {
            client.deactivate();
          }
        }
      },
      onWebSocketClose: () => {
        console.log('WebSocket Connection Closed');
        if (mounted && reconnectAttempts >= maxReconnectAttempts) {
          setConnecting(false);
        }
      },
      reconnectDelay: 30000,
      heartbeatIncoming: 25000,
      heartbeatOutgoing: 25000,
    });

    try {
      console.log('Attempting to connect to WebSocket...');
      client.activate();
      clientRef.current = client;
    } catch (error) {
      console.error('Connection Error:', error);
      handleError(error);
      setConnecting(false);
    }

    return () => {
      console.log('Cleaning up WebSocket connection...');
      mounted = false;
      if (client.active) {
        client.deactivate();
      }
      clientRef.current = null;
    };
  }, [roomId, handleError]);

  const sendMessage = () => {
    if (!message.trim() || !clientRef.current?.connected) return;

    try {
      clientRef.current.publish({
        destination: `/pub/chat/message/${roomId}`,
        body: JSON.stringify({
          from: username,
          to: roomId,
          message: message.trim()
        }),
      });
      setMessage('');
    } catch (error) {
      handleError(new Error('메시지 전송에 실패했습니다.'));
    }
  };

  const handleBack = () => {
    if (clientRef.current?.connected) {
      clientRef.current.deactivate();
    }
    navigate('/');
  };

  const handleLeaveRoom = async () => {
    if (!window.confirm('정말로 방을 나가시겠습니까?')) {
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/room/${roomId}/leave`, {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });

      if (!response.ok) {
        throw new Error('방 나가기에 실패했습니다.');
      }

      if (clientRef.current?.connected) {
        clientRef.current.deactivate();
      }
      navigate('/');
    } catch (error) {
      handleError(new Error('방 나가기에 실패했습니다.'));
    }
  };

  if (connecting) return <LoadingSpinner />;

  return (
    <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
      <AppBar position="static">
        <Toolbar>
          <IconButton
            edge="start"
            color="inherit"
            onClick={handleBack}
            sx={{ mr: 2 }}
          >
            <ArrowBackIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            채팅방
          </Typography>
          <IconButton
            color="inherit"
            onClick={handleLeaveRoom}
          >
            <ExitToAppIcon />
          </IconButton>
        </Toolbar>
      </AppBar>

      <Box sx={{ 
        flexGrow: 1, 
        display: 'flex', 
        flexDirection: 'column',
        p: 2,
        gap: 2,
        overflow: 'hidden'
      }}>
        <Box
          ref={messageContainerRef}
          sx={{
            flexGrow: 1,
            overflowY: 'auto',
            display: 'flex',
            flexDirection: 'column',
            bgcolor: 'background.paper',
            borderRadius: 1,
            boxShadow: 1,
            p: 2,
            '&::-webkit-scrollbar': {
              width: '8px',
            },
            '&::-webkit-scrollbar-track': {
              background: '#f1f1f1',
              borderRadius: '4px',
            },
            '&::-webkit-scrollbar-thumb': {
              background: '#888',
              borderRadius: '4px',
            },
          }}
        >
          {messages.map((msg, index) => (
            <Box 
              key={index} 
              sx={{ 
                mb: 1,
                alignSelf: msg.from === username ? 'flex-end' : 'flex-start',
                maxWidth: '70%'
              }}
            >
              <Box 
                sx={{ 
                  bgcolor: msg.from === username ? 'primary.light' : 'grey.100',
                  p: 1,
                  borderRadius: 2
                }}
              >
                <Typography variant="subtitle2" color="text.secondary">
                  {msg.from}
                </Typography>
                <Typography>{msg.message}</Typography>
              </Box>
            </Box>
          ))}
        </Box>

        <Box 
          sx={{ 
            display: 'flex', 
            gap: 1,
            bgcolor: 'background.paper',
            borderRadius: 1,
            p: 1
          }}
        >
          <TextField
            fullWidth
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && !e.shiftKey && sendMessage()}
            placeholder="메시지를 입력하세요..."
            disabled={!clientRef.current?.connected}
            multiline
            maxRows={4}
            size="small"
            sx={{ 
              '& .MuiOutlinedInput-root': {
                bgcolor: 'background.default'
              }
            }}
          />
          <Button 
            variant="contained" 
            onClick={sendMessage}
            disabled={!clientRef.current?.connected}
          >
            전송
          </Button>
        </Box>
      </Box>

      <AlertSnackbar
        open={Boolean(error)}
        message={error}
        severity="error"
        onClose={() => setError(null)}
      />
    </Box>
  );
}

export default ChatRoom; 