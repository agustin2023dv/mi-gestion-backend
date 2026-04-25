## Design & Motion Rationale
- **Staggered reveal**: `staggerChildren: 0.12` crea ritmo sin saturar. Se desactiva con `useReducedMotion()`.
- **Layout stability**: `layout` garantiza transiciones suaves si se filtran/agregan items.
- **Profundidad sin assets**: `backdrop-blur` + gradientes CSS reemplazan imágenes o 3D.
- **Accesibilidad**: `<section>` semántico, `aria-labelledby`, foco navegable, sin autoplay.
- **Performance**: Solo se animan `opacity` y `y`. Evita `width/height/box-shadow` en `whileHover`.

## Cómo usarlo
1. Copia el componente a tu proyecto.
2. Asegúrate de tener instalados: `framer-motion clsx tailwind-merge`
3. Adapta `accent`, tipografía y colores a tu `tailwind.config.js`.
4. Mantén `useReducedMotion()` siempre que uses `y`, `scale` o `stagger`.