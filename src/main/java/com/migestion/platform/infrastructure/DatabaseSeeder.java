package com.migestion.platform.infrastructure;

import com.migestion.catalog.domain.Categoria;
import com.migestion.catalog.domain.CategoriaRepository;
import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.domain.ProductoRepository;
import com.migestion.catalog.domain.Subcategoria;
import com.migestion.catalog.domain.SubcategoriaRepository;
import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.orders.domain.EstadoPedido;
import com.migestion.orders.domain.EstadoPedidoRepository;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.platform.domain.PlanSuscripcion;
import com.migestion.platform.domain.PlanSuscripcionRepository;
import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.tenant.domain.Cliente;
import com.migestion.tenant.domain.ClienteRepository;
import com.migestion.tenant.domain.Direccion;
import com.migestion.tenant.domain.DireccionRepository;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Local-only database seeder. Creates deterministic, relationship-consistent mock data.
 *
 * <p>Behaviour:
 * <ul>
 *   <li>Normal start → seeds once, skips on subsequent restarts (idempotent by unique email/slug).</li>
 *   <li>{@code --seed.reset=true} → wipes all seeded tables, resets sequences, then re-seeds.</li>
 * </ul>
 *
 * <p>Insert order (FK-safe):
 * PlanSuscripcion → SuperAdmin → Categoria → Subcategoria → Tenant → UsuarioTenant →
 * Cliente → Direccion → Producto → EstadoPedido → Pedido → PedidoItem → Entrega
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile("local")
public class DatabaseSeeder implements CommandLineRunner {

    // ── Repositories ──
    private final SuperAdminRepository superAdminRepository;
    private final PlanSuscripcionRepository planSuscripcionRepository;
    private final TenantRepository tenantRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final ClienteRepository clienteRepository;
    private final DireccionRepository direccionRepository;
    private final CategoriaRepository categoriaRepository;
    private final SubcategoriaRepository subcategoriaRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final ProductoRepository productoRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final EntregaRepository entregaRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager entityManager;

    @Value("${seed.reset:false}")
    private boolean seedReset;

    // ── Deterministic constants ──
    private static final String SUPER_ADMIN_EMAIL = "superadmin@mabizz.com";
    private static final String DEFAULT_PASSWORD = "Pass@12345";

    // ════════════════════════════════════════════════════════════════
    // ENTRY POINT
    // ════════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public void run(String... args) {
        log.info("══════ DATABASE SEEDER [local] ══════");

