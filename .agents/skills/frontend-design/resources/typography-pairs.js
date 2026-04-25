/**
 * Distinctive font pairings for React + Tailwind projects
 * Avoids generic stacks (Inter, Roboto, system-ui) unless explicitly requested.
 * Each pair includes: display + body, aesthetic tone, import instructions, and accessibility notes.
 */

export const typographyPairs = {
    // ─────────────────────────────────────────────────────────────
    // EDITORIAL / MAGAZINE
    // ─────────────────────────────────────────────────────────────
    editorial: {
        name: 'Editorial Elegance',
        display: {
            family: 'Playfair Display',
            weights: [400, 600, 700],
            style: 'italic optional',
            googleFonts: 'Playfair+Display:ital,wght@0,400;0,600;0,700;1,400',
        },
        body: {
            family: 'Source Sans 3',
            weights: [300, 400, 600],
            googleFonts: 'Source+Sans+3:wght@300;400;600',
        },
        tone: 'refined, literary, high-contrast',
        bestFor: ['blogs', 'portfolios', 'landing pages with long-form content'],
        a11y: {
            minDisplaySize: '2rem',
            minBodySize: '1rem',
            lineHeight: '1.6–1.8 for body',
            note: 'Playfair italic has low x-height; avoid for UI labels or small text.',
        },
    },

    // ─────────────────────────────────────────────────────────────
    // RETRO-FUTURISTIC / TECH
    // ─────────────────────────────────────────────────────────────
    retroTech: {
        name: 'Retro-Futuristic',
        display: {
            family: 'Space Grotesk',
            weights: [300, 500, 700],
            googleFonts: 'Space+Grotesk:wght@300;500;700',
        },
        body: {
            family: 'JetBrains Mono',
            weights: [400, 500],
            googleFonts: 'JetBrains+Mono:wght@400;500',
        },
        tone: 'technical, playful, slightly nostalgic',
        bestFor: ['dashboards', 'dev tools', 'SaaS products with personality'],
        a11y: {
            minDisplaySize: '1.75rem',
            minBodySize: '0.95rem',
            lineHeight: '1.7 for monospace body',
            note: 'Monospace body reduces scan speed; reserve for code-heavy interfaces or short sections.',
        },
    },

    // ─────────────────────────────────────────────────────────────
    // SOFT / ORGANIC / CALM
    // ─────────────────────────────────────────────────────────────
    softOrganic: {
        name: 'Soft & Natural',
        display: {
            family: 'Cormorant Garamond',
            weights: [400, 500, 600],
            style: 'italic recommended',
            googleFonts: 'Cormorant+Garamond:ital,wght@0,400;0,500;0,600;1,400;1,500',
        },
        body: {
            family: 'Lato',
            weights: [300, 400, 700],
            googleFonts: 'Lato:wght@300;400;700',
        },
        tone: 'warm, approachable, human-centered',
        bestFor: ['wellness apps', 'creative studios', 'e-commerce with artisanal positioning'],
        a11y: {
            minDisplaySize: '2.25rem',
            minBodySize: '1rem',
            lineHeight: '1.75 for body',
            note: 'Cormorant has delicate strokes; ensure sufficient contrast and avoid thin weights on low-DPI screens.',
        },
    },

    // ─────────────────────────────────────────────────────────────
    // BRUTALIST / RAW / INDUSTRIAL
    // ─────────────────────────────────────────────────────────────
    brutalist: {
        name: 'Brutalist Raw',
        display: {
            family: 'Syne',
            weights: [400, 700, 800],
            googleFonts: 'Syne:wght@400;700;800',
        },
        body: {
            family: 'IBM Plex Sans',
            weights: [300, 400, 500],
            googleFonts: 'IBM+Plex+Sans:wght@300;400;500',
        },
        tone: 'bold, unconventional, high-impact',
        bestFor: ['art portfolios', 'experimental products', 'brands that break conventions'],
        a11y: {
            minDisplaySize: '2.5rem',
            minBodySize: '1rem',
            lineHeight: '1.6 for body',
            note: 'Syne ExtraBold can overwhelm; pair with generous whitespace and avoid all-caps body text.',
        },
    },

    // ─────────────────────────────────────────────────────────────
    // LUXURY / MINIMAL / REFINED
    // ─────────────────────────────────────────────────────────────
    luxuryMinimal: {
        name: 'Refined Minimal',
        display: {
            family: 'Manrope',
            weights: [400, 600, 800],
            googleFonts: 'Manrope:wght@400;600;800',
        },
        body: {
            family: 'Manrope',
            weights: [300, 400, 500],
            googleFonts: 'Manrope:wght@300;400;500',
            note: 'Same family, different weights for cohesion',
        },
        tone: 'quiet confidence, precision, timeless',
        bestFor: ['fintech', 'premium SaaS', 'architectural/real estate'],
        a11y: {
            minDisplaySize: '2rem',
            minBodySize: '1rem',
            lineHeight: '1.65 for body',
            note: 'Monochromatic typography requires strong contrast hierarchy via weight/size, not color alone.',
        },
    },

    // ─────────────────────────────────────────────────────────────
    // PLAYFUL / TOY-LIKE / FRIENDLY
    // ─────────────────────────────────────────────────────────────
    playful: {
        name: 'Playful & Friendly',
        display: {
            family: 'Fredoka',
            weights: [400, 500, 600],
            googleFonts: 'Fredoka:wght@400;500;600',
        },
        body: {
            family: 'Nunito',
            weights: [300, 400, 700],
            googleFonts: 'Nunito:wght@300;400;700',
        },
        tone: 'approachable, energetic, youthful',
        bestFor: ['education', 'kids apps', 'community platforms'],
        a11y: {
            minDisplaySize: '1.875rem',
            minBodySize: '1rem',
            lineHeight: '1.7 for body',
            note: 'Rounded terminals improve legibility at small sizes; avoid using Fredoka for body text.',
        },
    },
};

/**
 * Helper: Generate Google Fonts <link> tag
 * Usage: <link rel="preload" href={getFontPreloadUrl(pair.display)} as="style" />
 */
export function getFontPreloadUrl(fontConfig) {
    const { family, weights, style } = fontConfig;
    const weightsParam = weights.join(';');
    const styleParam = style?.includes('italic') ? ':ital@0;1' : '';
    return `https://fonts.googleapis.com/css2?family=${family.replace(/ /g, '+')}:wght${styleParam}@${weightsParam}&display=swap`;
}

/**
 * Helper: Generate Tailwind font-family config snippet
 * Usage: Spread into theme.extend.fontFamily
 */
export function getTailwindFontConfig(pairKey) {
    const pair = typographyPairs[pairKey];
    if (!pair) return {};
    return {
        display: [`"${pair.display.family}"`, 'system-ui', 'sans-serif'],
        body: [`"${pair.body.family}"`, 'system-ui', 'sans-serif'],
    };
}