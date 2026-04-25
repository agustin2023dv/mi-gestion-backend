---
trigger: always_on
---

# AntyGravity Workspaces Configuration

Cada workspace representa un Bounded Context alineado al backend. Comparten reglas de estilo, contrato de API y estrategia de estado.

| Workspace       | Scope & Responsabilidad                          | APIs Conectadas                          | Agentes Asignados          |
|-----------------|--------------------------------------------------|------------------------------------------|----------------------------|
| `core-auth`     | Login, register, refresh, logout, tenant resolver, JWT interceptor, routing guards | `/api/v1/auth`                           | Architect, Feature Dev     |
| `catalog`       | Categorías, subcategorías, productos (público + tenant) | `/api/v1/categorias`, `/api/v1/subcategorias`, `/api/v1/productos` | Feature Dev, UI Specialist |
| `orders`        | Carrito, checkout guest/auth, creación con idempotencia, tracking público | `/api/v1/pedidos`                        | Feature Dev, QA Agent      |
| `payments`      | Stripe checkout session, webhook UI feedback, reembolsos admin | `/api/v1/pagos`                          | Feature Dev, QA Agent      |
| `logistics`     | Entregas, asignación repartidor, estados, firma digital, comprobantes | `/api/v1/entregas`, `/api/v1/firmas`, `/api/v1/tarifas-delivery/calcular` | Feature Dev, UI Specialist |
| `finance`       | Gastos, centros de costo, criterios prorrateo, simulador, cost dashboard | `/api/v1/gastos`, `/api/v1/centros-costo`, `/api/v1/simulador`, etc. | Feature Dev, QA Agent      |
| `marketing`     | Cupones, reseñas (producto/tenant), moderación, respuestas | `/api/v1/resenas`, cupones (pendiente API doc) | Feature Dev, UI Specialist |
| `analytics`     | Dashboards ventas/costos, top products/categories, gráficos | Dashboards internos + aggregation endpoints | Feature Dev, QA Agent      |
| `notifications` | Inbox, preferencias, marcado leído, envío admin  | `/api/v1/notificaciones`                 | Feature Dev, UI Specialist |
| `platform`      | SuperAdmin: gestión tenants, planes, métricas globales | Endpoints super-admin (internos)         | Architect, Feature Dev     |
| `shared`        | API client, interceptors, types, hooks, UI kit, utils, theme injector | N/A (cross-cutting)                      | Architect, UI Specialist   |

**Workspace Rules:**
- ❌ No compartir lógica de negocio entre workspaces. Usar `shared/` solo para utilidades puras, UI base y contratos de API.
- ✅ Cada workspace exporta su `index.ts` con la public API del feature.
- ✅ Todos los workspaces deben implementar estados `loading`, `error`, `empty`, `success` según `04-frontend-style-standards.md`.