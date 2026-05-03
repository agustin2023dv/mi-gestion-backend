import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Store, 
  MapPin, 
  Bike, 
  Eye, 
  EyeOff, 
  Edit2, 
  X,
  Plus,
  Trash2,
  GripVertical,
  ExternalLink,
  Check
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { PageHeader } from '../../../shared/components/ui/page-header';
import { cn } from '../../../shared/utils/cn';

type DeliveryMethodType = 'local' | 'pickup' | 'delivery';

interface DeliveryMethod {
  id: DeliveryMethodType;
  title: string;
  icon: React.ElementType;
  visible: boolean;
  paymentMethods: string[];
  operationType?: 'own' | 'third_party';
  costType?: 'negotiate' | 'fixed' | 'zone';
  acceptOutsideZone?: boolean;
}

const PAYMENT_METHODS = [
  { id: 'cash', label: 'Efectivo' },
  { id: 'mercado_pago', label: 'Mercado Pago' },
  { id: 'cards', label: 'Tarjeta débito/crédito' },
  { id: 'transfer', label: 'Transferencia' },
  { id: 'link_mp', label: 'Link de Pago - Mercadopago' },
];

const INITIAL_METHODS: DeliveryMethod[] = [
  {
    id: 'local',
    title: 'Consumo en local',
    icon: Store,
    visible: true,
    paymentMethods: ['cash', 'cards', 'transfer']
  },
  {
    id: 'pickup',
    title: 'Retiro en persona',
    icon: MapPin,
    visible: true,
    paymentMethods: ['cash']
  },
  {
    id: 'delivery',
    title: 'Envío a domicilio - Delivery',
    icon: Bike,
    visible: true,
    paymentMethods: ['cash', 'transfer', 'link_mp'],
    operationType: 'own',
    costType: 'zone',
    acceptOutsideZone: false
  }
];

export default function DeliverySettings() {
  const [methods, setMethods] = useState<DeliveryMethod[]>(INITIAL_METHODS);
  const [editingId, setEditingId] = useState<DeliveryMethodType | null>(null);

  const editingMethod = methods.find(m => m.id === editingId);

  const handleUpdate = (id: DeliveryMethodType, updates: Partial<DeliveryMethod>) => {
    setMethods(methods.map(m => m.id === id ? { ...m, ...updates } : m));
  };

  const getPaymentMethodLabels = (ids: string[]) => {
    return ids.map(id => PAYMENT_METHODS.find(p => p.id === id)?.label).filter(Boolean).join(', ');
  };

  return (
    <div className="max-w-4xl space-y-12 pb-20">
      <PageHeader 
        title="Formas de entrega" 
        description="A continuación, se listarán todas las formas de entrega disponibles (puedes desactivar las que no utilices)."
      />

      <div className="bg-white border border-stone-200 rounded-none shadow-sm">
        {methods.map((method, index) => (
          <div 
            key={method.id} 
            className={cn(
              "p-6 flex items-start justify-between group transition-colors hover:bg-stone-50",
              index !== methods.length - 1 && "border-b border-stone-100"
            )}
          >
            <div className="flex items-start space-x-4">
              <div className="mt-1">
                {method.visible ? (
                  <Eye className="w-5 h-5 text-stone-900" />
                ) : (
                  <EyeOff className="w-5 h-5 text-stone-400" />
                )}
              </div>
              <div className="space-y-1">
                <div className="flex items-center space-x-2">
                  <method.icon className="w-4 h-4 text-stone-500" />
                  <h3 className="text-sm font-bold text-stone-900">{method.title}</h3>
                </div>
                <p className="text-xs text-stone-500 font-medium">
                  - <strong className="font-bold text-stone-700">Medios de pago:</strong> {getPaymentMethodLabels(method.paymentMethods) || 'Ninguno'}
                </p>
                {method.id === 'delivery' && (
                  <>
                    <p className="text-xs text-stone-500 font-medium">
                      - <strong className="font-bold text-stone-700">Operación:</strong> {method.operationType === 'own' ? 'Delivery propio' : 'Servicio de logística'}
                    </p>
                    <p className="text-xs text-stone-500 font-medium">
                      - <strong className="font-bold text-stone-700">Costo:</strong> {
                        method.costType === 'zone' ? 'Costo por zona (2 zonas configuradas)' : 
                        method.costType === 'fixed' ? 'Costo fijo' : 'A convenir'
                      }
                    </p>
                  </>
                )}
              </div>
            </div>
            <button 
              onClick={() => setEditingId(method.id)}
              className="p-2 text-stone-400 hover:text-stone-900 hover:bg-stone-200 rounded-full transition-colors"
            >
              <Edit2 className="w-4 h-4" />
            </button>
          </div>
        ))}
      </div>

      <AnimatePresence>
        {editingId && editingMethod && (
          <EditModal 
            method={editingMethod} 
            onClose={() => setEditingId(null)} 
            onUpdate={(updates) => handleUpdate(editingId, updates)}
          />
        )}
      </AnimatePresence>
    </div>
  );
}

