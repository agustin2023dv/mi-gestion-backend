import { useState } from 'react';
import { motion } from 'framer-motion';
import { Link, useLocation } from 'react-router-dom';
import { 
  LayoutDashboard, 
  Package, 
  ShoppingBag, 
  Truck, 
  BarChart3, 
  Settings, 
  LogOut,
  Menu,
  X,
  Bell,
  Users
} from 'lucide-react';
import { cn } from '../../utils/cn';
import { useAuth } from '../../contexts/auth-context';
import { TenantDataProvider } from '../../contexts/tenant-data-context';

interface SidebarItemProps {
  icon: React.ElementType;
  label: string;
  to?: string;
  isActive?: boolean;
  onClick?: () => void;
}

const SidebarItem = ({ icon: Icon, label, to, isActive, onClick }: SidebarItemProps) => {
  const content = (
    <>
      {isActive && (
        <motion.div 
          layoutId="active-pill"
          className="absolute left-0 w-1 h-full bg-stone-900" 
        />
      )}
      <Icon className={cn("w-5 h-5", isActive ? "text-stone-900" : "group-hover:text-stone-900")} />
      <span className="text-xs font-bold tracking-[0.15em] uppercase text-left">{label}</span>
    </>
  );

  const styles = cn(
    "w-full flex items-center space-x-4 px-6 py-4 transition-all duration-300 group relative",
    isActive 
      ? "text-stone-900 bg-stone-100/50" 
      : "text-stone-400 hover:text-stone-900 hover:bg-stone-50"
  );

  if (to) {
    return (
      <Link to={to} onClick={onClick} className={styles}>
        {content}
      </Link>
    );
  }

  return (
    <button onClick={onClick} className={styles}>
      {content}
    </button>
  );
};

export const DashboardLayout = ({ children }: { children: React.ReactNode }) => {
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);
  const location = useLocation();
  const { logout } = useAuth();

  const isPathActive = (path: string) => location.pathname === path;
  const closeMenu = () => setIsMobileMenuOpen(false);

  const navItems = [
    { icon: LayoutDashboard, label: "Resumen", to: "/dashboard" },
    { icon: Package, label: "Inventario", to: "/dashboard/inventory" },
    { icon: ShoppingBag, label: "Pedidos", to: "/dashboard/orders" },
    { icon: Users, label: "Clientes", to: "/dashboard/customers" },
    { icon: Truck, label: "Logística", to: "/dashboard/logistics" },
    { icon: BarChart3, label: "Analíticas", to: "/dashboard/analytics" },
    { icon: Settings, label: "Configuración", to: "/dashboard/settings" },
  ];

  return (
    <TenantDataProvider>
      <div className="h-[100dvh] bg-[#FAF9F6] text-stone-900 flex flex-col lg:flex-row selection:bg-stone-900 selection:text-white font-sans overflow-hidden">
        {/* Sidebar Desktop */}
        <aside className="hidden lg:flex w-72 flex-col border-r border-stone-200 bg-white/50 backdrop-blur-sm h-full shrink-0">
          <div className="p-10">
            <h1 className="font-serif text-3xl tracking-tight text-stone-900">mi-gestion.</h1>
          </div>

          <nav className="flex-1 mt-4 overflow-y-auto no-scrollbar">
            {navItems.map((item) => (
              <SidebarItem 
                key={item.to} 
                icon={item.icon} 
                label={item.label} 
                to={item.to} 
                isActive={isPathActive(item.to)} 
              />
            ))}
          </nav>

          <div className="p-6 border-t border-stone-100">
            <SidebarItem 
              icon={LogOut} 
              label="Cerrar Sesión" 
              onClick={() => logout()} 
            />
          </div>
        </aside>

        {/* Header Mobile */}
        <header className="lg:hidden flex items-center justify-between p-6 bg-white border-b border-stone-200 sticky top-0 z-50 shrink-0">
          <h1 className="font-serif text-2xl tracking-tight text-stone-900">mi-gestion.</h1>
          <button onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)} className="p-2">
            {isMobileMenuOpen ? <X /> : <Menu />}
          </button>
        </header>

        {/* Mobile Menu Overlay */}
        <motion.div
          initial={false}
          animate={isMobileMenuOpen ? { x: 0 } : { x: '-100%' }}
          className="fixed inset-0 bg-white z-40 lg:hidden pt-24"
        >
          <nav className="flex flex-col h-full overflow-y-auto">
            {navItems.map((item) => (
              <SidebarItem 
                key={item.to} 
                icon={item.icon} 
                label={item.label} 
                to={item.to} 
                isActive={isPathActive(item.to)} 
                onClick={closeMenu}
              />
            ))}
            <div className="mt-8 pt-8 border-t border-stone-100 pb-12">
              <SidebarItem 
                icon={LogOut} 
                label="Cerrar Sesión" 
                onClick={() => {
                  closeMenu();
                  logout();
                }} 
              />
            </div>
          </nav>
        </motion.div>

        {/* Main Content Area */}
        <main className="flex-1 flex flex-col relative min-w-0 overflow-hidden">
          {/* Top Navbar */}
          <div className="hidden lg:flex h-20 items-center justify-between px-12 border-b border-stone-100 bg-white/30 backdrop-blur-sm shrink-0">
            <h2 className="text-xs font-bold tracking-[0.2em] uppercase text-stone-400">Panel de Emprendedor</h2>
            <div className="flex items-center space-x-6">
              <button className="relative p-2 text-stone-400 hover:text-stone-900 transition-colors">
                <Bell className="w-5 h-5" />
                <span className="absolute top-1 right-1 w-2 h-2 bg-stone-900 rounded-full border-2 border-white"></span>
              </button>
              <div className="flex items-center space-x-3">
                <div className="w-8 h-8 rounded-full bg-stone-900 flex items-center justify-center text-stone-50 text-[10px] font-bold">JD</div>
                <span className="text-xs font-bold tracking-wider text-stone-900">Jane Doe</span>
              </div>
            </div>
          </div>

          {/* Page Content */}
          <div className="flex-1 p-6 lg:p-12 overflow-y-auto overflow-x-hidden scroll-smooth">
            <div className="max-w-7xl mx-auto w-full">
              {children}
            </div>
          </div>
        </main>
      </div>
    </TenantDataProvider>
  );
};
