import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { motion } from 'framer-motion';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { AuthFormWrapper } from './auth-form-wrapper';
import type { AuthFormProps } from '../types';
import { authApi } from '../api/auth';

const registerSchema = z.object({
  nombreNegocio: z.string().min(1, 'El nombre del negocio es requerido').max(150),
  slug: z.string()
    .min(1, 'El slug es requerido')
    .max(150)
    .regex(/^[a-z0-9]+(?:-[a-z0-9]+)*$/, 'El slug solo puede contener letras minúsculas, números y guiones'),
  planSuscripcionId: z.number().positive(),
  propietarioNombre: z.string().min(1, 'El nombre es requerido').max(100),
  propietarioApellido: z.string().min(1, 'El apellido es requerido').max(100),
  propietarioEmail: z.string().email('Email inválido').max(255),
  propietarioTelefono: z.string().max(50).optional().or(z.literal('')),
  password: z.string()
    .min(8, 'La contraseña debe tener al menos 8 caracteres')
    .regex(/^(?=.*[A-Za-z])(?=.*\d).+$/, 'Debe contener al menos una letra y un número'),
});

import { useFormAction } from '../../../shared/hooks/use-form-action';

type RegisterFormValues = z.infer<typeof registerSchema>;

export function RegisterForm({ setView }: AuthFormProps) {
  const [success, setSuccess] = useState(false);

  const { execute, isLoading } = useFormAction(authApi.registerTenant, {
    onSuccess: () => setSuccess(true),
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormValues>({
    resolver: zodResolver(registerSchema),
    defaultValues: {
      planSuscripcionId: 1, // Default plan
    }
  });

  const onSubmit = (data: RegisterFormValues) => {
    execute(data);
  };

  if (success) {
    return (
      <motion.div
        initial={{ opacity: 0, scale: 0.95 }}
        animate={{ opacity: 1, scale: 1 }}
        className="text-center py-6"
      >
        <div className="w-12 h-12 bg-stone-900 rounded-full flex items-center justify-center mx-auto mb-4 text-stone-50">
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><polyline points="20 6 9 17 4 12"></polyline></svg>
        </div>
        <h2 className="font-serif text-3xl mb-2 text-stone-900">Negocio registrado</h2>
        <p className="text-stone-500 mb-4 font-medium">Hemos configurado tu entorno. Revisá tu email para continuar.</p>
        <Button onClick={() => setView('login')} className="w-full">Ir al Login</Button>
      </motion.div>
    );
  }

  return (
    <AuthFormWrapper
      title="Registrar Negocio"
      subtitle="Empezá a gestionar tu negocio con elegancia y precisión."
      onBack={() => setView('login')}
    >
      <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <Input 
            label="Nombre del Negocio" 
            placeholder="Mi Boutique" 
            error={errors.nombreNegocio?.message}
            {...register('nombreNegocio')}
          />
          <Input 
            label="URL (Slug)" 
            placeholder="mi-boutique" 
            error={errors.slug?.message}
            {...register('slug')}
          />
        </div>

        <div className="grid grid-cols-2 gap-4">
          <Input 
            label="Nombre" 
            placeholder="Juan" 
            error={errors.propietarioNombre?.message}
            {...register('propietarioNombre')}
          />
          <Input 
            label="Apellido" 
            placeholder="Pérez" 
            error={errors.propietarioApellido?.message}
            {...register('propietarioApellido')}
          />
        </div>

        <Input 
          label="Email del Propietario" 
          type="email" 
          placeholder="juan@ejemplo.com" 
          error={errors.propietarioEmail?.message}
          {...register('propietarioEmail')}
        />

        <Input 
          label="Teléfono (Opcional)" 
          placeholder="+54 11 ..." 
          error={errors.propietarioTelefono?.message}
          {...register('propietarioTelefono')}
        />

        <Input 
          label="Contraseña" 
          type="password" 
          placeholder="••••••••" 
          error={errors.password?.message}
          {...register('password')}
        />

        <div className="pt-2">
          <Button type="submit" isLoading={isLoading} className="w-full">Crear Cuenta</Button>
        </div>
      </form>
    </AuthFormWrapper>
  );
}

