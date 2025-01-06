import { useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';

export function useError() {
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleError = useCallback((error) => {
    if (error.response) {
      // HTTP 에러 응답
      switch (error.response.status) {
        case 401:
          localStorage.removeItem('token');
          localStorage.removeItem('username');
          navigate('/login');
          setError('인증이 만료되었습니다. 다시 로그인해주세요.');
          break;
        case 403:
          setError('접근 권한이 없습니다.');
          break;
        case 404:
          setError('요청하신 리소스를 찾을 수 없습니다.');
          break;
        default:
          setError(error.response.data?.message || '알 수 없는 오류가 발생했습니다.');
      }
    } else if (error.request) {
      // 요청은 보냈지만 응답을 받지 못함
      setError('서버와 통신할 수 없습니다. 네트워크 연결을 확인해주세요.');
    } else {
      // 요청 설정 중 오류 발생
      setError('요청 중 오류가 발생했습니다.');
    }
  }, [navigate]);

  return { error, setError, handleError };
} 