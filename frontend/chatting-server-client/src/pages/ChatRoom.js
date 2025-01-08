import React, { useState, useEffect, useRef, useCallback } from 'react';
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
  const [currentDate, setCurrentDate] = useState(new Date());
  const [loading, setLoading] = useState(false);
  const [showLoadMoreButton, setShowLoadMoreButton] = useState(false);
  const [hasMoreMessages, setHasMoreMessages] = useState(true);
  const [isLoadingPrevious, setIsLoadingPrevious] = useState(false);

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
          setMessages((prev) => [...prev, {
            sender: receivedMessage.from,
            message: receivedMessage.message,
            createdAt: receivedMessage.timestamp
          }]);
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
        client.deactivate().then(() => {
          console.log('WebSocket connection closed successfully');
        }).catch(console.error);
      }
      clientRef.current = null;
    };
  }, [roomId, handleError]);

  useEffect(() => {
    const fetchInitialMessages = async () => {
      try {
        setLoading(true);
        const formattedDate = currentDate.toISOString().split('T')[0];
        
        const response = await fetch(`http://localhost:8080/api/v1/chat/list/${roomId}/${formattedDate}`, {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        });
        
        if (!response.ok) throw new Error('Failed to fetch messages');
        
        const result = await response.json();
        setMessages(result.data);
        
      } catch (error) {
        handleError(error);
      } finally {
        setLoading(false);
      }
    };

    fetchInitialMessages();
  }, [roomId]);

  const sendMessage = () => {
    if (!message.trim() || !clientRef.current?.connected) return;

    try {
      clientRef.current.publish({
        destination: `/pub/chat/message/${roomId}`,
        body: JSON.stringify({
          from: username,
          to: roomId,
          message: message.trim(),
          timestamp: new Date().toISOString()
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

  useEffect(() => {
    const container = messageContainerRef.current;
    if (!container) return;

    const handleScroll = (e) => {
      if (e.target.scrollTop === 0) {
        setShowLoadMoreButton(true);
      } else {
        setShowLoadMoreButton(false);
      }
    };

    container.scrollTop = container.scrollHeight;

    // 스크롤 이벤트 리스너
    container.addEventListener('scroll', handleScroll);
    
    // 초기 스크롤 상태 확인
    handleScroll({ target: container });

    return () => container.removeEventListener('scroll', handleScroll);
  }, [messages]);

  const loadPreviousMessages = async () => {
    try {
      setIsLoadingPrevious(true);
      
      const container = messageContainerRef.current;
      const oldScrollHeight = container?.scrollHeight || 0;
      const oldScrollTop = container?.scrollTop || 0;

      const prevDate = new Date(currentDate);
      prevDate.setDate(prevDate.getDate() - 1);
      
      const formattedDate = prevDate.toISOString().split('T')[0];
      const response = await fetch(`http://localhost:8080/api/v1/chat/list/${roomId}/${formattedDate}`, {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
      
      if (!response.ok) throw new Error('Failed to fetch messages');
      
      const result = await response.json();
      
      if (!result.data || result.data.length === 0) {
        setHasMoreMessages(false);
        return;
      }

      setMessages(prevMessages => [...result.data, ...prevMessages]);
      setCurrentDate(prevDate);

      setTimeout(() => {
        if (container) {
          const newScrollHeight = container.scrollHeight;
          const scrollDiff = newScrollHeight - oldScrollHeight;
          container.scrollTop = oldScrollTop + scrollDiff;
        }
      }, 0);

    } catch (error) {
      handleError(error);
    } finally {
      setIsLoadingPrevious(false);
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
        {loading && <LoadingSpinner />}
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
          {showLoadMoreButton && (
            <Box sx={{ display: 'flex', justifyContent: 'center', mb: 2 }}>
              <Button
                variant="contained"
                size="small"
                onClick={loadPreviousMessages}
                disabled={!hasMoreMessages}
              >
                {hasMoreMessages ? '이전 메시지 보기' : '더 이상 메시지가 없습니다'}
              </Button>
            </Box>
          )}
          {messages.map((msg, index) => {
            const currentMessageDate = new Date(msg.createdAt).toLocaleDateString();
            const previousMessage = index > 0 ? messages[index - 1] : null;
            const previousMessageDate = previousMessage 
              ? new Date(previousMessage.createdAt).toLocaleDateString() 
              : null;
            
            const showDateDivider = previousMessageDate !== currentMessageDate;

            return (
              <React.Fragment key={index}>
                {showDateDivider && (
                  <Box 
                    sx={{ 
                      display: 'flex', 
                      alignItems: 'center', 
                      my: 2,
                      gap: 2
                    }}
                  >
                    <Box sx={{ flex: 1, height: '1px', bgcolor: 'divider' }} />
                    <Typography variant="body2" color="text.secondary">
                      {new Date(msg.createdAt).toLocaleDateString('ko-KR', {
                        year: 'numeric',
                        month: 'long',
                        day: 'numeric'
                      })}
                    </Typography>
                    <Box sx={{ flex: 1, height: '1px', bgcolor: 'divider' }} />
                  </Box>
                )}
                <Box 
                  sx={{ 
                    mb: 1,
                    alignSelf: msg.sender === username ? 'flex-end' : 'flex-start',
                    maxWidth: '70%'
                  }}
                >
                  <Box 
                    sx={{ 
                      bgcolor: msg.sender === username ? 'primary.light' : 'grey.100',
                      p: 1,
                      borderRadius: 2
                    }}
                  >
                    <Typography variant="subtitle2" color="text.secondary">
                      {msg.sender}
                    </Typography>
                    <Typography>{msg.message}</Typography>
                    <Typography 
                      variant="caption" 
                      color="text.secondary" 
                      sx={{ 
                        display: 'block',
                        textAlign: msg.sender === username ? 'right' : 'left',
                        mt: 0.5 
                      }}
                    >
                      {new Date(msg.createdAt).toLocaleTimeString('ko-KR', {
                        hour: '2-digit',
                        minute: '2-digit',
                        hour12: true
                      })}
                    </Typography>
                  </Box>
                </Box>
              </React.Fragment>
            );
          })}
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