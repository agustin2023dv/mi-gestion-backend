import { motion } from 'framer-motion';
import { ArrowRight, ShoppingBag, Smartphone, CreditCard, Box, TrendingUp, CheckCircle2 } from 'lucide-react';
import { Link } from 'react-router-dom';

const fadeIn = {
  hidden: { opacity: 0, y: 20 },
  visible: { opacity: 1, y: 0, transition: { duration: 0.6, ease: [0.22, 1, 0.36, 1] } }
};

const staggerContainer = {
  hidden: { opacity: 0 },
  visible: {
    opacity: 1,
    transition: {
      staggerChildren: 0.1
    }
  }
};

export default function LandingPage() {
  return (
    <div className="min-h-screen bg-[#faf9f6] text-[#1a1a1a] overflow-x-hidden selection:bg-emerald-200">
      {/* Navbar */}
      <nav className="fixed top-0 w-full z-50 bg-[#faf9f6]/80 backdrop-blur-md border-b border-black/5">
        <div className="max-w-7xl mx-auto px-6 h-20 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-emerald-500 rounded-xl flex items-center justify-center text-white font-bold text-xl">
              P
            </div>
            <span className="font-bold text-2xl tracking-tight">Pedix</span>
          </div>
          <div className="hidden md:flex items-center gap-8 font-medium text-stone-600">
            <a href="#como-funciona" className="hover:text-black transition-colors">¿Cómo funciona?</a>
            <a href="#caracteristicas" className="hover:text-black transition-colors">Características</a>
            <a href="#precios" className="hover:text-black transition-colors">Precios</a>
          </div>
          <div className="flex items-center gap-4">
            <Link to="/login" className="hidden md:block font-medium text-stone-600 hover:text-black transition-colors">
              Iniciar Sesión
            </Link>
            <Link to="/login" className="bg-black text-white px-6 py-2.5 rounded-full font-medium hover:bg-stone-800 transition-all hover:scale-105 active:scale-95">
              Quiero empezar
            </Link>
          </div>
        </div>
      </nav>

      {/* Hero Section */}
      <section className="relative pt-40 pb-20 px-6 overflow-hidden">
        <div className="absolute inset-0 top-[-20%] w-full h-full bg-[radial-gradient(circle_at_50%_0%,rgba(16,185,129,0.1),transparent_50%)]" />
        
        <div className="max-w-7xl mx-auto grid lg:grid-cols-2 gap-12 items-center relative z-10">
          <motion.div 
            initial="hidden"
            animate="visible"
            variants={staggerContainer}
            className="max-w-2xl"
          >
            <motion.div variants={fadeIn} className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-emerald-100 text-emerald-800 font-medium text-sm mb-6 border border-emerald-200">
              <span className="relative flex h-2 w-2">
                <span className="animate-ping absolute inline-flex h-full w-full rounded-full bg-emerald-400 opacity-75"></span>
                <span className="relative inline-flex rounded-full h-2 w-2 bg-emerald-500"></span>
              </span>
              +10.000.000 de pedidos procesados
            </motion.div>
            
            <motion.h1 variants={fadeIn} className="text-5xl lg:text-7xl font-bold tracking-tight leading-[1.1] mb-6">
              Tu tienda online para vender por <span className="text-emerald-500">WhatsApp</span>
            </motion.h1>
            
            <motion.p variants={fadeIn} className="text-lg lg:text-xl text-stone-600 mb-8 max-w-lg leading-relaxed">
              Pedix es la plataforma para crear tu tienda online y recibir pedidos organizados por WhatsApp, sin comisiones ni apps para descargar.
            </motion.p>
            
            <motion.div variants={fadeIn} className="flex flex-col sm:flex-row gap-4">
              <Link to="/login" className="bg-emerald-500 text-white px-8 py-4 rounded-full font-semibold text-lg flex items-center justify-center gap-2 hover:bg-emerald-600 transition-all shadow-[0_8px_30px_rgb(16,185,129,0.3)] hover:shadow-[0_8px_40px_rgb(16,185,129,0.4)] hover:-translate-y-1">
                Crear mi tienda gratis <ArrowRight className="w-5 h-5" />
              </Link>
              <button className="bg-white border-2 border-stone-200 text-stone-800 px-8 py-4 rounded-full font-semibold text-lg hover:border-stone-300 hover:bg-stone-50 transition-all">
                Ver precios
              </button>
            </motion.div>
            
            <motion.div variants={fadeIn} className="mt-10 flex items-center gap-4 text-sm text-stone-500 font-medium">
              <div className="flex items-center gap-1"><CheckCircle2 className="w-4 h-4 text-emerald-500"/> Sin comisiones por venta</div>
              <div className="flex items-center gap-1"><CheckCircle2 className="w-4 h-4 text-emerald-500"/> 14 días de prueba gratis</div>
            </motion.div>
          </motion.div>

          {/* Hero Visual Mockup */}
          <motion.div 
            initial={{ opacity: 0, scale: 0.95, rotate: -2 }}
            animate={{ opacity: 1, scale: 1, rotate: 0 }}
            transition={{ duration: 0.8, ease: "easeOut", delay: 0.2 }}
            className="relative lg:h-[600px] flex justify-center items-center"
          >
            <div className="relative w-[300px] h-[600px] bg-white rounded-[40px] border-[8px] border-stone-900 shadow-2xl overflow-hidden z-20">
              <div className="absolute top-0 w-full h-6 bg-stone-900 flex justify-center rounded-b-2xl z-30">
                <div className="w-16 h-4 bg-black rounded-b-xl" />
              </div>
              <div className="h-full w-full bg-stone-50 p-4 pt-10 overflow-hidden flex flex-col">
                <div className="flex items-center gap-3 mb-6">
                  <div className="w-12 h-12 bg-amber-200 rounded-full flex items-center justify-center font-bold text-amber-800">B</div>
                  <div>
                    <h3 className="font-bold">Burger House</h3>
                    <p className="text-xs text-stone-500">Abierto hasta 23:00</p>
                  </div>
                </div>
                
                <div className="space-y-4 flex-1">
                  {[1, 2, 3].map((i) => (
                    <div key={i} className="bg-white p-3 rounded-2xl shadow-sm border border-stone-100 flex gap-3">
                      <div className="w-20 h-20 bg-stone-200 rounded-xl overflow-hidden flex-shrink-0">
                         <img src={`https://source.unsplash.com/random/200x200/?burger&sig=${i}`} className="w-full h-full object-cover opacity-80" alt="Product" />
                      </div>
                      <div className="flex flex-col justify-between flex-1">
                        <div>
                          <h4 className="font-bold text-sm">Doble Bacon Smash</h4>
                          <p className="text-xs text-stone-500 line-clamp-1">Doble carne, cheddar, bacon.</p>
                        </div>
                        <div className="flex items-center justify-between mt-2">
                          <span className="font-bold text-sm">$8.500</span>
                          <button className="bg-emerald-100 text-emerald-700 w-6 h-6 rounded-md flex items-center justify-center font-bold text-lg leading-none">+</button>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
                
                <div className="mt-4 bg-emerald-500 text-white p-3 rounded-2xl flex items-center justify-between shadow-lg">
                  <div className="flex flex-col">
                    <span className="text-xs opacity-80">Total (2 items)</span>
                    <span className="font-bold">$17.000</span>
                  </div>
                  <span className="font-semibold text-sm">Hacer pedido</span>
                </div>
              </div>
            </div>
            
            {/* Decorative elements behind phone */}
            <div className="absolute top-1/4 right-0 w-32 h-32 bg-emerald-400 rounded-full mix-blend-multiply filter blur-3xl opacity-30 animate-pulse"></div>
            <div className="absolute bottom-1/4 left-0 w-40 h-40 bg-amber-400 rounded-full mix-blend-multiply filter blur-3xl opacity-20"></div>
          </motion.div>
        </div>
      </section>

      {/* How it works */}
      <section id="como-funciona" className="py-24 bg-white relative">
        <div className="max-w-7xl mx-auto px-6">
          <div className="text-center max-w-3xl mx-auto mb-16">
            <h2 className="text-4xl font-bold mb-4">Cómo funciona Pedix</h2>
            <p className="text-xl text-stone-500">Tres simples pasos para digitalizar tus ventas y automatizar tus pedidos por WhatsApp.</p>
          </div>

          <div className="grid md:grid-cols-3 gap-12">
            {[
              {
                step: "1",
                title: "Elige tu plan ideal",
                desc: "Tenemos planes (Principiante, Especialista y Pro) con funcionalidades escalables para cada etapa de tu negocio.",
                icon: <ShoppingBag className="w-8 h-8 text-emerald-500" />
              },
              {
                step: "2",
                title: "Crea tu catálogo",
                desc: "Sube tu logo, arma el catálogo con fotos y configura pagos, envío y cupones de descuento. Todo desde tu celular.",
                icon: <Smartphone className="w-8 h-8 text-blue-500" />
              },
              {
                step: "3",
                title: "Comparte y vende",
                desc: "Comparte tu link en Instagram o WhatsApp. Tus clientes abren, eligen y te hacen el pedido directo sin instalar nada.",
                icon: <TrendingUp className="w-8 h-8 text-purple-500" />
              }
            ].map((item, idx) => (
              <motion.div 
                key={idx}
                initial={{ opacity: 0, y: 30 }}
                whileInView={{ opacity: 1, y: 0 }}
                viewport={{ once: true, margin: "-100px" }}
                transition={{ duration: 0.5, delay: idx * 0.2 }}
                className="relative p-8 rounded-3xl bg-[#faf9f6] border border-stone-100 hover:shadow-xl transition-all duration-300 hover:-translate-y-1"
              >
                <div className="w-16 h-16 bg-white rounded-2xl shadow-sm flex items-center justify-center mb-6">
                  {item.icon}
                </div>
                <div className="absolute top-8 right-8 text-6xl font-black text-stone-100 z-0 select-none">
                  {item.step}
                </div>
                <h3 className="text-2xl font-bold mb-3 relative z-10">{item.title}</h3>
                <p className="text-stone-600 relative z-10 leading-relaxed">{item.desc}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      {/* Features Grid */}
      <section id="caracteristicas" className="py-24 bg-[#faf9f6]">
        <div className="max-w-7xl mx-auto px-6">
          <div className="mb-16">
            <h2 className="text-4xl font-bold mb-4">Todo lo que necesitas para crecer</h2>
            <p className="text-xl text-stone-500 max-w-2xl">Descubre todas las herramientas que te van a ayudar a vender más y mejor, organizadas en una sola plataforma.</p>
          </div>

          <div className="grid md:grid-cols-2 lg:grid-cols-3 gap-6">
            {[
              {
                title: "Gestión de Pedidos",
                desc: "Recepción de pedidos por WhatsApp, centro de pedidos, impresión térmica automática y punto de venta.",
                icon: <Box className="w-6 h-6 text-white" />,
                color: "bg-blue-500"
              },
              {
                title: "Catálogo Digital",
                desc: "Catálogo 24/7, galería de imágenes, variantes de productos y opciones extras personalizables en el checkout.",
                icon: <ShoppingBag className="w-6 h-6 text-white" />,
                color: "bg-emerald-500"
              },
              {
                title: "Ventas y Pagos",
                desc: "Integración con Mercado Pago, Ualá y transferencias. Cupones de descuento y configuración multi-moneda.",
                icon: <CreditCard className="w-6 h-6 text-white" />,
                color: "bg-violet-500"
              },
              {
                title: "Marketing y Análisis",
                desc: "Estadísticas avanzadas, Facebook Pixel, Google Analytics, SEO y campañas en Meta directo desde Pedix.",
                icon: <TrendingUp className="w-6 h-6 text-white" />,
                color: "bg-amber-500"
              },
              {
                title: "Logística Inteligente",
                desc: "Cálculo automático por zona con Google Maps, envíos nacionales e integración con Rapiboy y Rappi.",
                icon: <Box className="w-6 h-6 text-white" />,
                color: "bg-rose-500"
              },
              {
                title: "Gestión de Negocio",
                desc: "Control de stock en tiempo real, gestión de múltiples sucursales, usuarios con roles y edición masiva.",
                icon: <Smartphone className="w-6 h-6 text-white" />,
                color: "bg-cyan-500"
              }
            ].map((feature, idx) => (
              <div key={idx} className="bg-white p-8 rounded-3xl border border-stone-100 hover:border-stone-200 transition-colors shadow-sm">
                <div className={`w-12 h-12 rounded-xl flex items-center justify-center mb-6 shadow-md ${feature.color}`}>
                  {feature.icon}
                </div>
                <h3 className="text-xl font-bold mb-3">{feature.title}</h3>
                <p className="text-stone-600 text-sm leading-relaxed">{feature.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-24 px-6">
        <div className="max-w-5xl mx-auto bg-stone-900 rounded-[40px] p-12 lg:p-20 text-center relative overflow-hidden shadow-2xl">
          <div className="absolute inset-0 bg-[radial-gradient(circle_at_50%_100%,rgba(16,185,129,0.2),transparent_60%)]" />
          
          <div className="relative z-10">
            <h2 className="text-4xl lg:text-5xl font-bold text-white mb-6">
              Disfruta de la tecnología al servicio de tu emprendimiento
            </h2>
            <p className="text-xl text-stone-300 mb-10 max-w-2xl mx-auto">
              Empieza hoy mismo con tus 14 días gratis. Únete a los miles de negocios que ya multiplican sus ventas con Pedix.
            </p>
            <Link to="/login" className="inline-flex bg-emerald-500 text-white px-10 py-5 rounded-full font-bold text-xl items-center justify-center gap-3 hover:bg-emerald-400 transition-all hover:scale-105 shadow-[0_0_40px_rgb(16,185,129,0.4)]">
              Quiero unirme ahora <ArrowRight className="w-6 h-6" />
            </Link>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="bg-white border-t border-stone-100 py-12 px-6">
        <div className="max-w-7xl mx-auto flex flex-col md:flex-row justify-between items-center gap-6">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-black rounded-lg flex items-center justify-center text-white font-bold text-lg">
              P
            </div>
            <span className="font-bold text-xl tracking-tight">Pedix</span>
          </div>
          <div className="flex flex-wrap justify-center gap-6 text-sm font-medium text-stone-500">
            <a href="#" className="hover:text-black">Precios</a>
            <a href="#" className="hover:text-black">Preguntas frecuentes</a>
            <a href="#" className="hover:text-black">Términos y condiciones</a>
            <a href="#" className="hover:text-black">Privacidad</a>
          </div>
          <div className="text-sm text-stone-400">
            © 2021 - 2026 Pedix.
          </div>
        </div>
      </footer>
    </div>
  );
}
