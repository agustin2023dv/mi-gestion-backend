import { useState } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import { 
  Palette, 
  Store, 
  Globe, 
  MessageCircle, 
  Upload,
  Check
} from 'lucide-react';
import { Button } from '../../../shared/components/ui/button';
import { Input } from '../../../shared/components/ui/input';
import { PageHeader } from '../../../shared/components/ui/page-header';
import { Section } from '../../../shared/components/ui/section';
import { useFeedback } from '../../../shared/hooks/use-feedback';
import { useDesign } from '../../../shared/contexts/design-context';
import { cn } from '../../../shared/utils/cn';

export default function TenantSettings() {
  const { settings, updateSettings } = useDesign();
  const [primaryColor, setPrimaryColor] = useState(settings.primaryColor);
  const [secondaryColor, setSecondaryColor] = useState(settings.backgroundColor);
  const { isActive: isSaved, trigger: triggerSaved } = useFeedback();

  const handleSave = () => {
    updateSettings({
      primaryColor,
      backgroundColor: secondaryColor
    });
    triggerSaved();
  };

  return (
    <div className="max-w-4xl space-y-12 pb-20">
      <PageHeader 
        title="Configuración" 
        description="Define la identidad de tu marca y canales de venta."
      >
        <Button onClick={handleSave} className="md:w-auto min-w-[160px]">
          <AnimatePresence mode="wait">
            {isSaved ? (
              <motion.div
                key="saved"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -10 }}
                className="flex items-center"
              >
                <Check className="w-4 h-4 mr-2" /> Guardado
              </motion.div>
            ) : (
              <motion.div
                key="save"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                exit={{ opacity: 0 }}
              >
                Guardar Cambios
              </motion.div>
            )}
          </AnimatePresence>
        </Button>
      </PageHeader>

      <div className="grid grid-cols-1 gap-12">
        {/* Identidad Visual */}
        <Section title="Identidad Visual" icon={Palette}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
            {/* Logo Upload */}
            <div className="space-y-4">
              <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Logo de la Tienda</label>
              <div className="relative group cursor-pointer aspect-square max-w-[200px] border-2 border-dashed border-stone-200 flex flex-col items-center justify-center space-y-2 hover:border-stone-900 transition-colors">
                <Upload className="w-8 h-8 text-stone-300 group-hover:text-stone-900 transition-colors" />
                <span className="text-[10px] font-bold tracking-widest uppercase text-stone-400">Subir Logo</span>
              </div>
              <p className="text-[10px] text-stone-400 font-medium italic">Recomendado: PNG fondo transparente, min 500x500px.</p>
            </div>

            {/* Color Palette */}
            <div className="space-y-6">
              <div className="space-y-4">
                <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Color Primario</label>
                <div className="flex items-center space-x-4">
                  <input 
                    type="color" 
                    value={primaryColor} 
                    onChange={(e) => setPrimaryColor(e.target.value)}
                    className="w-12 h-12 rounded-none border-none cursor-pointer" 
                  />
                  <code className="text-sm font-bold text-stone-900 uppercase tracking-widest">{primaryColor}</code>
                </div>
              </div>
              <div className="space-y-4">
                <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Color de Fondo</label>
                <div className="flex items-center space-x-4">
                  <input 
                    type="color" 
                    value={secondaryColor} 
                    onChange={(e) => setSecondaryColor(e.target.value)}
                    className="w-12 h-12 rounded-none border border-stone-200 cursor-pointer" 
                  />
                  <code className="text-sm font-bold text-stone-900 uppercase tracking-widest">{secondaryColor}</code>
                </div>
              </div>
            </div>
          </div>
        </Section>

        {/* Información General */}
        <Section title="Información de la Tienda" icon={Store}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <Input label="Nombre de la Tienda" defaultValue="" placeholder="Nombre de tu tienda" />
            <Input label="Subdominio" defaultValue="" suffix=".mabizz.com" disabled placeholder="tu-tienda" />
            <div className="md:col-span-2">
              <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500 mb-2 block">Eslogan de Marca</label>
              <textarea 
                className="w-full bg-transparent border-b border-stone-300 py-2 focus:outline-none focus:border-stone-900 transition-colors rounded-none text-base font-medium resize-none"
                placeholder="Describe tu marca en una frase..."
              />
            </div>
          </div>
        </Section>

        {/* Canales de Venta */}
        <Section title="Canales y Redes" icon={Globe}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
            <div className="space-y-1">
              <div className="flex items-center space-x-2 mb-1">
                <MessageCircle className="w-3 h-3 text-green-600" />
                <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500">WhatsApp para Ventas</label>
              </div>
              <Input label="" placeholder="+54 9 11 ..." />
            </div>
            <div className="space-y-1">
              <div className="flex items-center space-x-2 mb-1">
                <Globe className="w-3 h-3 text-pink-600" />
                <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-500">Instagram / Redes</label>
              </div>
              <Input label="" placeholder="@usuario" />
            </div>
          </div>
        </Section>

        {/* Personalización del Menú Público */}
        <Section title="Diseño del Menú Público" icon={Palette}>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-12">
            {/* Tipografía y Layout */}
            <div className="space-y-8">
              <div className="space-y-4">
                <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Tipografía</label>
                <div className="flex gap-4">
                  {(['sans', 'serif', 'mono'] as const).map((font) => (
                    <button
                      key={font}
                      onClick={() => updateSettings({ fontFamily: font })}
                      className={cn(
                        "flex-1 py-3 px-4 border text-xs font-bold uppercase tracking-widest transition-all",
                        settings.fontFamily === font 
                          ? "border-stone-900 bg-stone-900 text-white shadow-lg" 
                          : "border-stone-200 text-stone-400 hover:border-stone-400"
                      )}
                    >
                      {font}
                    </button>
                  ))}
                </div>
              </div>

              <div className="space-y-4">
                <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Productos por fila (Desktop)</label>
                <div className="flex gap-4">
                  {([1, 2, 3] as const).map((cols) => (
                    <button
                      key={cols}
                      onClick={() => updateSettings({ gridColumns: cols })}
                      className={cn(
                        "flex-1 py-3 px-4 border text-xs font-bold transition-all",
                        settings.gridColumns === cols 
                          ? "border-stone-900 bg-stone-900 text-white shadow-lg" 
                          : "border-stone-200 text-stone-400 hover:border-stone-400"
                      )}
                    >
                      {cols} {cols === 1 ? 'Columna' : 'Columnas'}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            {/* Estilo de Tarjetas y Opciones */}
            <div className="space-y-8">
              <div className="space-y-4">
                <label className="text-[10px] font-bold tracking-[0.2em] uppercase text-stone-400">Estilo de Tarjetas</label>
                <div className="flex gap-4">
                  {(['minimal', 'glass', 'bordered'] as const).map((style) => (
                    <button
                      key={style}
                      onClick={() => updateSettings({ cardStyle: style })}
                      className={cn(
                        "flex-1 py-3 px-4 border text-[10px] font-bold uppercase tracking-tighter transition-all",
                        settings.cardStyle === style 
                          ? "border-stone-900 bg-stone-900 text-white shadow-lg" 
                          : "border-stone-200 text-stone-400 hover:border-stone-400"
                      )}
                    >
                      {style}
                    </button>
                  ))}
                </div>
              </div>

              <div className="flex items-center justify-between p-4 bg-stone-50 rounded-xl border border-stone-100">
                <div className="space-y-1">
                  <p className="text-xs font-bold text-stone-900 uppercase tracking-tight">Mostrar Categorías</p>
                  <p className="text-[10px] text-stone-500 font-medium">Agrupa tus productos por categorías en el menú.</p>
                </div>
                <button 
                  onClick={() => updateSettings({ showCategories: !settings.showCategories })}
                  className={cn(
                    "w-12 h-6 rounded-full transition-colors relative flex items-center px-1",
                    settings.showCategories ? "bg-stone-900" : "bg-stone-200"
                  )}
                >
                  <motion.div 
                    animate={{ x: settings.showCategories ? 24 : 0 }}
                    className="w-4 h-4 bg-white rounded-full shadow-sm"
                  />
                </button>
              </div>
            </div>
          </div>
        </Section>
      </div>
    </div>
  );
}
