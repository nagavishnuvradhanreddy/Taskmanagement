import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import api from '../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const storedUser = sessionStorage.getItem('task_manager_user');
    return storedUser ? JSON.parse(storedUser) : null;
  });
  const [token, setToken] = useState(() => sessionStorage.getItem('task_manager_token') || null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (token) {
      api.get('/auth/me')
        .then(({ data }) => {
          setUser(data);
          sessionStorage.setItem('task_manager_user', JSON.stringify(data));
        })
        .catch(() => {
          setUser(null);
          setToken(null);
          sessionStorage.removeItem('task_manager_token');
          sessionStorage.removeItem('task_manager_user');
        })
        .finally(() => setLoading(false));
    }
  }, [token]);

  const login = async (credentials) => {
    const { data } = await api.post('/auth/login', credentials);
    sessionStorage.setItem('task_manager_token', data.token);
    sessionStorage.setItem('task_manager_user', JSON.stringify(data.user));
    setToken(data.token);
    setUser(data.user);
    return data;
  };

  const register = async (payload) => {
    const { data } = await api.post('/auth/register', payload);
    return data;
  };

  const logout = async () => {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      console.error('Logout error', error);
    } finally {
      setUser(null);
      setToken(null);
      sessionStorage.removeItem('task_manager_token');
      sessionStorage.removeItem('task_manager_user');
    }
  };

  const value = useMemo(() => ({ user, token, loading, login, register, logout, setUser }), [user, token, loading]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
