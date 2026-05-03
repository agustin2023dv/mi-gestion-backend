import { motion, AnimatePresence } from 'framer-motion';
import { X, Mail, Phone, ShoppingBag, Calendar, DollarSign, ChevronRight } from 'lucide-react';
import type { Customer } from '../types';
import { cn } from '../../../shared/utils/cn';

interface CustomerDetailDrawerProps {
  customer: Customer | null;
  isOpen: boolean;
  onClose: () => void;
}

export function CustomerDetailDrawer({ customer, isOpen, onClose }: CustomerDetailDrawerProps) {
  if (!customer) return null;

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-stone-900/40 backdrop-blur-sm z-50 lg:hidden"
          />
          <motion.div
            initial={{ x: '100%' }}
            animate={{ x: 0 }}
            exit={{ x: '100%' }}
            transition={{ type: 'spring', damping: 25, stiffness: 200 }}
            className="fixed top-0 right-0 h-full w-full max-w-lg bg-white z-50 shadow-2xl flex flex-col font-sans"
          >
            {/* Header */}
            <div className="p-8 border-b border-stone-100 flex items-center justify-between">
              <div className="flex items-center space-x-4">
                <div className="w-12 h-12 rounded-full bg-stone-900 flex items-center justify-center text-white font-serif text-lg">
                  {customer.firstName[0]}{customer.lastName[0]}
                </div>
                <div>
                  <h2 className="font-serif text-2xl text-stone-900 tracking-tight">
                    {customer.firstName} {customer.lastName}
                  </h2>
                  <span className={cn(
                    "text-[10px] font-bold uppercase tracking-widest px-2 py-0.5 rounded-full border",
                    customer.status === 'VIP' ? "bg-amber-50 text-amber-700 border-amber-200" :
                      customer.status === 'Recurrente' ? "bg-blue-50 text-blue-700 border-blue-200" :
                        "bg-stone-50 text-stone-600 border-stone-200"
                  )}>
                    {customer.status}
                  </span>
                </div>
              </div>
              <button
                onClick={onClose}
                className="p-2 hover:bg-stone-100 rounded-full transition-colors text-stone-400 hover:text-stone-900"
              >
                <X className="w-6 h-6" />
              </button>
            </div>

            {/* Content */}
            <div className="flex-1 overflow-y-auto p-8 space-y-10">
              {/* Contact Info */}
              <section className="space-y-4">
                <h3 className="text-xs font-bold tracking-[0.2em] uppercase text-stone-400">Información de Contacto</h3>
                <div className="space-y-3">
                  <div className="flex items-center space-x-3 text-stone-600">
                    <Mail className="w-4 h-4" />
                    <span className="text-sm">{customer.email}</span>
                  </div>
                  <div className="flex items-center space-x-3 text-stone-600">
                    <Phone className="w-4 h-4" />
                    <span className="text-sm">{customer.phone}</span>
                  </div>
                </div>
              </section>

              {/* Stats Grid */}
              <section className="grid grid-cols-2 gap-4">
                <div className="bg-stone-50 p-4 rounded-xl border border-stone-100">
                  <div className="flex items-center space-x-2 text-stone-400 mb-1">
                    <ShoppingBag className="w-3.5 h-3.5" />
                    <span className="text-[10px] font-bold uppercase tracking-wider">Pedidos</span>
                  </div>
                  <div className="text-xl font-serif font-bold text-stone-900">{customer.totalOrders}</div>
                </div>
                <div className="bg-stone-50 p-4 rounded-xl border border-stone-100">
                  <div className="flex items-center space-x-2 text-stone-400 mb-1">
                    <DollarSign className="w-3.5 h-3.5" />
                    <span className="text-[10px] font-bold uppercase tracking-wider">Gasto Total</span>
                  </div>
                  <div className="text-xl font-serif font-bold text-stone-900">${customer.totalSpent.toLocaleString()}</div>
                </div>
                <div className="bg-stone-50 p-4 rounded-xl border border-stone-100 col-span-2">
                  <div className="flex items-center space-x-2 text-stone-400 mb-1">
                    <Calendar className="w-3.5 h-3.5" />
                    <span className="text-[10px] font-bold uppercase tracking-wider">Último Pedido</span>
                  </div>
                  <div className="text-sm font-bold text-stone-900">{customer.lastOrderDate}</div>
                </div>
              </section>

              {/* Order History */}
              <section className="space-y-6">
                <h3 className="text-xs font-bold tracking-[0.2em] uppercase text-stone-400">Historial de Pedidos</h3>
                <div className="space-y-4">
                  {customer.orders.map((order) => (
                    <div
                      key={order.id}
                      className="group flex items-center justify-between p-4 rounded-xl border border-stone-100 hover:border-stone-900 transition-all duration-300 cursor-pointer"
                    >
                      <div className="flex items-center space-x-4">
                        <div className="w-10 h-10 rounded-lg bg-stone-50 flex items-center justify-center text-stone-400 group-hover:bg-stone-900 group-hover:text-white transition-colors">
                          <ShoppingBag className="w-5 h-5" />
                        </div>
                        <div>
                          <div className="text-sm font-bold text-stone-900">Pedido #{order.id}</div>
                          <div className="text-xs text-stone-400">{order.date} • {order.itemsCount} productos</div>
                        </div>
                      </div>
                      <div className="flex items-center space-x-4">
                        <div className="text-right">
                          <div className="text-sm font-serif font-bold text-stone-900">${order.total}</div>
                          <div className="text-[9px] font-bold uppercase tracking-widest text-stone-400">{order.status}</div>
                        </div>
                        <ChevronRight className="w-4 h-4 text-stone-300 group-hover:text-stone-900 transition-colors" />
                      </div>
                    </div>
                  ))}
                </div>
              </section>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
