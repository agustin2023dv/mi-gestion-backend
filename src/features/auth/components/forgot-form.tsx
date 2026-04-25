import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { AuthFormProps } from '../types';

export function ForgotForm({ setView }: AuthFormProps) {
  const [status, setStatus] = useState<'idle' | 'loading' | 'success'>('idle');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setStatus('loading');
    setTimeout(() => {
      setStatus('success');
    }, 1200);
  };

  return (
    <motion.div
      key="forgot"
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
    >
      <div className="mb-10">
        <button
          onClick={() => setView('login')}
          className="mb-8 flex items-center text-sm font-bold tracking-widest uppercase text-stone-400 hover:text-stone-900 transition-colors group focus-visible:outline-stone-900"
        >
          <svg className="w-4 h-4 mr-2 transform group-hover:-translate-x-1 transition-transform" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 12H5M12 19l-7-7 7-7" /></svg>
          Atrás
        </button>
        <h2 className="font-serif text-4xl lg:text-5xl mb-3 text-stone-900">Recuperar</h2>
        <p className="text-stone-500 font-medium">Te enviaremos instrucciones para resetear tu contraseña.</p>
      </div>

      <AnimatePresence mode="wait">
        {status === 'success' ? (
          <motion.div
            key="success-msg"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-8"
          >
            <div className="bg-stone-200/50 p-6 border-l-2 border-stone-900">
              <p className="text-stone-900 font-medium">Enlace enviado a tu correo. Por favor, revisá tu bandeja de entrada.</p>
            </div>
            <Button onClick={() => setView('login')}>Volver al Login</Button>
          </motion.div>
        ) : (
          <motion.form key="forgot-form" exit={{ opacity: 0 }} onSubmit={handleSubmit} className="space-y-4">
            <Input label="Email" type="email" required placeholder="nombre@ejemplo.com" />
            <div className="pt-4">
              <Button type="submit" isLoading={status === 'loading'}>Enviar Enlace</Button>
            </div>
          </motion.form>
        )}
      </AnimatePresence>
    </motion.div>
  );
}
