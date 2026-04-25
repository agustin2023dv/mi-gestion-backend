import { useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import AuthMockup from './features/auth/auth-mockup'
import { DashboardLayout } from './shared/components/layout/dashboard-layout'
import EntrepreneurDashboard from './features/analytics/pages/entrepreneur-dashboard'
import ProductList from './features/catalog/pages/product-list'
import OrderList from './features/orders/pages/order-list'
import TenantSettings from './features/settings/pages/tenant-settings'
import './index.css'

// Mock de Guardia de Autenticación
const ProtectedRoute = ({ isAuthenticated }: { isAuthenticated: boolean }) => {
  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }
  return (
    <DashboardLayout>
      <Outlet />
    </DashboardLayout>
  );
};

function App() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);

  const handleLogin = () => {
    setIsAuthenticated(true);
  };

  return (
    <Router>
      <Routes>
        {/* Ruta de Auth */}
        <Route path="/" element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <AuthMockup onLogin={handleLogin} />} />

        {/* Rutas Protegidas del Dashboard */}
        <Route element={<ProtectedRoute isAuthenticated={isAuthenticated} />}>
          <Route path="/dashboard" element={<EntrepreneurDashboard />} />
          <Route path="/dashboard/catalog" element={<ProductList />} />
          <Route path="/dashboard/orders" element={<OrderList />} />
          <Route path="/dashboard/settings" element={<TenantSettings />} />
          <Route path="/dashboard/logistics" element={<div className="font-serif text-3xl">Logística (Próximamente)</div>} />
          <Route path="/dashboard/analytics" element={<div className="font-serif text-3xl">Analíticas Avanzadas (Próximamente)</div>} />
        </Route>

        {/* Fallback */}
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </Router>
  )
}

export default App
