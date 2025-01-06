import { createContext, useState, useContext, useEffect } from 'react';
import axios from '../api/axios';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  // 토큰으로 사용자 정보 가져오기
  const checkAuth = async () => {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        // 토큰 유효성 검증 API 호출
        const response = await axios.get('/auth/validate');
        setUser(response.data.data);
      } catch (error) {
        // 토큰이 유효하지 않으면 로그아웃
        localStorage.removeItem('token');
        localStorage.removeItem('username');
        setUser(null);
      }
    }
    setLoading(false);
  };

  // 로그인
  const login = async (username, password) => {
    const response = await axios.post('/auth/login', { username, password });
    const { token } = response.data.data;
    localStorage.setItem('token', token);
    localStorage.setItem('username', username);
    setUser({ username });
    return response.data;
  };

  // 로그아웃
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    setUser(null);
  };

  useEffect(() => {
    checkAuth();
  }, []);

  return (
    <AuthContext.Provider value={{ user, login, logout, loading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
} 