import { useState } from 'react';
import { ChevronLeft, CreditCard, Truck, CheckCircle2 } from 'lucide-react';
import { useTenant } from '../hooks/use-tenant';
import { Button } from '../../../shared/components/ui/button';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';

export default function CheckoutPage() {
  const { tenant } = useTenant();
  const navigate = useNavigate();
  const [step, setStep] = useState<'details' | 'success'>('details');
  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    address: '',
    notes: ''
  });

  if (!tenant) return null;

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    // Aquí iría la lógica para enviar el pedido al backend
    setStep('success');
  };

  if (step === 'success') {
    return (
      <div className="min-h-screen bg-stone-50 flex items-center justify-center p-4">
        <motion.div 
          initial={{ scale: 0.9, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="max-w-md w-full bg-white rounded-[2rem] p-10 text-center shadow-xl border border-stone-100"
        >
          <div className="w-20 h-20 bg-green-50 text-green-600 rounded-full flex items-center justify-center mx-auto mb-6">
            <CheckCircle2 className="w-10 h-10" />
          </div>
          <h2 className="font-serif text-3xl font-bold text-stone-900 mb-4">¡Pedido Recibido!</h2>
          <p className="text-stone-500 mb-8 leading-relaxed">
            Gracias por tu compra en <span className="font-bold text-stone-900">{tenant.name}</span>. 
            Te enviaremos una notificación cuando tu pedido esté en camino.
          </p>
          <Button 
            onClick={() => navigate(`/${tenant.slug}`)}
            className="w-full h-14 rounded-2xl font-bold uppercase tracking-widest text-xs"
          >
            Volver al Inicio
          </Button>
        </motion.div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-stone-50 font-sans text-stone-900">
      <header className="bg-white border-b border-stone-200 sticky top-0 z-10">
        <div className="max-w-3xl mx-auto px-4 h-16 flex items-center gap-4">
          <button 
            onClick={() => navigate(-1)}
            className="p-2 hover:bg-stone-50 rounded-full transition-colors"
          >
            <ChevronLeft className="w-6 h-6" />
          </button>
          <h1 className="font-serif text-xl font-bold">Finalizar Pedido</h1>
        </div>
      </header>

      <main className="max-w-3xl mx-auto px-4 py-8">
        <form onSubmit={handleSubmit} className="space-y-8">
          {/* Contact Information */}
          <section className="bg-white rounded-[2rem] p-8 border border-stone-100 shadow-sm">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-stone-900 text-white rounded-xl">
                <Truck className="w-5 h-5" />
              </div>
              <h2 className="text-lg font-bold uppercase tracking-tight">Datos de Entrega</h2>
            </div>
            
            <div className="grid gap-6">
              <div className="space-y-2">
                <label className="text-[10px] font-bold uppercase tracking-widest text-stone-400 ml-1">Nombre Completo</label>
                <input 
                  required
                  type="text" 
                  placeholder="Juan Pérez"
                  className="w-full bg-stone-50 border border-stone-100 rounded-2xl py-4 px-5 text-stone-900 focus:outline-none focus:ring-2 focus:ring-stone-900/5 transition-all"
                  value={formData.name}
                  onChange={(e) => setFormData({...formData, name: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-bold uppercase tracking-widest text-stone-400 ml-1">Teléfono / WhatsApp</label>
                <input 
                  required
                  type="tel" 
                  placeholder="+54 9 11 ..."
                  className="w-full bg-stone-50 border border-stone-100 rounded-2xl py-4 px-5 text-stone-900 focus:outline-none focus:ring-2 focus:ring-stone-900/5 transition-all"
                  value={formData.phone}
                  onChange={(e) => setFormData({...formData, phone: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-bold uppercase tracking-widest text-stone-400 ml-1">Dirección de Envío</label>
                <input 
                  required
                  type="text" 
                  placeholder="Calle 123, Depto 4B"
                  className="w-full bg-stone-50 border border-stone-100 rounded-2xl py-4 px-5 text-stone-900 focus:outline-none focus:ring-2 focus:ring-stone-900/5 transition-all"
                  value={formData.address}
                  onChange={(e) => setFormData({...formData, address: e.target.value})}
                />
              </div>
              <div className="space-y-2">
                <label className="text-[10px] font-bold uppercase tracking-widest text-stone-400 ml-1">Notas (Opcional)</label>
                <textarea 
                  placeholder="Timbre roto, dejar en portería..."
                  rows={3}
                  className="w-full bg-stone-50 border border-stone-100 rounded-2xl py-4 px-5 text-stone-900 focus:outline-none focus:ring-2 focus:ring-stone-900/5 transition-all resize-none"
                  value={formData.notes}
                  onChange={(e) => setFormData({...formData, notes: e.target.value})}
                />
              </div>
            </div>
          </section>

          {/* Payment Method */}
          <section className="bg-white rounded-[2rem] p-8 border border-stone-100 shadow-sm opacity-60">
            <div className="flex items-center gap-3 mb-6">
              <div className="p-2 bg-stone-100 text-stone-400 rounded-xl">
                <CreditCard className="w-5 h-5" />
              </div>
              <h2 className="text-lg font-bold uppercase tracking-tight text-stone-400">Método de Pago</h2>
            </div>
            <div className="p-4 bg-stone-50 rounded-2xl border border-dashed border-stone-200 text-center">
              <p className="text-xs font-medium text-stone-400">Pago contra entrega (Efectivo/Transferencia)</p>
            </div>
          </section>

          <Button 
            type="submit"
            className="w-full h-16 rounded-[2rem] shadow-xl text-sm font-bold uppercase tracking-widest flex items-center justify-center gap-3 bg-stone-900 hover:bg-stone-800"
          >
            Confirmar Pedido
          </Button>
        </form>
      </main>
    </div>
  );
}
