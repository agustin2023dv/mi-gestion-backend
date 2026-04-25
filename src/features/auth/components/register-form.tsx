import { useState } from 'react';
import { motion } from 'framer-motion';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { AuthFormProps } from '../types';

export function RegisterForm({ setView }: AuthFormProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [success, setSuccess] = useState(false);

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      setSuccess(true);
    }, 1500);
  };

  if (success) {
    return (
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="text-center"
      >
        <div className="w-16 h-16 bg-stone-900 rounded-full flex items-center justify-center mx-auto mb-6 text-stone-50">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
        </div>
        <h2 className="font-serif text-3xl mb-4 text-stone-900">Cuenta creada</h2>
        <p className="text-stone-500 mb-8 font-medium">Revisá tu email para verificar tu identidad.</p>
        <Button onClick={() => setView('login')}>Volver al Login</Button>
      </motion.div>
    );
  }

  return (
    <motion.div
      key="register"
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
    >
      <div className="mb-10">
        <button
          onClick={() => setView('login')}
          className="mb-8 flex items-center text-sm font-bold tracking-widest uppercase text-stone-400 hover:text-stone-900 transition-colors group focus-visible:outline-stone-900"
          aria-label="Volver atrás"
        >
          <svg className="w-4 h-4 mr-2 transform group-hover:-translate-x-1 transition-transform" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M19 12H5M12 19l-7-7 7-7" /></svg>
          Atrás
        </button>
        <h2 className="font-serif text-4xl lg:text-5xl mb-3 text-stone-900">Crear Cuenta</h2>
        <p className="text-stone-500 font-medium">Unite a mi-gestion y refiná tu estilo de vida.</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4">
        <Input label="Nombre Completo" type="text" required placeholder="Jane Doe" />
        <Input label="Email" type="email" required placeholder="nombre@ejemplo.com" />
        <Input label="Contraseña" type="password" required placeholder="••••••••" />

        <div className="pt-4">
          <Button type="submit" isLoading={isLoading}>Registrarse</Button>
        </div>
      </form>
    </motion.div>
  );
}
