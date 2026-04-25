// Merge this into your tailwind.config.js theme.extend section
// Example usage:
// import { getTailwindFontConfig } from './resources/typography-pairs';
// const editorialFonts = getTailwindFontConfig('editorial');

module.exports = {
    theme: {
        extend: {
            fontFamily: {
                // Default fallbacks (override per pair)
                sans: ['system-ui', 'sans-serif'],
                serif: ['Georgia', 'serif'],
                mono: ['JetBrains Mono', 'monospace'],

                // Semantic tokens for display/body (recommended)
                display: ['Manrope', 'system-ui', 'sans-serif'], // ← override with pair
                body: ['Manrope', 'system-ui', 'sans-serif'],    // ← override with pair
            },
            fontSize: {
                // Responsive scale with accessibility in mind
                'display-lg': ['3.5rem', { lineHeight: '1.1', letterSpacing: '-0.02em' }],
                'display-md': ['2.5rem', { lineHeight: '1.2', letterSpacing: '-0.01em' }],
                'display-sm': ['1.75rem', { lineHeight: '1.3' }],
                'body-lg': ['1.125rem', { lineHeight: '1.7' }],
                'body-base': ['1rem', { lineHeight: '1.65' }],
                'body-sm': ['0.875rem', { lineHeight: '1.6' }],
            },
            fontWeight: {
                // Semantic weights for consistency
                light: '300',
                regular: '400',
                medium: '500',
                semibold: '600',
                bold: '700',
                display: '800', // for accent headlines
            },
        },
    },
    // Optional: plugin for automatic font preconnect
    plugins: [
        function ({ addBase }) {
            addBase({
                'link[rel="preconnect"]': { crossorigin: 'anonymous' },
            });
        },
    ],
};