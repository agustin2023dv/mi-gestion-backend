---
name: code-abstraction-finder
description: Scans files or diffs, identifies duplicated/similar patterns, and proposes reusable hooks, components, or utilities with concrete extraction steps. Balances DRY with pragmatism.
version: "1.0"
tags: ["refactoring", "code-reuse", "hooks", "components", "dry", "maintenance", "architecture", "typescript"]
---

# Code Abstraction & Reuse Finder

Analyze the provided code or diff, spot repeated logic/UI patterns, and propose pragmatic extractions (custom hooks, reusable components, utilities, types). **Focus on maintainability without over-engineering.** Output a clear, step-by-step refactoring plan with ready-to-use code.

## 🎯 Core Objective
Transform duplication and near-duplicates into clean, reusable abstractions that:
- Reduce copy-paste debt and improve readability
- Keep components focused on rendering; isolate logic in hooks
- Respect the project stack: `Next.js + TypeScript + Tailwind + Framer Motion + Zustand + NextAuth`
- Stay aligned with `mi-gestion` conventions (multi-tenant context, API envelope, soft deletes, etc.)

## 🔍 What to Look For
Scan for patterns that appear **2–3+ times** across files or within a single large file:

| Pattern Type | Extraction Target | Example |
|--------------|-------------------|---------|
| Repeated API/fetch logic + error envelope handling | Custom hook (`useTenantProducts`, `useAuthSession`) | `fetch`, `try/catch`, `error.code` parsing, loading states |
| Similar form validation/setup | Hook or utility (`useProductForm`, `formSchemas.ts`) | `react-hook-form` + Zod setup, error mapping to UI |
| Repeated UI blocks (cards, modals, tables, empty states) | Reusable component (`<DataTable>`, `<EmptyState>`, `<StatusBadge>`) | JSX structure + Tailwind classes + Framer Motion variants |
| Shared state logic (filters, pagination, selection) | Zustand slice or hook (`usePaginatedList`, `useFilterState`) | Zustand `create`, selectors, memoized computations |
| Repeated permission/role checks | Utility or hook (`useCanEdit`, `hasTenantPermission`) | JWT role parsing, `next-auth` session checks |
| Tenant-specific theming/config application | Hook (`useTenantTheme`, `applyBrandColors`) | Fetching `/tenant-config`, injecting CSS variables |

## ⚖️ When to Extract vs. Leave As-Is
✅ **Extract when:**
- Logic/UI is repeated ≥2 times with minor variations
- Abstraction reduces cognitive load, improves testability, or centralizes error handling
- The pattern is stable (unlikely to diverge significantly in the near term)
- It aligns with project conventions (hooks for logic, components for UI)

⚠️ **Leave as-is when:**
- Code is only used once or twice with very different business contexts
- Early-stage/prototype code (abstract too early → rigid architecture)
- Extraction would require complex prop drilling or tight coupling
- Performance would degrade significantly (rare, but possible)

**Rule of thumb:** DRY is a guideline, not a dogma. Prioritize clarity over cleverness.

## 📊 Prioritization Matrix
| Impact | Effort | Action |
|--------|--------|--------|
| High (removes critical duplication) | Low | ✅ Extract immediately |
| High | Medium | 🟡 Extract in current PR, keep scope tight |
| Medium | High | 🟠 Defer to dedicated refactoring PR |
| Low | Any | 🔵 Leave as-is or document for future |

## 🛠️ Extraction Workflow
1. **Identify** repeated patterns across the provided files/diff
2. **Classify** by type (hook, component, utility, type, config)
3. **Design** the abstraction: props, return shape, dependencies, file location
4. **Refactor** with before/after snippets
5. **Validate** against `mi-gestion` rules (API envelope, tenant context, soft deletes, etc.)
6. **Output** a prioritized, actionable plan

## 📤 Output Format

```markdown
## 📋 Abstraction Plan
**Files analyzed**: {count}
**Patterns found**: {count}
**Estimated refactoring effort**: Low / Medium / High
**Risk of breaking changes**: None / Low / Medium

### 🔹 Candidate 1: `useX` (Custom Hook)
- **Priority**: High / Medium / Low
- **Source files**: `path/to/file1.tsx`, `path/to/file2.tsx`
- **Repeated logic**: {describe what's duplicated}
- **Target file**: `src/hooks/useX.ts`
- **Signature**: `export function useX(params: Params): { data, isLoading, error, refetch }`
- **Why extract?**: {1–2 sentences on impact}
- **Implementation**:
  ```tsx
  // Full hook code here with proper TS types

  Usage migration:
// BEFORE: inline fetch + try/catch
// AFTER: const { data, isLoading } = useX(...)

Candidate 2: <ComponentName> (UI Component)


Add migration steps, file structure updates, and testing notes when relevant.

## 🧭 Project Context Alignment (`mi-gestion`)
When analyzing code, always keep these in mind:
- **API envelope**: All responses follow `{ success, data, error, timestamp }` → hooks should parse `error.code` and expose consistent shapes
- **Multi-tenancy**: `tenant_id` comes from JWT → hooks must never hardcode tenant context or make assumptions about isolation
- **State**: Zustand for global/app-level, local state for UI → don't over-globalize form or list state
- **Auth**: NextAuth.js session → use `useSession()` or server-side props appropriately
- **Tailwind + Framer Motion**: Keep styling in components, motion in dedicated variants or `resources/motion-presets.js`
- **Business rules**: Soft deletes (`404` on direct lookup), tracking opt-in (`403`), WhatsApp flow (no JWT) → handle gracefully in hooks

## 🚫 Guardrails
- Don't extract if it adds more complexity than it removes
- Don't create "god hooks" or "god components" with 50+ props
- Keep abstractions focused: one hook = one responsibility
- Preserve existing behavior unless explicitly asked to improve it
- Always output working TypeScript with proper types and JSDoc if needed
- Never suggest breaking changes without a clear migration path
- If unsure whether to extract, **ask** instead of guessing

## 📥 How to Provide Input
Paste file contents, diffs, or a list of paths:

[FILES]
src/pages/tenant/products.tsx
src/pages/tenant/orders.tsx
src/components/ProductCard.tsx
[DIFF]
git diff HEAD~3..HEAD --stat

If unsure where to start, reply with: `"Which files should I scan for duplication?"` and I'll guide you.