import { motion, useReducedMotion } from 'framer-motion';
import { clsx, type ClassValue } from 'clsx';
import { twMerge } from 'tailwind-merge';

function cn(...inputs: ClassValue[]) {
    return twMerge(clsx(inputs));
}

interface CardData {
    id: string;
    title: string;
    description: string;
    accent: 'primary' | 'secondary' | 'neutral';
}

const cards: CardData[] = [
    { id: '1', title: 'Intentional Motion', description: 'Every transition serves a purpose. No jank, no distraction.', accent: 'primary' },
    { id: '2', title: 'Spatial Clarity', description: 'Breathing room, asymmetric balance, and deliberate hierarchy.', accent: 'secondary' },
    { id: '3', title: 'Accessible by Default', description: 'Respects reduced motion, keyboard nav, and semantic structure.', accent: 'neutral' },
];

export default function FeatureGrid() {
    const prefersReducedMotion = useReducedMotion();

    return (
        <section className="mx-auto max-w-5xl px-6 py-16 sm:py-24" aria-labelledby="features-heading">
            <h2 id="features-heading" className="mb-12 text-center text-3xl font-bold tracking-tight text-slate-900 sm:text-4xl">
                Designed with intention
            </h2>

            <motion.div
                className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3"
                initial={{ opacity: 0 }}
                animate={{ opacity: 1 }}
                transition={{ staggerChildren: prefersReducedMotion ? 0 : 0.12 }}
            >
                {cards.map((card) => (
                    <motion.div
                        key={card.id}
                        layout
                        initial={{ opacity: 0, y: 24 }}
                        animate={{ opacity: 1, y: 0 }}
                        whileHover={prefersReducedMotion ? {} : { y: -8, transition: { duration: 0.3, ease: 'easeOut' } }}
                        className={cn(
                            'group relative overflow-hidden rounded-2xl border border-slate-200 bg-white/70 p-6 backdrop-blur-sm',
                            'hover:border-transparent hover:shadow-xl hover:shadow-slate-200/50'
                        )}
                    >
                        <div className="pointer-events-none absolute inset-0 bg-gradient-to-br from-white/40 via-transparent to-slate-100/30" />
                        <div className="relative z-10">
                            <h3 className="mb-2 text-lg font-semibold tracking-tight text-slate-900">{card.title}</h3>
                            <p className="leading-relaxed text-slate-600">{card.description}</p>
                        </div>
                    </motion.div>
                ))}
            </motion.div>
        </section>
    );
}