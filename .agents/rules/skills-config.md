---
trigger: always_on
---

# AntyGravity Skills Configuration

Skills son capacidades reutilizables que los agentes aplican automáticamente durante la generación o revisión de código.

| Skill ID              | Trigger / Contexto                              | Reglas de Ejecución                                                                 |
|-----------------------|-------------------------------------------------|-------------------------------------------------------------------------------------|
| `api-consumption`     | Cualquier llamada a `apiClient`                 | - Validar envelope `{ success, data, error, timestamp }`<br>- Mapear `PageResponse` automáticamente<br>- Manejar `401/403/409/500` con patrones UI definidos |
| `multi-tenant`        | Generación de queries, mutaciones o rutas       | - Inyectar `X-Tenant-ID` desde JWT/contexto<br>- Filtrar todas las listas por `tenantId`<br>- Redirigir a selección si `TENANT_CONTEXT_REQUIRED` |
| `state-management`    | Hooks de datos o UI global                      | - Server state → `@tanstack/react-query` (staleTime, cacheTime, retry)<br>- Global UI → `zustand`<br>- Form → `react-hook-form` + `zod`<br>- ❌ Nunca mezclar capas |
| `styling-tokens`      | Componentes UI o páginas                        | - Tailwind utility-first + `twMerge`<br>- Colores solo vía CSS vars (`--color-primary`, etc.)<br>- Responsive mobile-first<br>- WCAG AA contraste mínimo |
| `form-validation`     | Formularios con POST/PUT/PATCH                  | - Zod schema estricto<br>- Server errors → `setError('field', { message })`<br>- `Idempotency-Key` en `POST /pedidos` y `/pagos` |
| `testing-contract`    | Generación de tests o mocks                     | - MSW handlers deben coincidir 1:1 con docs de API<br>- Vitest + RTL<br>- Coverage ≥80% features, ≥90% shared/utils |
| `accessibility-check` | Componentes interactivos o formularios          | - `aria-*` obligatorios<br>- `focus-visible` en inputs/botones<br>- `role="alert"` en errores, `aria-live` en toasts |

**Skill Execution Order:** `api-consumption` → `multi-tenant` → `state-management` → `styling-tokens` → `form-validation` → `testing-contract` → `accessibility-check`