function EditModal({ 
  method, 
  onClose, 
  onUpdate 
}: { 
  method: DeliveryMethod; 
  onClose: () => void;
  onUpdate: (updates: Partial<DeliveryMethod>) => void;
}) {
  const [editingZoneId, setEditingZoneId] = useState<string | null>(null);

  const togglePaymentMethod = (id: string) => {
    const newMethods = method.paymentMethods.includes(id)
      ? method.paymentMethods.filter(m => m !== id)
      : [...method.paymentMethods, id];
    onUpdate({ paymentMethods: newMethods });
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4 sm:p-6">
      <motion.div 
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        onClick={onClose}
        className="absolute inset-0 bg-stone-900/40 backdrop-blur-sm"
      />
      <motion.div 
        layoutId={`modal-${method.id}`}
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 20 }}
        className="relative w-full max-w-2xl bg-white shadow-2xl rounded-sm flex flex-col max-h-[90vh] overflow-hidden"
      >
        {/* Header */}
        <div className="px-6 py-4 border-b border-stone-100 flex items-center justify-between shrink-0 bg-stone-50">
          <h2 className="text-xl font-serif font-bold text-stone-900">{method.title}</h2>
          <button onClick={onClose} className="p-2 text-stone-400 hover:text-stone-900 transition-colors">
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto space-y-8 no-scrollbar">
          {/* Visibilidad */}
          <div className="space-y-3">
            <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Visibilidad</label>
            <label className="flex items-center space-x-3 cursor-pointer group">
              <div className={cn(
                "w-5 h-5 border flex items-center justify-center transition-colors",
                method.visible ? "bg-stone-900 border-stone-900" : "border-stone-300 bg-white group-hover:border-stone-400"
              )}>
                {method.visible && <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className="w-2 h-2 bg-white" />}
              </div>
              <span className="text-sm font-bold text-stone-700 select-none">Visible</span>
              <input 
                type="checkbox" 
                className="hidden" 
                checked={method.visible} 
                onChange={(e) => onUpdate({ visible: e.target.checked })} 
              />
            </label>
          </div>

          {/* Payment Methods */}
          <div className="space-y-4">
            <div className="space-y-1">
              <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Métodos de pago aceptados *</label>
              <p className="text-xs text-stone-500">Métodos de pago aceptados para esta forma de entrega</p>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-4">
              {PAYMENT_METHODS.map(pm => (
                <label key={pm.id} className="flex items-center space-x-3 cursor-pointer group">
                  <div className={cn(
                    "w-4 h-4 border flex items-center justify-center transition-colors shrink-0",
                    method.paymentMethods.includes(pm.id) ? "bg-stone-900 border-stone-900" : "border-stone-300 bg-white group-hover:border-stone-400"
                  )}>
                    {method.paymentMethods.includes(pm.id) && <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className="w-1.5 h-1.5 bg-white" />}
                  </div>
                  <span className="text-xs font-bold text-stone-700 select-none truncate">{pm.label}</span>
                  <input 
                    type="checkbox" 
                    className="hidden" 
                    checked={method.paymentMethods.includes(pm.id)} 
                    onChange={() => togglePaymentMethod(pm.id)} 
                  />
                </label>
              ))}
            </div>
          </div>

          {/* Delivery Specific Options */}
          {method.id === 'delivery' && (
            <motion.div 
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              className="space-y-8 pt-4 border-t border-stone-100"
            >
              {/* Operation Type */}
              <div className="space-y-4">
                <div className="space-y-1">
                  <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Operación del delivery</label>
                  <p className="text-xs text-stone-500">Puedes optar por delivery propio o integrarte con un servicio de logística.</p>
                </div>
                <div className="flex space-x-6">
                  {(['own', 'third_party'] as const).map(type => (
                    <label key={type} className="flex items-center space-x-3 cursor-pointer group">
                      <div className={cn(
                        "w-4 h-4 rounded-full border flex items-center justify-center transition-colors",
                        method.operationType === type ? "border-stone-900" : "border-stone-300 group-hover:border-stone-400"
                      )}>
                        {method.operationType === type && <motion.div layoutId="op-type" className="w-2 h-2 bg-stone-900 rounded-full" />}
                      </div>
                      <span className="text-xs font-bold text-stone-700 select-none">
                        {type === 'own' ? 'Delivery propio' : 'Servicio de logística'}
                      </span>
                      <input 
                        type="radio" 
                        name="op-type"
                        className="hidden" 
                        checked={method.operationType === type} 
                        onChange={() => onUpdate({ operationType: type })} 
                      />
                    </label>
                  ))}
                </div>

                <AnimatePresence>
                  {method.operationType === 'third_party' && (
                    <motion.div 
                      initial={{ opacity: 0, height: 0 }}
                      animate={{ opacity: 1, height: 'auto' }}
                      exit={{ opacity: 0, height: 0 }}
                      className="overflow-hidden"
                    >
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mt-4 mb-2">
                        {/* PuniYa Card */}
                        <div className="border border-stone-200 p-4 rounded-sm hover:border-stone-400 transition-colors bg-white flex flex-col group cursor-pointer">
                          <div className="h-12 flex items-center mb-4">
                            <h3 className="font-black text-xl text-orange-600 italic">Puni-Ya</h3>
                          </div>
                          <h4 className="text-sm font-bold text-stone-900 mb-2">Puniya</h4>
                          <p className="text-xs text-stone-500 mb-6 flex-1 leading-relaxed">Optimiza tus envíos con PuniYa, más que una empresa de logística, tu socio para una experiencia de entrega excepcional.</p>
                          <div className="flex items-center justify-between mt-auto">
                            <a href="#" className="text-xs font-bold text-blue-600 flex items-center hover:underline">
                              <ExternalLink className="w-3 h-3 mr-1" />
                              Ver sitio web
                            </a>
                            <div className="w-9 h-5 bg-stone-200 rounded-full relative transition-colors group-hover:bg-stone-300">
                              <div className="w-3 h-3 bg-white rounded-full absolute left-1 top-1 shadow-sm" />
                            </div>
                          </div>
                        </div>

                        {/* Rapiboy Card */}
                        <div className="border border-stone-200 p-4 rounded-sm hover:border-stone-400 transition-colors bg-white flex flex-col group cursor-pointer">
                          <div className="h-12 flex items-center mb-4">
                            <div className="flex items-center space-x-2">
                              <div className="w-8 h-8 bg-blue-900 text-white flex items-center justify-center rounded text-xs font-bold">R</div>
                              <h3 className="font-black text-xl text-blue-900 tracking-tighter">RAPIBOY</h3>
                            </div>
                          </div>
                          <h4 className="text-sm font-bold text-stone-900 mb-2">Rapiboy</h4>
                          <p className="text-xs text-stone-500 mb-6 flex-1 leading-relaxed">Integrá Rapiboy para contratar servicios de entrega OnDemand o repartidores fijos.</p>
                          <div className="flex items-center justify-between mt-auto">
                            <a href="#" className="text-xs font-bold text-blue-600 flex items-center hover:underline">
                              <ExternalLink className="w-3 h-3 mr-1" />
                              Ver sitio web
                            </a>
                            <div className="w-9 h-5 bg-stone-200 rounded-full relative transition-colors group-hover:bg-stone-300">
                              <div className="w-3 h-3 bg-white rounded-full absolute left-1 top-1 shadow-sm" />
                            </div>
                          </div>
                        </div>
                      </div>
                    </motion.div>
                  )}
                </AnimatePresence>
              </div>

              {/* Cost Type */}
              <div className="space-y-4">
                <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Costo del envío</label>
                <div className="flex space-x-6">
                  {(['negotiate', 'fixed', 'zone'] as const).map(type => (
                    <label key={type} className="flex items-center space-x-3 cursor-pointer group">
                      <div className={cn(
                        "w-4 h-4 rounded-full border flex items-center justify-center transition-colors",
                        method.costType === type ? "border-stone-900" : "border-stone-300 group-hover:border-stone-400"
                      )}>
                        {method.costType === type && <motion.div layoutId="cost-type" className="w-2 h-2 bg-stone-900 rounded-full" />}
                      </div>
                      <span className="text-xs font-bold text-stone-700 select-none">
                        {type === 'negotiate' ? 'A convenir' : type === 'fixed' ? 'Costo fijo' : 'Costo por zona'}
                      </span>
                      <input 
                        type="radio" 
                        name="cost-type"
                        className="hidden" 
                        checked={method.costType === type} 
                        onChange={() => onUpdate({ costType: type })} 
                      />
                    </label>
                  ))}
                </div>
              </div>

              {/* Min & Threshold */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                <Input label="Mínimo de compra" placeholder="$ Mínimo de compra" defaultValue="" />
                <div className="space-y-1">
                  <Input label="Bonificación de envío" placeholder="$ No bonificar" defaultValue="" />
                  <p className="text-[10px] text-stone-400 italic">Bonificar envío para compras superiores al monto ingresado.</p>
                </div>
              </div>

              {/* Zones */}
              {method.costType === 'zone' && (
                <div className="space-y-4 pt-4 border-t border-stone-100">
                  <div className="space-y-1">
                    <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 flex items-center space-x-2">
                      <span>Zonas de delivery</span>
                      <span className="text-red-500">*</span>
                    </label>
                    <p className="text-xs text-stone-500 leading-relaxed">
                      Agrega una o más zonas de delivery. En cada zona podrás configurar el costo asociado. 
                      Para modificar una zona en el mapa, haz click en el título de la zona para activar los controles.
                    </p>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                    <div className="space-y-4">
                      {/* Zone 1 */}
                      <div className="p-4 border border-stone-200 bg-white group hover:border-stone-400 transition-colors">
                        <div className="flex items-start justify-between">
                          <div className="flex items-center space-x-3">
                            <div className="w-3 h-3 rounded-full bg-amber-400 border border-stone-900/10" />
                            <div>
                              <h4 className="text-sm font-bold text-stone-900 group-hover:underline cursor-pointer">Zona 1: $6.500</h4>
                              <p className="text-[10px] text-stone-500 mt-1">Mínimo: $5.000 | Bonificado: $9.000</p>
                            </div>
                          </div>
                          <div className="flex items-center space-x-2 text-stone-400">
                            <button onClick={() => setEditingZoneId('zona-1')} className="hover:text-stone-900 transition-colors"><Edit2 className="w-4 h-4" /></button>
                            <button className="hover:text-red-500 transition-colors"><Trash2 className="w-4 h-4" /></button>
                          </div>
                        </div>
                      </div>
                      
                      {/* Zone 2 */}
                      <div className="p-4 border border-stone-200 bg-white group hover:border-stone-400 transition-colors">
                        <div className="flex items-start justify-between">
                          <div className="flex items-center space-x-3">
                            <div className="w-3 h-3 rounded-full bg-green-400 border border-stone-900/10" />
                            <div>
                              <h4 className="text-sm font-bold text-stone-900 group-hover:underline cursor-pointer">Zona 2: $5.000</h4>
                            </div>
                          </div>
                          <div className="flex items-center space-x-2 text-stone-400">
                            <button onClick={() => setEditingZoneId('zona-2')} className="hover:text-stone-900 transition-colors"><Edit2 className="w-4 h-4" /></button>
                            <button className="hover:text-red-500 transition-colors"><Trash2 className="w-4 h-4" /></button>
                          </div>
                        </div>
                      </div>

                      <div className="flex space-x-3">
                        <Button variant="secondary" className="flex-1 py-2 text-xs flex items-center justify-center space-x-2">
                          <Plus className="w-4 h-4" />
                          <span>Agregar zona</span>
                        </Button>
                        <Button variant="outline" className="flex-1 py-2 text-xs flex items-center justify-center space-x-2">
                          <GripVertical className="w-4 h-4" />
                          <span>Reordenar</span>
                        </Button>
                      </div>
                    </div>

                    {/* Mock Map */}
                    <div className="bg-stone-100 border border-stone-200 min-h-[300px] relative overflow-hidden flex items-center justify-center">
                      <div className="absolute inset-0 bg-[url('https://api.maptiler.com/maps/basic-v2/256/0/0/0.png?key=get_your_own_OpIi9ZULNHzrESv6T2vL')] opacity-40 bg-cover bg-center" />
                      
                      {/* Mock polygons */}
                      <svg className="absolute inset-0 w-full h-full" viewBox="0 0 100 100" preserveAspectRatio="none">
                        <polygon points="40,30 60,35 55,60 45,55" fill="rgba(251, 191, 36, 0.3)" stroke="rgba(217, 119, 6, 0.8)" strokeWidth="0.5" />
                        <polygon points="30,40 40,30 45,55 35,65" fill="rgba(74, 222, 128, 0.3)" stroke="rgba(22, 163, 74, 0.8)" strokeWidth="0.5" />
                      </svg>

                      {/* Map Pin Mock */}
                      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-8 h-8 bg-stone-900 text-white rounded-full flex items-center justify-center shadow-lg border-2 border-white">
                        <Store className="w-4 h-4" />
                      </div>
                    </div>
                  </div>

                  {/* Accept outside zone checkbox */}
                  <div className="pt-4 mt-6 border-t border-stone-100">
                    <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 mb-3 block">Pedidos fuera de zona de cobertura</label>
                    <label className="flex items-start space-x-3 cursor-pointer group">
                      <div className={cn(
                        "w-4 h-4 border flex items-center justify-center transition-colors shrink-0 mt-0.5",
                        method.acceptOutsideZone ? "bg-stone-900 border-stone-900" : "border-stone-300 bg-white group-hover:border-stone-400"
                      )}>
                        {method.acceptOutsideZone && <motion.div initial={{ scale: 0 }} animate={{ scale: 1 }} className="w-1.5 h-1.5 bg-white" />}
                      </div>
                      <div className="space-y-1">
                        <span className="text-sm font-bold text-stone-700 select-none">Aceptar pedidos de direcciones fuera de las zonas de delivery</span>
                        <p className="text-[10px] text-stone-500 leading-relaxed max-w-lg">
                          Activa esta opción si quieres recibir pedidos de direcciones fuera de las zonas de cobertura y coordinar el costo de envío y forma de entrega por WhatsApp.
                        </p>
                      </div>
                      <input 
                        type="checkbox" 
                        className="hidden" 
                        checked={method.acceptOutsideZone || false} 
                        onChange={(e) => onUpdate({ acceptOutsideZone: e.target.checked })} 
                      />
                    </label>
                  </div>

                </div>
              )}
            </motion.div>
          )}
        </div>
      </motion.div>

      <AnimatePresence>
        {editingZoneId && (
          <ZoneEditModal 
            zoneId={editingZoneId} 
            onClose={() => setEditingZoneId(null)} 
          />
        )}
      </AnimatePresence>

    </div>
  );
}

