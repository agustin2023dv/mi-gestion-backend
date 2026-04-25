---
name: frontend-design
description: Create distinctive, production-grade React interfaces using Tailwind CSS and Framer Motion. Focus on intentional aesthetics, avoiding generic AI patterns.
tags: ["react", "tailwind", "framer-motion", "ui", "design", "creative-coding"]
---

# Frontend Design Skill (React + Tailwind + Framer Motion)

This skill guides the creation of distinctive, production-grade React components and pages. Prioritize intentional visual design, smooth motion, and clean architecture. Avoid generic "AI template" aesthetics and over-engineered animations.

The user provides requirements: a component, page, dashboard, or interface. They may include context about purpose, audience, performance, or brand constraints.

## 🎨 Design Thinking

Before coding, commit to a BOLD aesthetic direction:
- **Purpose**: What problem does this interface solve? Who interacts with it?
- **Tone**: Choose a clear visual language: brutally minimal, editorial/magazine, retro-futuristic, luxury/refined, playful/toy-like, industrial/utilitarian, soft/organic, etc. Execute it with precision.
- **Constraints**: React architecture, Tailwind configuration, bundle size, accessibility, performance budgets.
- **Differentiation**: What's the single visual or interaction detail that will make this unforgettable?

**CRITICAL**: Intentionality > intensity. Both maximalism and restraint work if executed consistently. Never default to safe, template-like layouts.

## 🛠️ Stack-Specific Implementation Rules

### ⚛️ React Architecture
- Keep components focused: UI rendering, logic, and animation separation where it improves readability.
- Use hooks appropriately (`useState`, `useMemo`, `useCallback` only when needed). Avoid premature optimization.
- Semantic HTML first: `<section>`, `<article>`, `<nav>`, proper heading hierarchy.
- Ensure keyboard navigation and focus management, especially around animated/conditional elements.

### 🎨 Tailwind CSS
- Extend tokens in `tailwind.config.js` for colors, fonts, spacing, and breakpoints. Avoid scattering arbitrary values (`[23px]`, `[#ff00ff]`) unless strictly justified.
- Use `clsx` + `tailwind-merge` for conditional/dynamic class composition.
- Prefer utility composition over `@apply`. Keep inline `<style>` or `css` imports out unless absolutely necessary.
- Respect responsive design: mobile-first, test at `sm`, `md`, `lg`, `xl`. Use `container` or custom max-widths intentionally.

### 🌀 Framer Motion
- Prefer declarative animations: `motion.div`, `initial`, `animate`, `exit`, `transition`.
- Use `layout` prop for smooth DOM reflows (lists, filters, state toggles). Set `layoutRoot` when animating complex hierarchies.
- Optimize performance: animate `opacity`, `transform`, `scale`, `x/y`. Avoid animating `width`, `height`, `margin`, `box-shadow`.
- Use `useScroll`, `useTransform`, or `useAnimate` only when scroll-linked or sequenced motion is core to the UX.
- Respect accessibility: disable reduced-motion preferences with `useReducedMotion()`, provide `aria-live` for dynamic content.

## 📐 Visual & Interaction Guidelines

- **Typography**: Pair a distinctive display font with a highly readable body font. Use `next/font` or `@font-face` responsibly. Avoid Inter, Roboto, system-ui, or default sans/serif stacks unless brand-specified.
- **Color & Theme**: Commit to a cohesive palette. Use CSS variables or Tailwind tokens. Dominant base + sharp accent outperform muted, evenly-distributed schemes. Handle dark/light mode intentionally.
- **Motion Strategy**: One well-orchestrated entrance or transition > scattered micro-interactions. Use staggered delays (`transition: { staggerChildren, delayChildren }`), hover/tap states that feel physical, and scroll-triggered reveals that guide attention.
- **Spatial Composition**: Break predictable grids. Use asymmetry, overlap, diagonal flow, generous negative space, or controlled density. Let content breathe.
- **Depth & Atmosphere**: Create dimension without Three.js. Use layered transparencies, subtle noise/grain (`bg-[url('data:image/svg+xml,...')]`), gradient meshes, dramatic shadows, glassmorphism (`backdrop-blur`, `bg-white/10`), or geometric SVG overlays. Match to the chosen tone.

**🚫 NEVER output generic AI patterns**:
- Purple/blue gradients on white backgrounds
- Inter/Roboto/system fonts without justification
- Card-grid defaults with identical padding, rounded corners, and drop shadows
- Over-animated UIs that cause layout shift, jank, or accessibility violations
- Placeholder comments, `// TODO`, or incomplete prop typing

Match implementation complexity to the vision. Maximalism needs precise animation choreography and performance tuning. Minimalism needs flawless spacing, typographic hierarchy, and subtle interaction cues.

## 📤 Output Expectations

- Provide complete, runnable React component(s) with clear file boundaries.
- Include all necessary imports (`framer-motion`, `tailwind-merge`, `clsx`, font setups).
- Use TypeScript if the project expects it; otherwise, keep JSDoc or PropTypes minimal.
- Add brief comments only for non-obvious animation sequences or layout tricks.
- Ensure responsive behavior, keyboard accessibility, and `prefers-reduced-motion` support.
- No placeholders, no "add your own styles here", no pseudo-code.

## 📦 Reference Examples & Resources
- `examples/feature-grid/index.tsx`: Staggered entrance, `layout` transitions, responsive grid, `useReducedMotion` compliance.
- `resources/motion-presets.js`: Reusable variants + Tailwind animation tokens.
- **Usage rule**: Treat these as structural baselines. Adapt aesthetic direction, but preserve motion/accessibility patterns. Load files only when generating complex lists, grids, or staggered sequences.

Remember: Modern AI code assistants can produce exceptional, context-aware UIs. Commit fully to a distinctive vision, respect performance and accessibility, and deliver production-ready code.