import { useState } from 'react';
import { ShoppingBag, Search, Plus, Minus, X, ArrowLeft } from 'lucide-react';
import { useTenant } from '../hooks/use-tenant';
import { Button } from '../../../shared/components/ui/button';
import { motion, AnimatePresence } from 'framer-motion';
import { useNavigate } from 'react-router-dom';
import { useDesign } from '../../../shared/contexts/design-context';
import { cn } from '../../../shared/utils/cn';

import { useQuery } from '@tanstack/react-query';
import { catalogApi } from '../../catalog/api/productos';
import { useEffect } from 'react';

export default function MenuPage() {
  const { tenant, error } = useTenant();
  const navigate = useNavigate();
  const { settings } = useDesign();
  const [cart, setCart] = useState<Record<number, number>>({});
  const [isCartOpen, setIsCartOpen] = useState(false);
  const [selectedProduct, setSelectedProduct] = useState<any | null>(null);

  useEffect(() => {
    if (tenant?.id) {
      localStorage.setItem('tenant_id', tenant.id);
    }
  }, [tenant]);

  const { data: productsResponse, isLoading: productsLoading } = useQuery({
    queryKey: ['tenant-products', tenant?.id],
    queryFn: () => catalogApi.getProducts({ size: 100 }),
    enabled: !!tenant?.id,
  });

  if (error) return <div className="p-10 text-center">Tenant not found</div>;
  if (!tenant) return null;

  const products = productsResponse?.data?.content?.map(p => ({
    ...p,
    name: p.nombre,
    description: p.descripcion,
    price: p.precio,
    image: p.imagenUrl || 'https://images.unsplash.com/photo-1585478259715-876acc5be8eb?auto=format&fit=crop&q=80&w=300&h=200', // Fallback image
    category: 'General' // We might want to fetch real category name if available
  })) || [];

  const addToCart = (productId: number) => {
    setCart(prev => ({
      ...prev,
      [productId]: (prev[productId] || 0) + 1
    }));
  };

  const removeFromCart = (productId: number) => {
    setCart(prev => {
      const newCart = { ...prev };
      if (newCart[productId] > 1) {
        newCart[productId] -= 1;
      } else {
        delete newCart[productId];
      }
      return newCart;
    });
  };

  const cartItemsCount = Object.values(cart).reduce((a, b) => a + b, 0);
  const cartTotal = products.reduce((total, p) => total + (p.price * (cart[p.id] || 0)), 0);

  const productsByCategory = products.reduce((acc, p) => {
    const cat = p.category || 'Otros';
    if (!acc[cat]) acc[cat] = [];
    acc[cat].push(p);
    return acc;
  }, {} as Record<string, any[]>);

  const scrollToCategory = (category: string) => {
    const el = document.getElementById(`category-${category}`);
    if (el) {
      const offset = 140; // Header + Sticky nav offset
      const top = el.getBoundingClientRect().top + window.scrollY - offset;
      window.scrollTo({ top, behavior: 'smooth' });
    }
  };

  const gridColsClass = {
    1: 'grid-cols-1',
    2: 'grid-cols-1 md:grid-cols-2',
    3: 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3'
  }[settings.gridColumns];

  const fontClass = {
    sans: 'font-sans',
    serif: 'font-serif',
    mono: 'font-mono'
  }[settings.fontFamily];

  return (
    <div className={cn("min-h-screen pb-28 transition-colors duration-500", fontClass)} style={{ backgroundColor: settings.backgroundColor }}>
      {/* Hero Section */}
      <header className="bg-white border-b border-stone-200 sticky top-0 z-20">
        <div className="max-w-5xl mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-3">
            <div 
              className="w-10 h-10 rounded-full flex items-center justify-center text-white font-serif font-bold italic transition-colors"
              style={{ backgroundColor: settings.primaryColor }}
            >
              {tenant.name.charAt(0)}
            </div>
            <h1 className="font-serif text-xl font-bold tracking-tight">{tenant.name}</h1>
          </div>
          <button 
            onClick={() => setIsCartOpen(true)}
            className="relative p-2 text-stone-600 hover:text-stone-900 transition-colors"
          >
            <ShoppingBag className="w-6 h-6" />
            {cartItemsCount > 0 && (
              <span 
                className="absolute -top-1 -right-1 text-white text-[10px] font-bold w-5 h-5 flex items-center justify-center rounded-full border-2 border-white"
                style={{ backgroundColor: settings.primaryColor }}
              >
                {cartItemsCount}
              </span>
            )}
          </button>
        </div>
      </header>

      <main className="max-w-5xl mx-auto px-4 py-6">
        <div className="mb-6">
          <p className="text-stone-500 font-medium leading-relaxed max-w-lg">
            {tenant.description}
          </p>
        </div>

        {/* Search */}
        <div className="relative mb-6">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-stone-400" />
          <input 
            type="text" 
            placeholder="¿Qué estás buscando hoy?"
            className="w-full bg-white border border-stone-200 rounded-2xl py-4 pl-12 pr-4 text-stone-900 focus:outline-none focus:ring-2 focus:ring-stone-900/5 transition-all shadow-sm"
          />
        </div>

        {/* Sticky Categories Navigation */}
        {settings.showCategories && (
          <div className="sticky top-16 z-10 py-3 mb-6 -mx-4 px-4 overflow-x-auto no-scrollbar border-b border-stone-100 flex gap-2 shadow-sm backdrop-blur-xl bg-white/80">
            {Object.keys(productsByCategory).map(category => (
              <button
                key={category}
                onClick={() => scrollToCategory(category)}
                className="whitespace-nowrap px-4 py-2 rounded-full font-bold text-xs uppercase tracking-wider transition-all border"
                style={{ 
                  borderColor: settings.primaryColor,
                  color: settings.primaryColor,
                  backgroundColor: 'transparent'
                }}
              >
                {category}
              </button>
            ))}
          </div>
        )}

        {/* Products Grid */}
        {settings.showCategories ? (
          <div className="space-y-12">
            {(Object.entries(productsByCategory) as [string, any[]][]).map(([category, items]) => (
              <div key={category} id={`category-${category}`} className="space-y-6 pt-2 scroll-mt-36">
                <h2 className="font-serif text-2xl font-bold text-stone-900">
                  {category}
                </h2>
                <div className={cn("grid gap-4", gridColsClass)}>
                  {items.map((product) => (
                    <ProductCard 
                      key={product.id} 
                      product={product} 
                      cart={cart} 
                      addToCart={addToCart} 
                      removeFromCart={removeFromCart} 
                      settings={settings}
                      onSelect={() => setSelectedProduct(product)}
                    />
                  ))}
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className={cn("grid gap-4", gridColsClass)}>
            {products.map((product) => (
              <ProductCard 
                key={product.id} 
                product={product} 
                cart={cart} 
                addToCart={addToCart} 
                removeFromCart={removeFromCart} 
                settings={settings}
                onSelect={() => setSelectedProduct(product)}
              />
            ))}
          </div>
        )}
      </main>

      {/* Floating Checkout Bar (Mobile Native Feel) */}
      <AnimatePresence>
        {cartItemsCount > 0 && !isCartOpen && (
          <motion.div 
            initial={{ y: 100, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            exit={{ y: 100, opacity: 0 }}
            className="fixed bottom-0 left-0 right-0 z-20 md:bottom-6 md:left-auto md:right-6 md:w-96"
          >
            <div className="bg-white/80 backdrop-blur-md border-t border-stone-200 md:border md:rounded-2xl md:shadow-2xl p-4 pb-safe">
              <Button 
                onClick={() => setIsCartOpen(true)}
                className="w-full h-14 rounded-xl shadow-lg flex items-center justify-between px-6 hover:opacity-90 transition-opacity"
                style={{ backgroundColor: settings.primaryColor }}
              >
                <div className="flex items-center gap-3">
                  <span className="bg-white/20 w-8 h-8 rounded-lg flex items-center justify-center font-bold text-sm">
                    {cartItemsCount}
                  </span>
                  <span className="font-bold uppercase tracking-widest text-xs">Ver Pedido</span>
                </div>
                <span className="font-serif font-bold text-lg">${cartTotal}</span>
              </Button>
            </div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* Product Detail Modal */}
      <AnimatePresence>
        {selectedProduct && (
          <>
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setSelectedProduct(null)}
              className="fixed inset-0 bg-stone-900/60 backdrop-blur-sm z-40"
            />
            <motion.div 
              initial={{ y: '100%' }}
              animate={{ y: 0 }}
              exit={{ y: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className={cn("fixed bottom-0 left-0 right-0 max-h-[90vh] bg-white z-50 flex flex-col md:top-1/2 md:bottom-auto md:left-1/2 md:-translate-x-1/2 md:-translate-y-1/2 md:w-full md:max-w-lg md:rounded-3xl md:overflow-hidden rounded-t-3xl overflow-hidden", fontClass)}
            >
              <div className="relative h-64 md:h-80 bg-stone-100 shrink-0">
                <img 
                  src={selectedProduct.image} 
                  alt={selectedProduct.name} 
                  className="w-full h-full object-cover"
                />
                <button 
                  onClick={() => setSelectedProduct(null)}
                  className="absolute top-4 right-4 bg-white/50 backdrop-blur-md p-2 rounded-full text-stone-900 hover:bg-white transition-colors"
                >
                  <X className="w-5 h-5" />
                </button>
              </div>
              <div className="p-6 overflow-y-auto pb-safe">
                <div className="flex justify-between items-start mb-2">
                  <h2 className="font-bold text-2xl text-stone-900 uppercase tracking-tight">{selectedProduct.name}</h2>
                  <span className="font-serif font-bold text-2xl text-stone-900">${selectedProduct.price}</span>
                </div>
                <p className="text-stone-500 mb-8 leading-relaxed">
                  {selectedProduct.description}
                </p>
                
                <div className="flex items-center gap-4 mt-auto">
                  {cart[selectedProduct.id] ? (
                    <div className="flex-1 flex items-center justify-between bg-stone-100 rounded-2xl p-2 px-6 h-14">
                      <button 
                        onClick={() => removeFromCart(selectedProduct.id)}
                        className="w-10 h-10 flex items-center justify-center rounded-full bg-white shadow-sm hover:scale-105 transition-transform text-stone-900"
                      >
                        <Minus className="w-5 h-5" />
                      </button>
                      <span className="font-bold text-xl">{cart[selectedProduct.id]}</span>
                      <button 
                        onClick={() => addToCart(selectedProduct.id)}
                        className="w-10 h-10 flex items-center justify-center rounded-full bg-white shadow-sm hover:scale-105 transition-transform text-stone-900"
                      >
                        <Plus className="w-5 h-5" />
                      </button>
                    </div>
                  ) : (
                    <Button 
                      onClick={() => addToCart(selectedProduct.id)}
                      className="w-full h-14 rounded-2xl font-bold uppercase tracking-widest text-sm hover:opacity-90 transition-opacity"
                      style={{ backgroundColor: settings.primaryColor }}
                    >
                      Añadir por ${selectedProduct.price}
                    </Button>
                  )}
                </div>
              </div>
            </motion.div>
          </>
        )}
      </AnimatePresence>

      {/* Cart Sidebar / Drawer */}
      <AnimatePresence>
        {isCartOpen && (
          <>
            <motion.div 
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              exit={{ opacity: 0 }}
              onClick={() => setIsCartOpen(false)}
              className="fixed inset-0 bg-stone-900/40 backdrop-blur-sm z-40"
            />
            <motion.div 
              initial={{ x: '100%' }}
              animate={{ x: 0 }}
              exit={{ x: '100%' }}
              transition={{ type: 'spring', damping: 25, stiffness: 200 }}
              className="fixed top-0 right-0 h-full w-full max-w-md bg-white z-50 shadow-2xl flex flex-col"
            >
              <div className="p-6 border-b border-stone-100 flex items-center gap-4">
                <button onClick={() => setIsCartOpen(false)} className="p-2 hover:bg-stone-50 rounded-full transition-colors">
                  <ArrowLeft className="w-6 h-6 text-stone-900" />
                </button>
                <h2 className="font-serif text-2xl font-bold">Tu Pedido</h2>
              </div>

              <div className="flex-1 overflow-y-auto p-6 space-y-6">
                {cartItemsCount === 0 ? (
                  <div className="h-full flex flex-col items-center justify-center text-stone-400">
                    <ShoppingBag className="w-12 h-12 mb-4 opacity-20" />
                    <p className="font-medium">Tu carrito está vacío</p>
                  </div>
                ) : (
                  products.filter(p => cart[p.id]).map(product => (
                    <div key={product.id} className="flex gap-4">
                      <div className="w-20 h-20 bg-stone-100 rounded-2xl overflow-hidden shrink-0">
                        <img src={product.image} alt={product.name} className="w-full h-full object-cover" />
                      </div>
                      <div className="flex-1 flex flex-col justify-between py-1">
                        <div>
                          <h4 className="font-bold text-stone-900 uppercase tracking-tight text-sm">{product.name}</h4>
                          <p className="text-stone-500 text-xs">${product.price} c/u</p>
                        </div>
                        <div className="flex items-center justify-between">
                          <div className="flex items-center bg-stone-50 rounded-full p-0.5 border border-stone-100">
                            <button 
                              onClick={() => removeFromCart(product.id)}
                              className="w-7 h-7 flex items-center justify-center rounded-full hover:bg-white shadow-sm transition-colors"
                            >
                              <Minus className="w-3 h-3" />
                            </button>
                            <span className="w-6 text-center text-xs font-bold">{cart[product.id]}</span>
                            <button 
                              onClick={() => addToCart(product.id)}
                              className="w-7 h-7 flex items-center justify-center rounded-full hover:bg-white shadow-sm transition-colors"
                            >
                              <Plus className="w-3 h-3" />
                            </button>
                          </div>
                          <span className="font-serif font-bold text-stone-900">${product.price * cart[product.id]}</span>
                        </div>
                      </div>
                    </div>
                  ))
                )}
              </div>

              {cartItemsCount > 0 && (
                <div className="p-6 border-t border-stone-100 bg-stone-50/50 pb-safe">
                  <div className="flex justify-between items-center mb-6">
                    <span className="text-stone-500 font-medium">Subtotal</span>
                    <span className="font-serif text-2xl font-bold">${cartTotal}</span>
                  </div>
                  <Button 
                    onClick={() => navigate(`/${tenant.slug}/checkout`)}
                    className="w-full h-14 rounded-2xl font-bold uppercase tracking-widest text-xs hover:opacity-90 transition-opacity"
                    style={{ backgroundColor: settings.primaryColor }}
                  >
                    Confirmar Pedido
                  </Button>
                </div>
              )}
            </motion.div>
          </>
        )}
      </AnimatePresence>
    </div>
  );
}

function ProductCard({ product, cart, addToCart, removeFromCart, settings, onSelect }: any) {
  const cardStyles = {
    minimal: "bg-white rounded-3xl border border-stone-100 shadow-sm",
    glass: "bg-white/70 backdrop-blur-md rounded-[2rem] border border-white/20 shadow-xl",
    bordered: "bg-transparent rounded-none border-2 border-stone-900 shadow-none"
  }[settings.cardStyle as 'minimal' | 'glass' | 'bordered'];

  return (
    <motion.div 
      layout
      className={cn("overflow-hidden group transition-all duration-300 flex md:block", cardStyles)}
    >
      <div 
        className="w-32 h-32 md:w-full md:h-48 overflow-hidden bg-stone-100 cursor-pointer shrink-0"
        onClick={onSelect}
      >
        <img 
          src={product.image} 
          alt={product.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
        />
      </div>
      <div className="p-4 md:p-5 flex flex-col flex-1">
        <div className="flex justify-between items-start mb-1 cursor-pointer" onClick={onSelect}>
          <h3 className="font-bold text-stone-900 uppercase tracking-wide text-sm leading-tight pr-2">{product.name}</h3>
          <span className="font-serif font-bold text-base md:text-lg text-stone-900">${product.price}</span>
        </div>
        <p 
          className="text-stone-500 text-xs mb-4 line-clamp-2 leading-relaxed cursor-pointer hidden md:block"
          onClick={onSelect}
        >
          {product.description}
        </p>
        <div className="mt-auto">
          {cart[product.id] ? (
            <div className="flex items-center justify-between bg-stone-100 rounded-full p-1">
              <button 
                onClick={(e) => { e.stopPropagation(); removeFromCart(product.id); }}
                className="w-8 h-8 flex items-center justify-center rounded-full bg-white shadow-sm hover:scale-105 transition-transform"
              >
                <Minus className="w-4 h-4 text-stone-900" />
              </button>
              <span className="px-2 font-bold text-sm text-stone-900">{cart[product.id]}</span>
              <button 
                onClick={(e) => { e.stopPropagation(); addToCart(product.id); }}
                className="w-8 h-8 flex items-center justify-center rounded-full bg-white shadow-sm hover:scale-105 transition-transform"
              >
                <Plus className="w-4 h-4 text-stone-900" />
              </button>
            </div>
          ) : (
            <Button 
              onClick={(e) => { e.stopPropagation(); addToCart(product.id); }}
              variant="secondary" 
              className="w-full rounded-full h-9 text-[10px] md:text-xs font-bold uppercase tracking-widest hover:opacity-90 transition-opacity"
              style={{ 
                backgroundColor: settings.primaryColor, 
                color: 'white',
                borderRadius: settings.cardStyle === 'bordered' ? 0 : '9999px'
              }}
            >
              Agregar
            </Button>
          )}
        </div>
      </div>
    </motion.div>
  );
}
