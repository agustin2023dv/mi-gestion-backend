import { useState } from 'react';
import {
  Plus,
  Search,
  Filter,
  MoreVertical,
  Edit2,
  Trash2,
  Image as ImageIcon
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { cn } from '../../../shared/utils/cn';
import ProductForm from '../components/product-form';

const MOCK_PRODUCTS = [
  { id: 1, name: 'Vela Minimalista Sándalo', category: 'Hogar', price: 1200, stock: 15, status: 'Active' },
  { id: 2, name: 'Difusor Lavanda', category: 'Hogar', price: 2500, stock: 2, status: 'Low Stock' },
  { id: 3, name: 'Set de Cerámica Oasis', category: 'Decoración', price: 5800, stock: 0, status: 'Out of Stock' },
  { id: 4, name: 'Jabón Orgánico Coco', category: 'Belleza', price: 850, stock: 45, status: 'Active' },
  { id: 5, name: 'Espejo Sol', category: 'Decoración', price: 12400, stock: 5, status: 'Active' },
];

export default function ProductList() {
  const [searchTerm, setSearchTerm] = useState('');
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<any>(null);

  const handleOpenForm = (product: any = null) => {
    setSelectedProduct(product);
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setSelectedProduct(null);
  };

  return (
    <div className="space-y-8">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="font-serif text-4xl text-stone-900">Catálogo</h1>
          <p className="text-stone-500 font-medium">Gestiona tus productos, stock y precios.</p>
        </div>
        <Button className="md:w-auto" onClick={() => handleOpenForm()}>
          <Plus className="w-4 h-4 mr-2" /> Nuevo Producto
        </Button>
      </div>

      {/* Filters & Search */}
      <div className="flex flex-col md:flex-row gap-4 items-center bg-white p-4 border border-stone-100">
        <div className="relative flex-1 w-full">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-stone-400" />
          <input
            type="text"
            placeholder="Buscar productos..."
            className="w-full pl-10 pr-4 py-2 bg-stone-50 border-none focus:ring-1 focus:ring-stone-900 text-sm outline-none transition-all"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
        <div className="flex gap-2 w-full md:w-auto">
          <Button variant="secondary" size="sm" className="flex-1 md:flex-none">
            <Filter className="w-3 h-3 mr-2" /> Filtros
          </Button>
        </div>
      </div>

      {/* Product Table */}
      <div className="bg-white border border-stone-100 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-stone-100 bg-stone-50/50">
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Producto</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Categoría</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Precio</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Stock</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Estado</th>
                <th className="px-6 py-4 text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400 text-right">Acciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-stone-50">
              {MOCK_PRODUCTS.map((product) => (
                <tr key={product.id} className="group hover:bg-stone-50/30 transition-colors">
                  <td className="px-6 py-5">
                    <div className="flex items-center space-x-4">
                      <div className="w-12 h-12 bg-stone-100 flex items-center justify-center text-stone-300">
                        <ImageIcon className="w-5 h-5" />
                      </div>
                      <span className="text-sm font-bold text-stone-900 uppercase tracking-wide">{product.name}</span>
                    </div>
                  </td>
                  <td className="px-6 py-5 text-xs font-medium text-stone-500">{product.category}</td>
                  <td className="px-6 py-5 font-serif text-lg text-stone-900">${product.price}</td>
                  <td className="px-6 py-5">
                    <span className={cn(
                      "text-sm font-bold",
                      product.stock === 0 ? "text-red-500" : product.stock < 5 ? "text-orange-500" : "text-stone-900"
                    )}>
                      {product.stock} un.
                    </span>
                  </td>
                  <td className="px-6 py-5">
                    <span className={cn(
                      "px-2 py-1 text-[9px] font-bold tracking-widest uppercase",
                      product.status === 'Active' ? "bg-green-50 text-green-600" :
                        product.status === 'Low Stock' ? "bg-orange-50 text-orange-600" :
                          "bg-red-50 text-red-600"
                    )}>
                      {product.status}
                    </span>
                  </td>
                  <td className="px-6 py-5 text-right">
                    <div className="flex justify-end space-x-2">
                      <button
                        onClick={() => handleOpenForm(product)}
                        className="p-2 text-stone-400 hover:text-stone-900 transition-colors"
                      >
                        <Edit2 className="w-4 h-4" />
                      </button>
                      <button className="p-2 text-stone-400 hover:text-red-600 transition-colors"><Trash2 className="w-4 h-4" /></button>
                      <button className="p-2 text-stone-400 hover:text-stone-900 transition-colors"><MoreVertical className="w-4 h-4" /></button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Slide-over Form */}
      <ProductForm
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        product={selectedProduct}
      />
    </div>
  );
}
