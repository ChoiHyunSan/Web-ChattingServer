export const refreshToken = async (navigate) => {
  try {
    const refreshToken = localStorage.getItem('refreshToken');
    if (!refreshToken) {
      throw new Error('Refresh token not found');
    }

    const response = await fetch('http://localhost:8080/api/auth/refresh', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${refreshToken}`,
        'Content-Type': 'application/json'
      }
    });

    if (!response.ok) {
      throw new Error('Token refresh failed');
    }

    const data = await response.json();
    localStorage.setItem('token', data.data.token);
    localStorage.setItem('refreshToken', data.data.refreshToken);
    return data.data.token;
  } catch (error) {
    console.error('Token refresh failed:', error);
    navigate('/login');
    throw error;
  }
};

export const fetchWithAuth = async (url, options, navigate) => {
  try {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('No token found');
    }

    const headers = new Headers({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
      'Accept': 'application/json',
      ...(options.headers || {})
    });

    const response = await fetch(url, {
      ...options,
      headers
    });

    if (response.status === 401 || response.status === 403) {
      console.log('Token expired, attempting to refresh...');
      const newToken = await refreshToken(navigate);
      headers.set('Authorization', `Bearer ${newToken}`);
      
      const retryResponse = await fetch(url, {
        ...options,
        headers
      });

      // 토큰 갱신 후 재시도가 성공한 경우
      if (retryResponse.ok) {
        return retryResponse;
      }
    }

    return response;
  } catch (error) {
    console.error('Fetch error:', error);
    throw error;
  }
}; 