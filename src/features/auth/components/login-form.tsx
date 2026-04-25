import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { AuthFormProps } from '../types';

export function LoginForm({ setView, onLogin }: AuthFormProps) {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    
    if (!email.includes('@')) {
      setError('Por favor, ingresá un email válido.');
      return;
    }
    if (password.length < 6) {
      setError('La contraseña debe tener al menos 6 caracteres.');
      return;
    }

    setIsLoading(true);
    
    setTimeout(() => {
      setIsLoading(false);
      if (email === 'admin@mabizz.com' && password === '123456') {
        onLogin?.();
      } else {
        setError('Credenciales incorrectas. Intenta con admin@mabizz.com / 123456');
      }
    }, 1200);
  };

  return (
    <motion.div
      key="login"
      initial={{ opacity: 0, x: 20 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -20 }}
      transition={{ duration: 0.5, ease: [0.22, 1, 0.36, 1] }}
    >
      <div className="mb-12">
        <h2 className="font-serif text-4xl lg:text-5xl mb-3 text-stone-900">Iniciar Sesión</h2>
        <p className="text-stone-500 font-medium">Bienvenido de nuevo a mi-gestion.</p>
      </div>

      <form onSubmit={handleSubmit} className="space-y-4" noValidate>
        <Input
          label="Email"
          type="email"
          required
          placeholder="nombre@ejemplo.com"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
        />
        <Input
          label="Contraseña"
          type="password"
          required
          placeholder="••••••••"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        <AnimatePresence>
          {error && (
            <motion.p 
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="text-red-600 text-sm mb-4 font-medium" 
              role="alert"
            >
              {error}
            </motion.p>
          )}
        </AnimatePresence>

        <div className="flex items-center justify-between mt-6 mb-8 pt-2">
          <label className="flex items-center space-x-3 cursor-pointer group">
            <div className="relative flex items-center justify-center">
              <input type="checkbox" className="peer sr-only" />
              <div className="w-4 h-4 border border-stone-300 peer-checked:bg-stone-900 peer-checked:border-stone-900 transition-colors"></div>
              <svg className="absolute w-3 h-3 text-white opacity-0 peer-checked:opacity-100 transition-opacity" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
            </div>
            <span className="text-sm font-medium text-stone-600 group-hover:text-stone-900 transition-colors">Recordarme</span>
          </label>
          <button
            type="button"
            onClick={() => setView('forgot')}
            className="text-sm font-medium text-stone-900 underline underline-offset-4 hover:text-stone-500 transition-colors focus-visible:outline-stone-900"
          >
            ¿Olvidaste tu contraseña?
          </button>
        </div>

        <Button type="submit" isLoading={isLoading}>Entrar</Button>
      </form>

      <div className="mt-12 text-center text-sm font-medium text-stone-500">
        ¿No tenés una cuenta?{' '}
        <button
          onClick={() => setView('register')}
          className="text-stone-900 underline underline-offset-4 hover:text-stone-600 transition-colors focus-visible:outline-stone-900"
        >
          Crear cuenta
        </button>
      </div>
    </motion.div>
  );
}
