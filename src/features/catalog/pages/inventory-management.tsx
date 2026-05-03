import { useState } from 'react';
import { Plus, Edit2, X, Package } from 'lucide-react';
import { DataTable } from '../../../shared/components/ui/data-table';
import type { Column } from '../../../shared/components/ui/data-table'; // ✅ Import type
import { Button } from '../../../shared/components/ui/button';
import { StatusBadge } from '../../../shared/components/ui/status-badge';
import { useProducts } from '../hooks/use-products';
import type { Product } from '../types';
import ProductForm from '../components/product-form';

export default function InventoryManagement() {
  const [page, setPage] = useState(0);
  const pageSize = 20;
  const { products, isLoading, totalElements, totalPages, markAsOutOfStock } = useProducts(page, pageSize);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);

  const handleOpenForm = (product: Product | null = null) => {
    setSelectedProduct(product);
    setIsFormOpen(true);
  };

  // ✅ Tipado explícito para que TS valide 'align: "right"' correctamente
  const columns: Column<Product>[] = [
    {
      header: 'Producto',
      accessor: (item: Product) => (
        <div className="flex flex-col">
          <span className="font-bold text-stone-900 uppercase tracking-wide">{item.nombre}</span>
          <span className="text-[10px] text-stone-400 font-mono">{item.sku}</span>
        </div>
      ),
    },
    {
      header: 'Precio',
      accessor: (item: Product) => (
        <span className="font-serif text-lg text-stone-900">
          ${item.precio.toLocaleString('es-AR', { minimumFractionDigits: 2 })}
        </span>
      ),
    },
    {
      header: 'Stock',
      accessor: (item: Product) => (
        <div className="flex items-center space-x-2">
          <span className={`font-bold ${item.stock === 0 ? 'text-rose-500' : item.stock < 10 ? 'text-amber-500' : 'text-stone-900'}`}>
            {item.stock} un.
          </span>
        </div>
      ),
    },
    {
      header: 'Estado',
      accessor: (item: Product) => {
        let status: 'AVAILABLE' | 'LOW_STOCK' | 'OUT_OF_STOCK' = 'AVAILABLE';
        if (item.stock === 0) status = 'OUT_OF_STOCK';
        else if (item.stock < 10) status = 'LOW_STOCK';
        return <StatusBadge status={status} />;
      },
    },
    {
      header: 'Acciones',
      accessor: (item: Product) => (
        <div className="flex justify-end space-x-1">
          <button
            onClick={(e) => {
              e.stopPropagation();
              handleOpenForm(item);
            }}
            className="p-2 text-stone-400 hover:text-stone-900 transition-colors"
            title="Editar"
          >
            <Edit2 className="w-4 h-4" />
          </button>
          {item.stock > 0 && (
            <button
              onClick={(e) => {
                e.stopPropagation();
                markAsOutOfStock(item.id);
              }}
              className="p-2 text-stone-400 hover:text-rose-600 transition-colors"
              title="Marcar como Agotado"
            >
              <X className="w-4 h-4" />
            </button>
          )}
        </div>
      ),
      align: 'right',
    },
  ];

  return (
    <div className="space-y-8 max-w-6xl mx-auto">
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="font-serif text-4xl text-stone-900">Inventario</h1>
          <p className="text-stone-500 font-medium">Controla tu stock y precios de forma simple.</p>
        </div>
        <Button onClick={() => handleOpenForm()} className="md:w-auto">
          <Plus className="w-4 h-4 mr-2" /> Agregar Producto
        </Button>
      </div>

      {/* Stats Summary */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-4">
        <div className="bg-white p-6 border border-stone-100 rounded-2xl shadow-sm">
          <p className="text-[10px] font-bold tracking-widest uppercase text-stone-400 mb-1">Total Productos</p>
          <p className="text-3xl font-serif text-stone-900">{totalElements}</p>
        </div>
        <div className="bg-white p-6 border border-stone-100 rounded-2xl shadow-sm">
          <p className="text-[10px] font-bold tracking-widest uppercase text-amber-500 mb-1">Stock Bajo</p>
          <p className="text-3xl font-serif text-stone-900">
            {products.filter(p => p.stock > 0 && p.stock < 10).length}
          </p>
        </div>
        <div className="bg-white p-6 border border-stone-100 rounded-2xl shadow-sm">
          <p className="text-[10px] font-bold tracking-widest uppercase text-rose-500 mb-1">Agotados</p>
          <p className="text-3xl font-serif text-stone-900">
            {products.filter(p => p.stock === 0).length}
          </p>
        </div>
      </div>

      {/* Main Table */}
      <DataTable
        columns={columns}
        data={products}
        isLoading={isLoading}
        searchPlaceholder="Buscar por nombre o SKU..."
        searchKey={(item, term) =>
          item.nombre.toLowerCase().includes(term.toLowerCase()) ||
          item.sku.toLowerCase().includes(term.toLowerCase())
        }
        emptyState={{
          title: 'No hay productos',
          description: 'Comienza agregando tu primer producto al inventario.',
          icon: <Package className="w-8 h-8 text-stone-200" />
        }}
        footer={
          <div className="flex items-center justify-between w-full">
            <div className="text-xs font-medium text-stone-400 uppercase tracking-widest">
              Página {page + 1} de {totalPages || 1}
            </div>
            <div className="flex space-x-2">
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setPage(p => Math.max(0, p - 1))}
                disabled={page === 0}
                className="px-3 py-1.5 h-auto text-[10px]"
              >
                Anterior
              </Button>
              <Button
                variant="secondary"
                size="sm"
                onClick={() => setPage(p => p + 1)}
                disabled={page >= totalPages - 1}
                className="px-3 py-1.5 h-auto text-[10px]"
              >
                Siguiente
              </Button>
            </div>
          </div>
        }
      />

      {/* Slide-over Form */}
      <ProductForm
        isOpen={isFormOpen}
        onClose={() => setIsFormOpen(false)}
        product={selectedProduct}
      />
    </div>
  );
}