function ZoneEditModal({ onClose, zoneId }: { onClose: () => void, zoneId: string }) {
  const [shape, setShape] = useState<'circle' | 'polygon'>('circle');

  return (
    <div className="fixed inset-0 z-[60] flex items-center justify-center p-4 sm:p-6">
      <motion.div 
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        exit={{ opacity: 0 }}
        onClick={onClose}
        className="absolute inset-0 bg-stone-900/60 backdrop-blur-sm"
      />
      <motion.div 
        initial={{ opacity: 0, scale: 0.95, y: 20 }}
        animate={{ opacity: 1, scale: 1, y: 0 }}
        exit={{ opacity: 0, scale: 0.95, y: 20 }}
        className="relative w-full max-w-lg bg-white shadow-2xl rounded-sm flex flex-col"
      >
        <div className="px-6 py-4 border-b border-stone-100 flex items-center justify-between shrink-0 bg-stone-50">
          <h2 className="text-xl font-serif font-bold text-stone-900">Actualizar zona de entrega</h2>
          <button onClick={onClose} className="p-2 text-stone-400 hover:text-stone-900 transition-colors">
            <X className="w-5 h-5" />
          </button>
        </div>
        
        <div className="p-6 overflow-y-auto space-y-6">
          <Input label="Costo del envío *" placeholder="$" defaultValue={zoneId === 'zona-1' ? "6500" : "5000"} />
          <Input label="Mínimo de compra" placeholder="$" defaultValue={zoneId === 'zona-1' ? "5000" : ""} />
          
          <div className="space-y-1">
            <Input label="Bonificación de envío" placeholder="$" defaultValue={zoneId === 'zona-1' ? "9000" : ""} />
            <p className="text-[10px] text-stone-400 italic">Bonificar envío para compras superiores al monto ingresado, ó dejar vacío para no bonificar.</p>
          </div>

          <div className="space-y-3 pt-2">
            <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 flex items-center space-x-2">
              <span>Formato de selección en el mapa</span>
              <span className="text-red-500">*</span>
            </label>
            <div className="flex space-x-6">
              {(['circle', 'polygon'] as const).map(type => (
                <label key={type} className="flex items-center space-x-3 cursor-pointer group">
                  <div className={cn(
                    "w-4 h-4 rounded-full border flex items-center justify-center transition-colors",
                    shape === type ? "border-stone-900" : "border-stone-300 group-hover:border-stone-400"
                  )}>
                    {shape === type && <motion.div layoutId="shape-type-modal" className="w-2 h-2 bg-stone-900 rounded-full" />}
                  </div>
                  <span className="text-xs font-bold text-stone-700 select-none">
                    {type === 'circle' ? 'Circular' : 'Poligonal'}
                  </span>
                  <input 
                    type="radio" 
                    className="hidden" 
                    checked={shape === type} 
                    onChange={() => setShape(type)} 
                  />
                </label>
              ))}
            </div>
          </div>

          <AnimatePresence mode="popLayout">
            {shape === 'circle' && (
              <motion.div 
                initial={{ opacity: 0, height: 0 }} 
                animate={{ opacity: 1, height: 'auto' }}
                exit={{ opacity: 0, height: 0 }}
                className="overflow-hidden"
              >
                <div className="pt-2">
                  <Input label="Radio (en kilómetros) *" defaultValue="2.54" suffix="km" />
                </div>
              </motion.div>
            )}
          </AnimatePresence>

        </div>

        <div className="px-6 py-4 border-t border-stone-100 flex items-center justify-center space-x-4 bg-stone-50">
          <Button variant="outline" onClick={onClose} className="w-32 flex items-center justify-center">
            <X className="w-4 h-4 mr-2 text-red-500" />
            <span className="text-red-500">Cancelar</span>
          </Button>
          <Button onClick={onClose} className="w-32 flex items-center justify-center">
            <Check className="w-4 h-4 mr-2" />
            <span>Guardar</span>
          </Button>
        </div>
      </motion.div>
    </div>
  );
}
