import { createContext, useState, useContext } from 'react';
import axios from '../api/axios';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const username = localStorage.getItem('username');
    return username ? { username } : null;
  });

  // 로그인
  const login = async (username, password) => {
    try {
      const response = await axios.post('/api/auth/login', { username, password });
      console.log('로그인 응답:', response.data);
      
      const { data } = response.data;
      const { token, refreshToken, username: userName } = data;
      
      // 토큰 저장 �후 값 확인
      localStorage.setItem('token', token);
      console.log('저장된 토��:', localStorage.getItem('token'));
      
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('username', userName);
      
      setUser({ username: userName });
      return data;
    } catch (error) {
      console.error('로그인 에러:', error);
      throw error;
    }
  };

  // 로그아웃
  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('username');
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, login, logout }}>
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