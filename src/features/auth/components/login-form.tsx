import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { AnimatePresence, motion } from 'framer-motion';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { AuthFormWrapper } from './auth-form-wrapper';
import { authApi } from '../api/auth';
import type { AuthFormProps } from '../types';
import { decodeJwt } from '../../../shared/utils/jwt-utils';

const loginSchema = z.object({
  email: z.string().email('Por favor, ingresá un email válido.'),
  password: z.string().min(6, 'La contraseña debe tener al menos 6 caracteres.'),
  remember: z.boolean().optional(),
});

import { useFormAction } from '../../../shared/hooks/use-form-action';

type LoginFormValues = z.infer<typeof loginSchema>;

export function LoginForm({ setView, onLogin }: AuthFormProps) {
  const { execute, isLoading, error: serverError } = useFormAction(authApi.login, {
    onSuccess: (data) => {
      console.log('[LoginForm] Login successful, calling onLogin with token...');
      const token = data.accessToken;
      
      // Extract tenantId from JWT if possible
      const payload = decodeJwt(token);
      if (payload?.tenantId) {
        localStorage.setItem('tenant_id', payload.tenantId);
        console.log('[LoginForm] Extracted and saved tenantId:', payload.tenantId);
      }

      onLogin?.(token);
    }
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<LoginFormValues>({
    resolver: zodResolver(loginSchema),
    defaultValues: {
      remember: false,
    }
  });

  const onSubmit = (data: LoginFormValues) => {
    console.log('[LoginForm] Submit initiated with data:', { email: data.email, remember: data.remember });
    execute({
      email: data.email,
      password: data.password,
    });
  };

  return (
    <AuthFormWrapper
      title="Iniciar Sesión"
      subtitle="Bienvenido de nuevo a mi-gestion."
      showBack={false}
    >
      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Input
          label="Email"
          type="email"
          placeholder="nombre@ejemplo.com"
          error={errors.email?.message}
          {...register('email')}
        />
        <Input
          label="Contraseña"
          type="password"
          placeholder="••••••••"
          error={errors.password?.message}
          {...register('password')}
        />

        <AnimatePresence>
          {serverError && (
            <motion.p 
              initial={{ opacity: 0, height: 0 }}
              animate={{ opacity: 1, height: 'auto' }}
              exit={{ opacity: 0, height: 0 }}
              className="text-red-600 text-sm mb-4 font-medium" 
              role="alert"
            >
              {serverError}
            </motion.p>
          )}
        </AnimatePresence>

        <div className="flex items-center justify-between mt-4 mb-6 pt-1">
          <label className="flex items-center space-x-3 cursor-pointer group">
            <div className="relative flex items-center justify-center">
              <input 
                type="checkbox" 
                className="peer sr-only"
                {...register('remember')}
              />
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

        <Button type="submit" isLoading={isLoading} className="w-full">Entrar</Button>
      </form>

      <div className="mt-8 text-center text-sm font-medium text-stone-500">
        ¿No tenés una cuenta?{' '}
        <button
          onClick={() => setView('register')}
          className="text-stone-900 underline underline-offset-4 hover:text-stone-600 transition-colors focus-visible:outline-stone-900"
        >
          Crear cuenta
        </button>
      </div>
    </AuthFormWrapper>
  );
}

