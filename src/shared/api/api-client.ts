import axios from 'axios';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

export const apiClient = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Interceptor for JWT and Tenant ID
apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('auth_token');
  const tenantId = localStorage.getItem('tenant_id');

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  if (tenantId) {
    config.headers['X-Tenant-ID'] = tenantId;
  }

  // Idempotency-Key for POST requests
  if (config.method === 'post' && !config.headers['Idempotency-Key']) {
    config.headers['Idempotency-Key'] = crypto.randomUUID();
  }

  return config;
});

// Interceptor for error handling
apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    
    if (status === 401 || status === 403) {
      console.error(`[apiClient] ${status} Error: Authentication/Authorization failed.`);
      
      // If we get a 401/403, the token might be expired or invalid
      // We should clear it and redirect to login
      if (typeof window !== 'undefined') {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('tenant_id');
        localStorage.removeItem('user_info');
        window.location.href = '/'; 
      }
    }
    return Promise.reject(error);
  }
);
