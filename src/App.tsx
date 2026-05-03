import { BrowserRouter as Router, Routes, Route, Navigate, Outlet } from 'react-router-dom';
import AuthMockup from './features/auth/auth-mockup'
import { DashboardLayout } from './shared/components/layout/dashboard-layout'
import EntrepreneurDashboard from './features/analytics/pages/entrepreneur-dashboard'
import ProductList from './features/catalog/pages/product-list'
import InventoryManagement from './features/catalog/pages/inventory-management'
import OrderList from './features/orders/pages/order-list'
import TenantSettings from './features/settings/pages/tenant-settings'
import MenuPage from './features/tenant-experience/pages/menu-page'
import CheckoutPage from './features/tenant-experience/pages/checkout-page'
import LogisticsDashboard from './features/logistics/pages/logistics-dashboard'
import AdvancedAnalytics from './features/analytics/pages/advanced-analytics'
import CustomerList from './features/customers/pages/customer-list'
import ExpensesDashboard from './features/finance/pages/expenses-dashboard'
import { AuthProvider, useAuth } from './shared/contexts/auth-context';
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

function AppContent() {
  const { isAuthenticated, isLoading, login } = useAuth();

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-stone-50">
        <div className="w-8 h-8 border-4 border-stone-200 border-t-stone-900 rounded-full animate-spin"></div>
      </div>
    );
  }

  return (
    <Routes>
      {/* Rutas Públicas de Tenant (Poner antes de fallback) */}
      <Route path="/:tenantSlug/checkout" element={<CheckoutPage />} />
      <Route path="/:tenantSlug" element={<MenuPage />} />

      {/* Ruta de Auth */}
      <Route path="/" element={isAuthenticated ? <Navigate to="/dashboard" replace /> : <AuthMockup onLogin={(token) => login(token)} />} />

      {/* Rutas Protegidas del Dashboard */}
      <Route element={<ProtectedRoute isAuthenticated={isAuthenticated} />}>
        <Route path="/dashboard" element={<EntrepreneurDashboard />} />
        <Route path="/dashboard/catalog" element={<ProductList />} />
        <Route path="/dashboard/inventory" element={<InventoryManagement />} />
        <Route path="/dashboard/orders" element={<OrderList />} />
        <Route path="/dashboard/customers" element={<CustomerList />} />
        <Route path="/dashboard/settings" element={<TenantSettings />} />
        <Route path="/dashboard/logistics" element={<LogisticsDashboard />} />
        <Route path="/dashboard/analytics" element={<AdvancedAnalytics />} />
        <Route path="/dashboard/finance" element={<ExpensesDashboard />} />
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

function App() {
  return (
    <Router>
      <AuthProvider>
        <AppContent />
      </AuthProvider>
    </Router>
  )
}

export default App;
