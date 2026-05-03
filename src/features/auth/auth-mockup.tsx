import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import type { AuthView, AuthMockupProps } from './types';
import { LoginForm } from './components/login-form';
import { RegisterForm } from './components/register-form';
import { ForgotForm } from './components/forgot-form';

export default function AuthMockup({ onLogin }: AuthMockupProps) {
  const [view, setView] = useState<AuthView>('login');

  return (
    <div className="min-h-screen bg-[#FAF9F6] text-stone-900 flex flex-col lg:flex-row selection:bg-stone-900 selection:text-white">
      {/* Lado Izquierdo: Hero Editorial */}
      <div className="hidden lg:flex lg:w-5/12 p-10 xl:p-16 flex-col justify-between border-r border-stone-200 relative bg-[#F5F4F0] overflow-hidden">
        {/* Efecto de ruido sutil */}
        <div className="absolute inset-0 opacity-[0.03] mix-blend-multiply pointer-events-none" style={{ backgroundImage: 'url("data:image/svg+xml,%3Csvg viewBox=%220 0 200 200%22 xmlns=%22http://www.w3.org/2000/svg%22%3E%3Cfilter id=%22noiseFilter%22%3E%3CfeTurbulence type=%22fractalNoise%22 baseFrequency=%220.85%22 numOctaves=%223%22 stitchTiles=%22stitch%22/%3E%3C/filter%3E%3Crect width=%22100%25%22 height=%22100%25%22 filter=%22url(%23noiseFilter)%22/%3E%3C/svg%3E")' }}></div>

        <div className="relative z-10">
          <h1 className="font-serif text-3xl tracking-tight text-stone-900">mi-gestion.</h1>
        </div>

        <div className="relative z-10 max-w-sm">
          <motion.div
            initial={{ opacity: 0, y: 30 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.8, delay: 0.2, ease: [0.22, 1, 0.36, 1] }}
          >
            <h2 className="font-serif text-4xl xl:text-5xl leading-[1.1] mb-4 text-stone-900">
              Esenciales curados para la mente moderna.
            </h2>
          </motion.div>
          <motion.p
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ duration: 1, delay: 0.5 }}
            className="text-stone-500 font-medium text-lg leading-relaxed"
          >
            Accedé a tu membresía exclusiva y descubrí un mundo de estética refinada.
          </motion.p>
        </div>
      </div>

      {/* Lado Derecho: Contenedor del Formulario */}
      <div className="w-full lg:w-7/12 flex-1 flex flex-col">
        <div className="lg:hidden p-6 border-b border-stone-200">
          <h1 className="font-serif text-2xl tracking-tight text-stone-900">mi-gestion.</h1>
        </div>

        <div className="flex-1 flex items-center justify-center p-6 sm:p-10 md:p-16 relative">
          <div className="w-full max-w-[400px]">
            <AnimatePresence mode="wait" initial={false}>
              {view === 'login' && <LoginForm setView={setView} onLogin={onLogin} />}
              {view === 'register' && <RegisterForm setView={setView} />}
              {view === 'forgot' && <ForgotForm setView={setView} />}
            </AnimatePresence>
          </div>
        </div>
      </div>
    </div>
  );
}
