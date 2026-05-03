import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../../features/auth/api/auth';

interface User {
  email: string;
  nombre: string;
  apellido: string;
}

interface AuthContextType {
  isAuthenticated: boolean;
  user: User | null;
  login: (token: string, userData?: User) => void;
  logout: () => void;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem('auth_token');
    if (token) {
      // In a real app, we might want to validate the token or fetch user profile here
      setIsAuthenticated(true);
      // Mock user for now or fetch it
      const savedUser = localStorage.getItem('user_info');
      if (savedUser) setUser(JSON.parse(savedUser));
    }
    setIsLoading(false);
  }, []);

  const login = (token: string, userData?: User) => {
    localStorage.setItem('auth_token', token);
    if (userData) {
      localStorage.setItem('user_info', JSON.stringify(userData));
      setUser(userData);
    }
    setIsAuthenticated(true);
  };

  const logout = async () => {
    // Limpiar localmente de inmediato para UX rápida
    localStorage.removeItem('auth_token');
    localStorage.removeItem('user_info');
    localStorage.removeItem('tenant_id');
    setIsAuthenticated(false);
    setUser(null);

    try {
      // Avisar al backend en segundo plano
      await authApi.logout();
    } catch (error) {
      console.error('Error logging out from server:', error);
    }
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, user, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
}
