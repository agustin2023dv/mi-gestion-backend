// Variantes reutilizables para Framer Motion
export const fadeInUp = {
    hidden: { opacity: 0, y: 20 },
    visible: { opacity: 1, y: 0, transition: { duration: 0.4, ease: [0.22, 1, 0.36, 1] } }
};

export const staggerContainer = {
    hidden: { opacity: 0 },
    visible: { opacity: 1, transition: { staggerChildren: 0.1 } }
};

export const hoverLift = (reduced = false) =>
    reduced ? {} : { y: -6, transition: { duration: 0.25, ease: 'easeOut' } };

/*
📌 Tailwind config snippet (agrega a tailwind.config.js si quieres tokens globales):
theme: {
  extend: {
    animation: {
      'fade-in': 'fadeIn 0.4s ease-out',
      'slide-up': 'slideUp 0.5s cubic-bezier(0.22, 1, 0.36, 1)',
    },
    keyframes: {
      fadeIn: { '0%': { opacity: 0 }, '100%': { opacity: 1 } },
      slideUp: { '0%': { opacity: 0, transform: 'translateY(16px)' }, '100%': { opacity: 1, transform: 'translateY(0)' } },
    },
  },
}
*/