        try {
            if (seedReset) {
                log.warn("🗑️  seed.reset=true → wiping all seeded data…");
                wipeAllSeededData();
                log.info("🗑️  Wipe complete.");
            }

            if (alreadySeeded()) {
                log.info("⏭️  Seed data already present — skipping.");
                return;
            }

            log.info("🌱 Seeding…");
            seed();
            log.info("✅ DATABASE SEED COMPLETE");

        } catch (Exception e) {
            log.error("❌ Seed failed: {}", e.getMessage(), e);
            throw new RuntimeException("Database seed failed", e);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // IDEMPOTENCY GUARD — checks unique business keys, not row count
    // ════════════════════════════════════════════════════════════════

    private boolean alreadySeeded() {
        return superAdminRepository.findByEmailIgnoreCase(SUPER_ADMIN_EMAIL).isPresent()
                && tenantRepository.findBySlug("pizzeria-del-sol").isPresent();
    }

    // ════════════════════════════════════════════════════════════════
    // WIPE (reverse FK order) — raw SQL required for TRUNCATE CASCADE
    // ════════════════════════════════════════════════════════════════

    private void wipeAllSeededData() {
        entityManager.flush();

        // Truncate in reverse-dependency order with CASCADE
        List<String> tables = List.of(
                "entrega",
                "pedidoitem",
                "pedido",
                "producto",
                "subcategoria",
                "categoria",
                "direccion",
                "cliente",
                "\"UsuarioTenant\"",
                "tenant",
                "super_admin",
                "plan_suscripcion",
                "estadopedido"
        );

        for (String table : tables) {
            entityManager.createNativeQuery(
                    "TRUNCATE TABLE " + table + " RESTART IDENTITY CASCADE"
            ).executeUpdate();
            log.debug("  truncated: {}", table);
        }

        entityManager.flush();
        entityManager.clear();
        log.info("🗑️  All seeded tables truncated, sequences reset.");
    }

    // ════════════════════════════════════════════════════════════════
    // SEED ORCHESTRATOR — strict FK insert order
    // ════════════════════════════════════════════════════════════════

    private void seed() {
        // 1. PlanSuscripcion (no FK deps)
        log.info("  1/9  PlanSuscripcion…");
        PlanSuscripcion planBasico = seedPlanSuscripcion("Básico", 50, 200, 500,
                new BigDecimal("9.99"), 1, List.of("catalog", "orders"));
        PlanSuscripcion planPro = seedPlanSuscripcion("Profesional", 200, 1000, 2000,
                new BigDecimal("29.99"), 2, List.of("catalog", "orders", "analytics", "marketing"));

        // 2. SuperAdmin (no FK deps)
        log.info("  2/9  SuperAdmin…");
        seedSuperAdmin();

        // 3. Categorias + Subcategorias (no tenant FK)
        log.info("  3/9  Categorías y Subcategorías…");
        List<Subcategoria> subcategorias = seedCatalogStructure();

        // 4. EstadoPedido (no FK deps)
        log.info("  4/9  EstadoPedido…");
        List<EstadoPedido> estados = seedEstadosPedido();

        // 5. Tenants → UsuarioTenant (FK: planSuscripcionId)
        log.info("  5/9  Tenants + Propietarios…");
        List<TenantBundle> tenantBundles = seedTenants(planBasico, planPro);

        // 6. Clientes + Direcciones (FK: tenantId)
        log.info("  6/9  Clientes + Direcciones…");
        for (TenantBundle tb : tenantBundles) {
            tb.clientes = seedClientes(tb.tenant);
            tb.direcciones = seedDirecciones(tb.clientes);
        }

        // 7. Productos (FK: tenantId, subcategoriaId)
        log.info("  7/9  Productos…");
        for (TenantBundle tb : tenantBundles) {
            tb.productos = seedProductos(tb.tenant, subcategorias);
        }

        // 8. Pedidos + PedidoItems (FK: tenantId, clienteId, estadoId, productoId)
        log.info("  8/9  Pedidos + Items…");
        for (TenantBundle tb : tenantBundles) {
            tb.pedidos = seedPedidos(tb.tenant, tb.clientes, tb.direcciones, estados, tb.productos);
        }

        // 9. Entregas (FK: tenantId, pedidoId)
        log.info("  9/9  Entregas…");
        for (TenantBundle tb : tenantBundles) {
            seedEntregas(tb.tenant, tb.pedidos);
        }
    }

    // ════════════════════════════════════════════════════════════════
    // INDIVIDUAL SEEDERS
    // ════════════════════════════════════════════════════════════════

    private PlanSuscripcion seedPlanSuscripcion(String nombre, int maxProd, int maxPed,
            int maxMb, BigDecimal precio, int order, List<String> features) {
        return planSuscripcionRepository.findByNombreIgnoreCase(nombre)
                .orElseGet(() -> planSuscripcionRepository.save(PlanSuscripcion.builder()
                        .nombre(nombre)
                        .maxProductos(maxProd)
                        .maxPedidosMensuales(maxPed)
                        .maxAlmacenamientoMb(maxMb)
                        .precioMensual(precio)
                        .orderLevel(order)
                        .features(features)
                        .build()));
    }

    private void seedSuperAdmin() {
        if (superAdminRepository.findByEmailIgnoreCase(SUPER_ADMIN_EMAIL).isPresent()) return;
        superAdminRepository.save(SuperAdmin.builder()
                .email(SUPER_ADMIN_EMAIL)
                .nombre("Admin")
                .apellido("Sistema")
                .passwordHash(passwordEncoder.encode("Admin@12345"))
                .isActive(true)
                .build());
    }

    private List<Subcategoria> seedCatalogStructure() {
        List<Subcategoria> allSubs = new ArrayList<>();

        record CatDef(String name, String desc, String[][] subs) {}
        List<CatDef> defs = List.of(
                new CatDef("Comidas", "Platos principales y entradas", new String[][]{
                        {"Hamburguesas", "Hamburguesas artesanales"},
                        {"Pizzas", "Pizzas a la piedra"},
                        {"Pastas", "Pastas frescas y secas"}
                }),
                new CatDef("Bebidas", "Bebidas frías y calientes", new String[][]{
                        {"Gaseosas", "Bebidas carbonatadas"},
                        {"Jugos Naturales", "Jugos frescos"}
                }),
                new CatDef("Postres", "Dulces y postres", new String[][]{
                        {"Tortas", "Tortas y tartas"},
                        {"Helados", "Helados artesanales"}
                })
        );

        for (CatDef cd : defs) {
            Categoria cat = categoriaRepository.save(Categoria.builder()
                    .nombre(cd.name)
                    .descripcion(cd.desc)
                    .isActive(true)
                    .build());
            for (String[] sub : cd.subs) {
                allSubs.add(subcategoriaRepository.save(Subcategoria.builder()
                        .categoria(cat)
                        .nombre(sub[0])
                        .descripcion(sub[1])
                        .isActive(true)
                        .build()));
            }
        }
        return allSubs;
    }

    private List<EstadoPedido> seedEstadosPedido() {
        record Def(String code, String name, String color, int idx, boolean fin) {}
        List<Def> defs = List.of(
                new Def("PENDIENTE", "Pendiente", "#FFA500", 1, false),
                new Def("CONFIRMADO", "Confirmado", "#4169E1", 2, false),
                new Def("EN_PREPARACION", "En Preparación", "#9B59B6", 3, false),
                new Def("LISTO", "Listo para Entrega", "#2ECC71", 4, false),
                new Def("EN_CAMINO", "En Camino", "#3498DB", 5, false),
                new Def("ENTREGADO", "Entregado", "#228B22", 6, true),
                new Def("CANCELADO", "Cancelado", "#DC143C", 7, true)
        );

        List<EstadoPedido> result = new ArrayList<>();
        for (Def d : defs) {
            result.add(estadoPedidoRepository.findByCodigo(d.code)
                    .orElseGet(() -> estadoPedidoRepository.save(EstadoPedido.builder()
                            .codigo(d.code)
                            .nombre(d.name)
                            .color(d.color)
                            .orderIndex(d.idx)
                            .esFinal(d.fin)
                            .build())));
        }
        return result;
    }

    private List<TenantBundle> seedTenants(PlanSuscripcion planBasico, PlanSuscripcion planPro) {
        record Def(String biz, String slug, String color1, String color2, PlanSuscripcion plan,
                   String ownerName, String ownerLastName, String ownerEmail) {}

        List<Def> defs = List.of(
                new Def("Pizzería del Sol", "pizzeria-del-sol", "#E74C3C", "#F39C12", planBasico,
                        "Ana", "García", "ana.garcia@mabizz.test"),
                new Def("Café Aroma", "cafe-aroma", "#8E44AD", "#2ECC71", planPro,
                        "Luis", "Martínez", "luis.martinez@mabizz.test"),
                new Def("Burger House", "burger-house", "#E67E22", "#1ABC9C", planBasico,
                        "María", "López", "maria.lopez@mabizz.test")
        );

        List<TenantBundle> bundles = new ArrayList<>();
        for (int i = 0; i < defs.size(); i++) {
            Def d = defs.get(i);

            Tenant tenant = tenantRepository.findBySlug(d.slug).orElseGet(() -> {
                Tenant t = Tenant.builder()
                        .tenantIdentifier("tenant_seed_" + d.slug)
                        .nombreNegocio(d.biz)
                        .slug(d.slug)
                        .planSuscripcionId(d.plan.getId())
                        .colorPrimario(d.color1)
                        .colorSecundario(d.color2)
                        .visibilidadPublica(true)
                        .aceptaReservasServicios(false)
                        .permitePedidosProgramados(true)
                        .isActive(true)
                        .isSuspended(false)
                        .build();
                return tenantRepository.save(t);
            });

            // Owner
            if (tenant.getPropietarioId() == null) {
                UsuarioTenant owner = usuarioTenantRepository.save(UsuarioTenant.builder()
                        .tenantId(tenant.getId())
                        .nombre(d.ownerName)
                        .apellido(d.ownerLastName)
                        .email(d.ownerEmail)
                        .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                        .caracteristicaTelefonoZona("54")
                        .telefono("11" + String.format("%08d", 40000000 + i))
                        .sexo(i % 2 == 0 ? "F" : "M")
                        .rol("admin")
                        .isActive(true)
                        .build());
                tenant.setPropietarioId(owner.getId());
                tenantRepository.save(tenant);
            }

            TenantBundle tb = new TenantBundle();
            tb.tenant = tenant;
            bundles.add(tb);
        }
        return bundles;
    }

    private List<Cliente> seedClientes(Tenant tenant) {
        record Def(String name, String email) {}
        List<Def> defs = List.of(
                new Def("Carlos", "carlos.cliente@mabizz.test"),
                new Def("Sofía", "sofia.cliente@mabizz.test"),
                new Def("Diego", "diego.cliente@mabizz.test")
        );

        List<Cliente> result = new ArrayList<>();
        for (Def d : defs) {
            // Unique per (tenant_id, email) — build scoped email
            String scopedEmail = tenant.getSlug() + "." + d.email;
            result.add(clienteRepository.save(Cliente.builder()
                    .tenantId(tenant.getId())
                    .email(scopedEmail)
                    .passwordHash(passwordEncoder.encode(DEFAULT_PASSWORD))
                    .emailVerificado(true)
                    .build()));
        }
        return result;
    }

    private List<Direccion> seedDirecciones(List<Cliente> clientes) {
        List<Direccion> result = new ArrayList<>();
        BigDecimal baseLat = new BigDecimal("-34.6037");
        BigDecimal baseLon = new BigDecimal("-58.3816");

        for (int i = 0; i < clientes.size(); i++) {
            result.add(direccionRepository.save(Direccion.builder()
                    .clienteId(clientes.get(i).getId())
                    .latitud(baseLat.add(new BigDecimal("0.00" + (i + 1))))
                    .longitud(baseLon.add(new BigDecimal("0.00" + (i + 1))))
                    .build()));
        }
        return result;
    }

    private List<Producto> seedProductos(Tenant tenant, List<Subcategoria> subcategorias) {
        record Def(String name, String desc, BigDecimal price, BigDecimal cost, int stock, int subIdx) {}
        List<Def> defs = List.of(
                new Def("Hamburguesa Clásica", "Carne 200g, lechuga, tomate, queso",
                        bd("1500"), bd("600"), 50, 0),
                new Def("Pizza Margarita", "Mozzarella, tomate, albahaca",
                        bd("2200"), bd("800"), 30, 1),
                new Def("Pasta Carbonara", "Panceta, huevo, parmesano",
                        bd("1800"), bd("700"), 40, 2),
                new Def("Coca-Cola 500ml", "Bebida carbonatada",
                        bd("500"), bd("200"), 100, 3),
                new Def("Jugo de Naranja", "Naranja exprimida natural",
                        bd("700"), bd("300"), 60, 4),
                new Def("Torta de Chocolate", "Bizcocho húmedo con ganache",
                        bd("1200"), bd("500"), 20, 5),
                new Def("Helado Artesanal", "Dulce de leche, 2 bochas",
                        bd("900"), bd("350"), 45, 6)
        );

        List<Producto> result = new ArrayList<>();
        for (int i = 0; i < defs.size(); i++) {
            Def d = defs.get(i);
            String sku = "SEED-" + tenant.getSlug().toUpperCase().replace("-", "") + "-" + String.format("%03d", i + 1);

            // Skip if SKU already exists for this tenant
            if (productoRepository.existsByTenantIdAndSku(tenant.getId(), sku)) continue;

            Subcategoria sub = subcategorias.get(Math.min(d.subIdx, subcategorias.size() - 1));

            result.add(productoRepository.save(Producto.builder()
                    .tenantId(tenant.getId())
                    .subcategoriaId(sub.getId())
                    .nombre(d.name)
                    .descripcion(d.desc)
                    .precio(d.price)
                    .costoUnitarioCalculado(d.cost)
                    .usaCostoCalculado(true)
                    .stock(d.stock)
                    .stockReservado(0)
                    .stockMinimo(5)
                    .sku(sku)
                    .esPersonalizable(false)
                    .esServicio(false)
                    .permiteBooking(false)
                    .requiereVerificacionEdad(false)
                    .isActive(true)
                    .build()));
        }
        return result;
    }

    private List<Pedido> seedPedidos(Tenant tenant, List<Cliente> clientes,
            List<Direccion> direcciones, List<EstadoPedido> estados, List<Producto> productos) {
        if (clientes.isEmpty() || productos.isEmpty() || estados.isEmpty()) return List.of();

        List<Pedido> result = new ArrayList<>();
        Instant now = Instant.now();
        String[] origenes = {"web", "app", "whatsapp"};
        String[] metodosPago = {"efectivo", "tarjeta", "transferencia"};

        for (int i = 0; i < 15; i++) {
            Cliente cliente = clientes.get(i % clientes.size());
            EstadoPedido estado = estados.get(i % estados.size());
            Direccion dir = direcciones.isEmpty() ? null : direcciones.get(i % direcciones.size());

            String idempKey = "seed-" + tenant.getSlug() + "-order-" + (i + 1);
            if (pedidoRepository.existsByIdempotencyKey(idempKey)) continue;

            BigDecimal subtotal = bd(String.valueOf(5000 + i * 1500));
            BigDecimal impuestos = subtotal.multiply(bd("0.21")).setScale(2, RoundingMode.HALF_UP);
            BigDecimal costoEnvio = bd(i % 3 == 0 ? "0" : "500");
            BigDecimal total = subtotal.add(impuestos).add(costoEnvio);

            Pedido pedido = pedidoRepository.save(Pedido.builder()
                    .tenantId(tenant.getId())
                    .clienteId(cliente.getId())
                    .direccionEntregaId(dir != null ? dir.getId() : null)
                    .estado(estado)
                    .numeroPedido("PED-" + tenant.getSlug().substring(0, 3).toUpperCase()
                            + "-" + String.format("%04d", i + 1))
                    .tipoEntrega(i % 4 == 0 ? "retiro" : "domicilio")
                    .subtotal(subtotal)
                    .impuestos(impuestos)
                    .costoEnvio(costoEnvio)
                    .descuentoInicial(BigDecimal.ZERO)
                    .descuentoAdicional(BigDecimal.ZERO)
                    .total(total)
                    .metodoPago(metodosPago[i % metodosPago.length])
                    .estadoPago(estado.isEsFinal() ? "pagado" : "pendiente")
                    .origenPedido(origenes[i % origenes.length])
                    .trackingToken(UUID.nameUUIDFromBytes(idempKey.getBytes()).toString())
                    .correlationId(UUID.nameUUIDFromBytes(("corr-" + idempKey).getBytes()).toString())
                    .idempotencyKey(idempKey)
                    .fechaPedido(now.minus(30L - i * 2, ChronoUnit.DAYS))
                    .fechaEntregaSolicitada(now.minus(29L - i * 2, ChronoUnit.DAYS))
                    .requiereVerificacionEdad(false)
                    .esProgramado(false)
                    .build());

            // 2-3 items per order
            int itemCount = 2 + (i % 2);
            for (int j = 0; j < itemCount; j++) {
                Producto prod = productos.get((i + j) % productos.size());
                pedidoItemRepository.save(PedidoItem.builder()
                        .pedidoId(pedido.getId())
                        .productoId(prod.getId())
                        .cantidad(1 + (j % 3))
                        .precioBaseSnapshot(prod.getPrecio())
                        .precioExtrasSnapshot(BigDecimal.ZERO)
                        .costoUnitarioSnapshot(prod.getCostoUnitarioCalculado())
                        .nombreProductoSnapshot(prod.getNombre())
                        .skuSnapshot(prod.getSku())
                        .build());
            }

            result.add(pedido);
        }
        return result;
    }

    private void seedEntregas(Tenant tenant, List<Pedido> pedidos) {
        String[] estadosEntrega = {"pendiente", "asignado", "en_camino", "entregado"};
        for (int i = 0; i < pedidos.size(); i++) {
            Pedido p = pedidos.get(i);
            if (!"domicilio".equals(p.getTipoEntrega())) continue;
            // Only create delivery for ~60% of delivery orders
            if (i % 5 == 0) continue;

            entregaRepository.save(Entrega.builder()
                    .tenantId(tenant.getId())
                    .pedidoId(p.getId())
                    .estado(estadosEntrega[i % estadosEntrega.length])
                    .geolocalizacionValidada(i % 2 == 0)
                    .verificacionEdadHecha(false)
                    .build());
        }
    }

    // ── Helpers ──

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }

    /** Mutable holder to group per-tenant seeded entities */
    private static class TenantBundle {
        Tenant tenant;
        List<Cliente> clientes = List.of();
        List<Direccion> direcciones = List.of();
        List<Producto> productos = List.of();
        List<Pedido> pedidos = List.of();
    }
}
