import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { motion, AnimatePresence } from 'framer-motion';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { AuthFormWrapper } from './auth-form-wrapper';
import type { AuthFormProps } from '../types';

const forgotSchema = z.object({
  email: z.string().email('Por favor, ingresá un email válido.'),
});

type ForgotFormValues = z.infer<typeof forgotSchema>;

export function ForgotForm({ setView }: AuthFormProps) {
  const [status, setStatus] = useState<'idle' | 'loading' | 'success'>('idle');

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<ForgotFormValues>({
    resolver: zodResolver(forgotSchema),
  });

  const onSubmit = async (_data: ForgotFormValues) => {
    setStatus('loading');
    // Simulate API call
    setTimeout(() => {
      setStatus('success');
    }, 1200);
  };

  return (
    <AuthFormWrapper
      title="Recuperar"
      subtitle="Te enviaremos instrucciones para resetear tu contraseña."
      onBack={() => setView('login')}
    >
      <AnimatePresence mode="wait">
        {status === 'success' ? (
          <motion.div
            key="success-msg"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            className="space-y-8"
          >
            <div className="bg-stone-100 p-6 border-l-2 border-stone-900">
              <p className="text-stone-900 font-medium">Enlace enviado a tu correo. Por favor, revisá tu bandeja de entrada.</p>
            </div>
            <Button onClick={() => setView('login')} className="w-full">Volver al Login</Button>
          </motion.div>
        ) : (
          <motion.form key="forgot-form" exit={{ opacity: 0 }} onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <Input 
              label="Email" 
              type="email" 
              placeholder="nombre@ejemplo.com" 
              error={errors.email?.message}
              {...register('email')}
            />
            <div className="pt-4">
              <Button type="submit" isLoading={status === 'loading'} className="w-full">Enviar Enlace</Button>
            </div>
          </motion.form>
        )}
      </AnimatePresence>
    </AuthFormWrapper>
  );
}

