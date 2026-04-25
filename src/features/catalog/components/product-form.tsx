import React, { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { X, Upload, Trash2 } from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';

interface ProductFormProps {
  isOpen: boolean;
  onClose: () => void;
  product?: any; // Para edición
}

export default function ProductForm({ isOpen, onClose, product }: ProductFormProps) {
  const [images, setImages] = useState<string[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const handleFakeUpload = () => {
    // Simular subida de imagen
    setImages([...images, 'https://via.placeholder.com/150']);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    setTimeout(() => {
      setIsLoading(false);
      onClose();
    }, 1500);
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
                  <p className="text-stone-500 font-medium mt-2">Completa los detalles de tu producto.</p>
                </div>
                <button onClick={onClose} className="p-2 hover:bg-stone-100 rounded-none transition-colors">
                  <X className="w-6 h-6 text-stone-400" />
                </button>
              </div>

              <form onSubmit={handleSubmit} className="space-y-10">
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
                    <Input label="Nombre del Producto" placeholder="Ej. Vela Sándalo" required />
                  </div>
                  <div className="md:col-span-2">
                    <div className="flex flex-col space-y-1 mb-6">
                      <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500 mb-1">Descripción</label>
                      <textarea
                        className="bg-transparent border-b border-stone-300 py-2 focus:outline-none focus:border-stone-900 transition-colors rounded-none w-full text-base font-medium min-h-[100px] resize-none"
                        placeholder="Describe las características de tu producto..."
                      />
                    </div>
                  </div>
                  <Input label="Precio (USD)" type="number" placeholder="0.00" required />
                  <Input label="Stock Inicial" type="number" placeholder="0" required />
                  <div className="md:col-span-2">
                    <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500 mb-2 block">Categoría</label>
                    <select className="w-full bg-transparent border-b border-stone-300 py-2 focus:outline-none focus:border-stone-900 transition-colors rounded-none text-base font-medium appearance-none cursor-pointer">
                      <option>Hogar</option>
                      <option>Decoración</option>
                      <option>Belleza</option>
                      <option>Accesorios</option>
                    </select>
                  </div>
                </div>

                {/* Actions */}
                <div className="flex flex-col sm:flex-row gap-4 pt-8">
                  <Button type="submit" isLoading={isLoading} className="flex-1">Guardar Producto</Button>
                  <Button type="button" variant="ghost" onClick={onClose} className="flex-1">Cancelar</Button>
                </div>
              </form>
            </div>
          </motion.div>
        </>
      )}
    </AnimatePresence>
  );
}
