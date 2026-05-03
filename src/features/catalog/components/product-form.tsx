import { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Upload, Trash2 } from 'lucide-react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import * as z from 'zod';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { useProducts } from '../hooks/use-products';
import { catalogApi } from '../api/productos';
import type { Category, Product } from '../types';
import DeleteConfirmModal from '../../../shared/components/ui/delete-confirm-modal';

const productSchema = z.object({
  nombre: z.string().min(1, 'El nombre es requerido'),
  descripcion: z.string().optional(),
  precio: z.number().min(0, 'El precio debe ser mayor o igual a 0'),
  stock: z.number().min(0, 'El stock debe ser mayor o igual a 0'),
  subcategoriaId: z.number().optional(), 
  sku: z.string().optional(),
});

type ProductFormData = z.infer<typeof productSchema>;

interface ProductFormProps {
  isOpen: boolean;
  onClose: () => void;
  product?: Product | null;
}

export default function ProductForm({ isOpen, onClose, product }: ProductFormProps) {
  const [images, setImages] = useState<string[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const { addProduct, updateProduct, deleteProduct, isDeleting } = useProducts();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<ProductFormData>({
    resolver: zodResolver(productSchema),
    defaultValues: {
      nombre: '',
      descripcion: '',
      precio: 0,
      stock: 0,
      sku: '',
    }
  });

  // Load categories
  useEffect(() => {
    if (isOpen) {
      catalogApi.getCategories({ size: 100 }).then(res => {
        setCategories(res.data.content);
      });
    }
  }, [isOpen]);

  // Pre-fill form when product changes
  useEffect(() => {
    if (product) {
      reset({
        nombre: product.nombre,
        descripcion: product.descripcion || '',
        precio: product.precio,
        stock: product.stock,
        subcategoriaId: product.subcategoriaId,
        sku: product.sku || '',
      });
      setImages(product.imagenUrl ? [product.imagenUrl] : []);
    } else {
      reset({
        nombre: '',
        descripcion: '',
        precio: 0,
        stock: 0,
        subcategoriaId: undefined,
        sku: '',
      });
      setImages([]);
    }
  }, [product, reset, isOpen]);

  const handleFakeUpload = () => {
    setImages([...images, 'https://images.unsplash.com/photo-1602872030219-3df6d82497ac?auto=format&fit=crop&q=80&w=200']);
  };

  const handleDelete = async () => {
    if (product) {
      try {
        setDeleteError(null);
        await deleteProduct(product.id);
        setIsDeleteModalOpen(false);
        onClose();
      } catch (error: any) {
        console.error('Error deleting product:', error);
        setDeleteError(error.response?.data?.message || 'Error al eliminar el producto.');
      }
    }
  };

  const handleOpenDelete = () => {
    setDeleteError(null);
    setIsDeleteModalOpen(true);
  };

  const onSubmit = async (data: ProductFormData) => {
    setIsSubmitting(true);
    try {
      const payload = {
        ...data,
        imagenUrl: images[0], // Using the first image for now
        isActive: true,
      };

      if (product) {
        await updateProduct({ id: product.id, data: payload });
      } else {
        await addProduct(payload as any);
      }
      onClose();
    } catch (error) {
      console.error('Error saving product:', error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AnimatePresence>
      {isOpen && (
        <>
          {/* Backdrop */}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={onClose}
            className="fixed inset-0 bg-stone-900/20 backdrop-blur-sm z-[60]"
          />

          {/* Slide-over Panel */}
          <motion.div
            initial={{ x: '100%' }}
            animate={{ x: 0 }}
            exit={{ x: '100%' }}
            transition={{ type: 'spring', damping: 25, stiffness: 200 }}
            className="fixed right-0 top-0 h-full w-full max-w-xl bg-white shadow-2xl z-[70] overflow-y-auto"
          >
            <div className="p-8 lg:p-12 space-y-12">
              {/* Header */}
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="font-serif text-4xl text-stone-900">
                    {product ? 'Editar Producto' : 'Nuevo Producto'}
                  </h2>
                  <p className="text-stone-500 font-medium mt-2">
                    {product ? 'Modifica los detalles de tu producto.' : 'Completa los detalles de tu nuevo producto.'}
                  </p>
                </div>
                <button onClick={onClose} className="p-2 hover:bg-stone-100 rounded-none transition-colors">
                  <X className="w-6 h-6 text-stone-400" />
                </button>
              </div>

              <form onSubmit={handleSubmit(onSubmit)} className="space-y-10">
                {/* Image Upload Area */}
                <div className="space-y-4">
                  <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Imágenes del Producto</label>
                  <div className="grid grid-cols-3 gap-4">
                    {images.map((img, idx) => (
                      <div key={idx} className="relative aspect-square bg-stone-100 group">
                        <img src={img} alt="Product" className="w-full h-full object-cover" />
                        <button
                          type="button"
                          className="absolute inset-0 bg-stone-900/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center text-white"
                          onClick={() => setImages(images.filter((_, i) => i !== idx))}
                        >
                          <Trash2 className="w-5 h-5" />
                        </button>
                      </div>
                    ))}
                    <button
                      type="button"
                      onClick={handleFakeUpload}
                      className="aspect-square border-2 border-dashed border-stone-200 flex flex-col items-center justify-center space-y-2 text-stone-400 hover:border-stone-900 hover:text-stone-900 transition-all group"
                    >
                      <Upload className="w-6 h-6 group-hover:-translate-y-1 transition-transform" />
                      <span className="text-[10px] font-bold tracking-widest uppercase">Subir</span>
                    </button>
                  </div>
                </div>

                {/* Form Fields */}
                <div className="grid grid-cols-1 md:grid-cols-2 gap-x-8 gap-y-6">
                  <div className="md:col-span-2">
                    <Input 
                      label="Nombre del Producto" 
                      placeholder="Ej. Vela Sándalo" 
                      {...register('nombre')}
                      error={errors.nombre?.message}
                    />
                  </div>
                  <div className="md:col-span-2">
                    <Input 
                      label="SKU / Código de Barras" 
                      placeholder="Ej. VELA-001" 
                      {...register('sku')}
                      error={errors.sku?.message}
                    />
                  </div>
                  <div className="md:col-span-2">
                    <div className="flex flex-col space-y-1 mb-6">
                      <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500 mb-1">Descripción</label>
                      <textarea
                        {...register('descripcion')}
                        className="bg-transparent border-b border-stone-300 py-2 focus:outline-none focus:border-stone-900 transition-colors rounded-none w-full text-base font-medium min-h-[100px] resize-none"
                        placeholder="Describe las características de tu producto..."
                      />
                    </div>
                  </div>
                  <Input 
                    label="Precio" 
                    type="number" 
                    placeholder="0.00" 
                    {...register('precio', { valueAsNumber: true })}
                    error={errors.precio?.message}
                  />
                  <Input 
                    label="Stock Inicial" 
                    type="number" 
                    placeholder="0" 
                    {...register('stock', { valueAsNumber: true })}
                    error={errors.stock?.message}
                  />
                  <div className="md:col-span-2">
                    <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500 mb-2 block">Categoría</label>
                    <select 
                      {...register('subcategoriaId', { 
                        setValueAs: (v) => v === "" ? undefined : Number(v) 
                      })}
                      className="w-full bg-transparent border-b border-stone-300 py-2 focus:outline-none focus:border-stone-900 transition-colors rounded-none text-base font-medium appearance-none cursor-pointer"
                    >
                      <option value="">Seleccionar categoría</option>
                      {categories.map((cat) => (
                        <option key={cat.id} value={cat.id}>{cat.nombre}</option>
                      ))}
                    </select>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex flex-col gap-4 pt-8">
                  <div className="flex flex-col sm:flex-row gap-4">
                    <Button type="submit" isLoading={isSubmitting} className="flex-1">
                      {product ? 'Actualizar Producto' : 'Guardar Producto'}
                    </Button>
                    <Button type="button" variant="ghost" onClick={onClose} className="flex-1">Cancelar</Button>
                  </div>
                  
                  {product && (
                    <button
                      type="button"
                      onClick={handleOpenDelete}
                      className="text-[10px] font-bold tracking-[0.2em] uppercase text-red-500 hover:text-red-700 transition-colors py-2 flex items-center justify-center gap-2 border-t border-stone-100 pt-6"
                    >
                      <Trash2 className="w-3 h-3" />
                      Eliminar permanentemente
                    </button>
                  )}
                </div>
              </form>
            </div>
          </motion.div>

          <DeleteConfirmModal
            isOpen={isDeleteModalOpen}
            onClose={() => setIsDeleteModalOpen(false)}
            onConfirm={handleDelete}
            title="¿Eliminar producto?"
            description={`Estás por eliminar "${product?.nombre}". Esta acción es permanente.`}
            isLoading={isDeleting}
            error={deleteError}
          />
        </>
      )}
    </AnimatePresence>
  );
}
