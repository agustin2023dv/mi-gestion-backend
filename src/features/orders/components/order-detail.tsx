import { motion, AnimatePresence } from 'framer-motion';
import { X, User, MapPin, Phone, Package, CreditCard, ChevronDown } from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';

interface OrderDetailProps {
  isOpen: boolean;
  onClose: () => void;
  order: any;
}

const STATUS_OPTIONS = [
  { value: 'PENDING', label: 'Pendiente' },
  { value: 'READY', label: 'Preparado' },
  { value: 'SHIPPED', label: 'En Camino' },
  { value: 'DELIVERED', label: 'Entregado' },
  { value: 'CANCELLED', label: 'Cancelado' },
];

export default function OrderDetail({ isOpen, onClose, order }: OrderDetailProps) {
  if (!order) return null;

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-stone-900/20 backdrop-blur-sm z-[60]"
          />
          <motion.div
            initial={{ x: '100%' }}
            animate={{ x: 0 }}
            exit={{ x: '100%' }}
            transition={{ type: 'spring', damping: 25, stiffness: 200 }}
            className="fixed right-0 top-0 h-full w-full max-w-2xl bg-white shadow-2xl z-[70] overflow-y-auto"
          >
            <div className="p-8 lg:p-12">
              {/* Header */}
              <div className="flex items-center justify-between mb-12">
                <div>
                  <div className="flex items-center space-x-4 mb-2">
                    <span className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Pedido</span>
                    <h2 className="font-serif text-4xl text-stone-900">#{order.id}</h2>
                  </div>
                  <p className="text-stone-500 font-medium">{order.date}</p>
                </div>
                <button onClick={onClose} className="p-2 hover:bg-stone-100 transition-colors">
                  <X className="w-6 h-6 text-stone-400" />
                </button>
              </div>

              <div className="space-y-12">
                {/* Status Selector */}
                <div className="bg-stone-50 p-6 space-y-4">
                  <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500">Estado del Pedido</label>
                  <div className="relative">
                    <select 
                      defaultValue={order.status}
                      className="w-full bg-white border border-stone-200 py-3 px-4 focus:outline-none focus:border-stone-900 transition-colors appearance-none text-sm font-bold uppercase tracking-wider cursor-pointer"
                    >
                      {STATUS_OPTIONS.map(opt => (
                        <option key={opt.value} value={opt.value}>{opt.label}</option>
                      ))}
                    </select>
                    <ChevronDown className="absolute right-4 top-1/2 -translate-y-1/2 w-4 h-4 text-stone-400 pointer-events-none" />
                  </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
                  {/* Customer Info */}
                  <div className="space-y-6">
                    <h3 className="text-xs font-bold tracking-[0.2em] uppercase text-stone-900 border-b border-stone-100 pb-2 flex items-center">
                      <User className="w-3 h-3 mr-2" /> Cliente
                    </h3>
                    <div className="space-y-4">
                      <div>
                        <p className="text-sm font-bold text-stone-900 uppercase tracking-wide">{order.client}</p>
                        <p className="text-xs text-stone-500 mt-1">ID Cliente: #4492</p>
                      </div>
                      <div className="flex items-start space-x-3 text-stone-600">
                        <Phone className="w-4 h-4 mt-0.5" />
                        <span className="text-sm font-medium">+54 9 11 1234-5678</span>
                      </div>
                      <div className="flex items-start space-x-3 text-stone-600">
                        <MapPin className="w-4 h-4 mt-0.5" />
                        <span className="text-sm font-medium leading-relaxed">
                          Av. Libertador 1234, 4to B<br />
                          Recoleta, CABA, Argentina
                        </span>
                      </div>
                    </div>
                  </div>

                  {/* Payment & Method */}
                  <div className="space-y-6">
                    <h3 className="text-xs font-bold tracking-[0.2em] uppercase text-stone-900 border-b border-stone-100 pb-2 flex items-center">
                      <CreditCard className="w-3 h-3 mr-2" /> Pago y Envío
                    </h3>
                    <div className="space-y-4">
                      <div>
                        <p className="text-[10px] font-bold text-stone-400 uppercase tracking-widest mb-1">Método de Pago</p>
                        <p className="text-sm font-bold text-stone-900 uppercase tracking-wide">Transferencia Bancaria</p>
                      </div>
                      <div>
                        <p className="text-[10px] font-bold text-stone-400 uppercase tracking-widest mb-1">Método de Envío</p>
                        <p className="text-sm font-bold text-stone-900 uppercase tracking-wide">Mensajería Propia (CABA)</p>
                      </div>
                    </div>
                  </div>
                </div>

                {/* Items List */}
                <div className="space-y-6">
                  <h3 className="text-xs font-bold tracking-[0.2em] uppercase text-stone-900 border-b border-stone-100 pb-2 flex items-center">
                    <Package className="w-3 h-3 mr-2" /> Artículos ({order.items})
                  </h3>
                  <div className="divide-y divide-stone-50 border-b border-stone-50">
                    {[1, 2].map((i) => (
                      <div key={i} className="py-6 flex items-center justify-between">
                        <div className="flex items-center space-x-4">
                          <div className="w-16 h-16 bg-stone-50 flex items-center justify-center text-stone-300">
                            <Package className="w-6 h-6" />
                          </div>
                          <div>
                            <h4 className="text-sm font-bold text-stone-900 uppercase tracking-wide">Vela Minimalista Sándalo</h4>
                            <p className="text-xs text-stone-500 mt-1">Cantidad: 1</p>
                          </div>
                        </div>
                        <span className="font-serif text-lg text-stone-900">$1,200</span>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Totals */}
                <div className="bg-stone-900 p-8 text-stone-50 space-y-4">
                  <div className="flex justify-between text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">
                    <span>Subtotal</span>
                    <span>$4,100</span>
                  </div>
                  <div className="flex justify-between text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">
                    <span>Envío</span>
                    <span>$400</span>
                  </div>
                  <div className="pt-4 border-t border-stone-800 flex justify-between items-end">
                    <span className="text-xs font-bold tracking-[0.2em] uppercase">Total del Pedido</span>
                    <span className="font-serif text-3xl">${order.total.toLocaleString()}</span>
                  </div>
                </div>

                <div className="flex gap-4 pt-4">
                  <Button className="flex-1">Imprimir Comprobante</Button>
                  <Button variant="outline" className="flex-1">Notificar al Cliente</Button>
                </div>
              </div>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
