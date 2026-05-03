import { useState } from 'react';
import { Search, Filter, MoreHorizontal, User, Loader2 } from 'lucide-react';
import { Section } from '../../../shared/components/ui/section';
import { CustomerDetailDrawer } from '../components/customer-detail-drawer';
import type { Customer } from '../types';
import { useTenantData } from '../../../shared/contexts/tenant-data-context';
import { cn } from '../../../shared/utils/cn';

export default function CustomerList() {
  const { customers, isLoading } = useTenantData();
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCustomer, setSelectedCustomer] = useState<Customer | null>(null);
  const [isDrawerOpen, setIsDrawerOpen] = useState(false);

  const filteredCustomers = customers.filter(customer =>
    `${customer.firstName} ${customer.lastName}`.toLowerCase().includes(searchTerm.toLowerCase()) ||
    customer.email.toLowerCase().includes(searchTerm.toLowerCase())
  );


  const handleSelectCustomer = (customer: Customer) => {
    setSelectedCustomer(customer);
    setIsDrawerOpen(true);
  };

  return (
    <Section title="Directorio de Clientes">
      {/* Search and Filters */}
      <div className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-4 mb-8">
        <div className="relative w-full max-w-md">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-4 h-4 text-stone-400" />
          <input
            type="text"
            placeholder="Buscar por nombre o email..."
            className="w-full bg-white border border-stone-200 rounded-xl py-3 pl-12 pr-4 text-sm focus:outline-none focus:ring-2 focus:ring-stone-900/5 transition-all"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex items-center space-x-3 w-full sm:w-auto">
          <button className="flex-1 sm:flex-none flex items-center justify-center space-x-2 px-4 py-3 bg-white border border-stone-200 rounded-xl text-xs font-bold tracking-wider uppercase hover:bg-stone-50 transition-colors">
            <Filter className="w-4 h-4" />
            <span>Filtros</span>
          </button>
        </div>
      </div>

      {/* Loading State */}
      {isLoading ? (
        <div className="flex flex-col items-center justify-center py-24">
          <Loader2 className="w-8 h-8 animate-spin text-stone-400 mb-4" />
          <p className="text-sm text-stone-400">Cargando clientes...</p>
        </div>
      ) : (
        /* Customers Table */
        <div className="bg-white border border-stone-100 rounded-2xl overflow-hidden shadow-sm">
          {filteredCustomers.length > 0 ? (
            <div className="overflow-x-auto">
              <table className="w-full text-left border-collapse">
                <thead>
                  <tr className="bg-stone-50 border-b border-stone-100">
                    <th className="px-6 py-4 text-[10px] font-bold uppercase tracking-[0.2em] text-stone-400">Cliente</th>
                    <th className="px-6 py-4 text-[10px] font-bold uppercase tracking-[0.2em] text-stone-400">Estado</th>
                    <th className="px-6 py-4 text-[10px] font-bold uppercase tracking-[0.2em] text-stone-400">Pedidos</th>
                    <th className="px-6 py-4 text-[10px] font-bold uppercase tracking-[0.2em] text-stone-400">Gasto Total</th>
                    <th className="px-6 py-4 text-[10px] font-bold uppercase tracking-[0.2em] text-stone-400 text-right">Acciones</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-stone-100">
                  {filteredCustomers.map((customer) => (
                    <tr
                      key={customer.id}
                      className="hover:bg-stone-50/50 transition-colors group cursor-pointer"
                      onClick={() => handleSelectCustomer(customer)}
                    >
                      <td className="px-6 py-5">
                        <div className="flex items-center space-x-4">
                          <div className="w-10 h-10 rounded-full bg-stone-100 flex items-center justify-center text-stone-500 font-serif group-hover:bg-stone-900 group-hover:text-white transition-all duration-300">
                            {customer.firstName[0]}{customer.lastName[0]}
                          </div>
                          <div>
                            <div className="text-sm font-bold text-stone-900">{customer.firstName} {customer.lastName}</div>
                            <div className="text-xs text-stone-400">{customer.email}</div>
                          </div>
                        </div>
                      </td>
                      <td className="px-6 py-5">
                        <span className={cn(
                          "text-[9px] font-bold uppercase tracking-widest px-2 py-1 rounded-full border",
                          customer.status === 'VIP' ? "bg-amber-50 text-amber-700 border-amber-200" :
                            customer.status === 'Recurrente' ? "bg-blue-50 text-blue-700 border-blue-200" :
                              "bg-stone-50 text-stone-600 border-stone-200"
                        )}>
                          {customer.status}
                        </span>
                      </td>
                      <td className="px-6 py-5">
                        <div className="text-sm font-bold text-stone-900">{customer.totalOrders}</div>
                        <div className="text-[10px] text-stone-400 uppercase tracking-tight">Última: {customer.lastOrderDate}</div>
                      </td>
                      <td className="px-6 py-5">
                        <div className="text-sm font-serif font-bold text-stone-900">${customer.totalSpent.toLocaleString()}</div>
                        <div className="text-[10px] text-stone-400 uppercase tracking-tight">Ticket Prom. ${(customer.totalSpent / customer.totalOrders).toFixed(0)}</div>
                      </td>
                      <td className="px-6 py-5 text-right">
                        <button className="p-2 hover:bg-white rounded-full text-stone-400 hover:text-stone-900 transition-colors shadow-sm border border-transparent hover:border-stone-100">
                          <MoreHorizontal className="w-4 h-4" />
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <div className="p-20 text-center">
              <div className="w-16 h-16 bg-stone-50 rounded-full flex items-center justify-center mx-auto mb-4">
                <User className="w-8 h-8 text-stone-200" />
              </div>
              <h3 className="font-serif text-xl text-stone-900 mb-1">No hay clientes registrados</h3>
              <p className="text-stone-400 text-sm">Los clientes aparecerán aquí cuando realicen pedidos.</p>
            </div>
          )}

          {filteredCustomers.length > 0 && (
            <div className="p-6 bg-stone-50/50 border-t border-stone-100 flex items-center justify-between">
              <div className="text-xs text-stone-400 font-medium"> Mostrando {filteredCustomers.length} clientes</div>
              <div className="flex space-x-2">
                <button className="px-3 py-1.5 bg-white border border-stone-200 rounded-lg text-[10px] font-bold uppercase tracking-wider text-stone-400 cursor-not-allowed">Anterior</button>
                <button className="px-3 py-1.5 bg-white border border-stone-200 rounded-lg text-[10px] font-bold uppercase tracking-wider text-stone-600 hover:border-stone-900 transition-colors">Siguiente</button>
              </div>
            </div>
          )}
        </div>
      )}

      <CustomerDetailDrawer
        customer={selectedCustomer}
        isOpen={isDrawerOpen}
        onClose={() => setIsDrawerOpen(false)}
      />
    </Section>
  );
}
