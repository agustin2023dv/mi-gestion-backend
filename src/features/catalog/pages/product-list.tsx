import { useState } from 'react';
import {
  Plus,
  MoreVertical,
  Edit2,
  Trash2,
  Image as ImageIcon,
  Package
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { cn } from '../../../shared/utils/cn';
import ProductForm from '../components/product-form';
import { useProducts } from '../hooks/use-products';
import { DataTable, type Column } from '../../../shared/components/ui/data-table';
import { useDebounce } from '../../../shared/hooks/use-debounce';
import type { Product } from '../types';

export default function ProductList() {
  const [searchTerm, setSearchTerm] = useState('');
  const debouncedSearch = useDebounce(searchTerm, 500);
  const [isFormOpen, setIsFormOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<Product | null>(null);
  const [page, setPage] = useState(0);
  const pageSize = 20;

  const { products, isLoading, isError, totalElements, totalPages } = useProducts(page, pageSize, debouncedSearch);

  const handleOpenForm = (product: Product | null = null) => {
    setSelectedProduct(product);
    setIsFormOpen(true);
  };

  const handleCloseForm = () => {
    setIsFormOpen(false);
    setSelectedProduct(null);
  };

  const columns: Column<Product>[] = [
    {
      header: 'Producto',
      accessor: (product) => (
        <div className="flex items-center space-x-4">
          <div className="w-12 h-12 bg-stone-100 flex items-center justify-center text-stone-300 overflow-hidden">
            {product.imagenUrl ? (
              <img src={product.imagenUrl} alt={product.nombre} className="w-full h-full object-cover" />
            ) : (
              <ImageIcon className="w-5 h-5" />
            )}
          </div>
          <span className="text-sm font-bold text-stone-900 uppercase tracking-wide">{product.nombre}</span>
        </div>
      ),
    },
    {
      header: 'Categoría',
      accessor: (product) => <span className="text-xs font-medium text-stone-500">ID: {product.subcategoriaId}</span>,
    },
    {
      header: 'Precio',
      accessor: (product) => <span className="font-serif text-lg text-stone-900">${product.precio}</span>,
    },
    {
      header: 'Stock',
      accessor: (product) => (
        <span className={cn(
          "text-sm font-bold",
          product.stock === 0 ? "text-red-500" : product.stock < (product.stockMinimo || 5) ? "text-orange-500" : "text-stone-900"
        )}>
          {product.stock} un.
        </span>
      ),
    },
    {
      header: 'Estado',
      accessor: (product) => (
        <span className={cn(
          "px-2 py-1 text-[9px] font-bold tracking-widest uppercase",
          product.isActive ? "bg-green-50 text-green-600" : "bg-red-50 text-red-600"
        )}>
          {product.isActive ? 'Activo' : 'Inactivo'}
        </span>
      ),
    },
    {
      header: '',
      align: 'right',
      accessor: (product) => (
        <div className="flex justify-end space-x-2">
          <button
            onClick={(e) => { e.stopPropagation(); handleOpenForm(product); }}
            className="p-2 text-stone-400 hover:text-stone-900 transition-colors"
          >
            <Edit2 className="w-4 h-4" />
          </button>
          <button className="p-2 text-stone-400 hover:text-red-600 transition-colors"><Trash2 className="w-4 h-4" /></button>
          <button className="p-2 text-stone-400 hover:text-stone-900 transition-colors"><MoreVertical className="w-4 h-4" /></button>
        </div>
      ),
    },
  ];

  if (isError) {
    return (
      <div className="p-8 text-center bg-red-50 border border-red-100 rounded-2xl text-red-600">
        Error al cargar el catálogo. Por favor, intenta de nuevo.
      </div>
    );
  }

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

      <DataTable
        columns={columns}
        data={products}
        isLoading={isLoading}
        onRowClick={handleOpenForm}
        searchPlaceholder="Buscar productos..."
        onSearch={setSearchTerm}
        searchValue={searchTerm}
        page={page}
        totalPages={totalPages}
        totalElements={totalElements}
        pageSize={pageSize}
        onPageChange={setPage}
        emptyState={{
          title: 'No hay productos',
          description: 'Tu catálogo está vacío. Comenzá agregando tu primer producto.',
          icon: <Package className="w-8 h-8 text-stone-200" />
        }}
      />

      <ProductForm
        isOpen={isFormOpen}
        onClose={handleCloseForm}
        product={selectedProduct}
      />
    </div>
  );
}
