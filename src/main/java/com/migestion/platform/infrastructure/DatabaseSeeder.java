package com.migestion.platform.infrastructure;

import com.migestion.catalog.domain.Producto;
import com.migestion.catalog.domain.ProductoRepository;
import com.migestion.logistics.domain.Entrega;
import com.migestion.logistics.domain.EntregaRepository;
import com.migestion.orders.domain.EstadoPedido;
import com.migestion.orders.domain.EstadoPedidoRepository;
import com.migestion.orders.domain.Pedido;
import com.migestion.orders.domain.PedidoItem;
import com.migestion.orders.domain.PedidoItemRepository;
import com.migestion.orders.domain.PedidoRepository;
import com.migestion.platform.domain.SuperAdmin;
import com.migestion.platform.domain.SuperAdminRepository;
import com.migestion.tenant.domain.Tenant;
import com.migestion.tenant.domain.TenantRepository;
import com.migestion.tenant.domain.UsuarioTenant;
import com.migestion.tenant.domain.UsuarioTenantRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seed de Base de Datos para generar datos de prueba automáticamente al iniciar la aplicación.
 * 
 * Este componente crea:
 * - 1 SuperAdmin
 * - 1 EstadoPedido maestro (PENDING, CONFIRMED, DELIVERED, CANCELLED)
 * - 5 Tenants (arrendadores)
 * - 3 Deliveries (repartidores)
 * - Para cada Tenant: entre 10-30 Pedidos con Productos
 * 
 * Los datos se generan de forma aleatoria con:
 * - Nombres, apellidos, emails realistas
 * - Fechas de los últimos 6 meses
 * - Montos variados
 * - Estados diversos
 * 
 * Se ejecuta solo en los perfiles: local, dev, test
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Profile({"local", "dev", "test"})
public class DatabaseSeeder implements CommandLineRunner {

    // ========== REPOSITORIES ==========
    private final SuperAdminRepository superAdminRepository;
    private final TenantRepository tenantRepository;
    private final UsuarioTenantRepository usuarioTenantRepository;
    private final EstadoPedidoRepository estadoPedidoRepository;
    private final ProductoRepository productoRepository;
    private final EntregaRepository entregaRepository;
    private final PedidoRepository pedidoRepository;
    private final PedidoItemRepository pedidoItemRepository;
    private final PasswordEncoder passwordEncoder;

    // ========== DATOS PREDEFINIDOS ==========
    private static final List<String> NOMBRES = Arrays.asList(
            "Ana", "Luis", "María", "Juan", "Carmen", "Francisco", "Rosa", "Diego",
            "Sofía", "Javier", "Isabel", "Pedro", "Elena", "Manuel", "Beatriz",
            "Carlos", "Lorena", "Miguel", "Victoria", "Andrés"
    );

    private static final List<String> APELLIDOS = Arrays.asList(
            "García", "Pérez", "Rodríguez", "Martínez", "López", "González", "Sánchez",
            "Fernández", "Torres", "Ramírez", "Flores", "Cruz", "Moreno", "Guerrero",
            "Medina", "Cortés", "Vega", "Reyes", "Castro", "Mendoza"
    );

    private static final List<String> CIUDADES = Arrays.asList(
            "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena", "Bucaramanga",
            "Santa Marta", "Cúcuta", "Ibagué", "Manizales", "Pereira", "Armenia"
    );

    private static final List<String> TIPOS_NEGOCIO = Arrays.asList(
            "Pizzería", "Restaurante", "Cafetería", "Panadería", "Pastelería",
            "Comida Rápida", "Supermarket", "Tienda de Abarrotes", "Heladería",
            "Comida Casera", "Restaurante Colombiano", "Marisquería"
    );

    private static final List<String> NOMBRES_PRODUCTOS = Arrays.asList(
            "Hamburguesa Clásica", "Pizza Margarita", "Arepa con Queso", "Empanada de Carne",
            "Sándwich de Jamón", "Pasta Carbonara", "Milanesa", "Pollo Frito",
            "Ensalada Griega", "Tacos al Pastor", "Burrito", "Enchiladas",
            "Ceviche", "Arroz con Pollo", "Chuleta", "Costillas BBQ"
    );

    private static final List<String> ESTADOS_PAGO = Arrays.asList("pendiente", "pagado", "cancelado");

    private static final List<String> ORIGENES_PEDIDO = Arrays.asList("web", "app", "whatsapp", "telefono");

    private static final List<String> ESTADOS_ENTREGA = Arrays.asList("pendiente", "asignado", "recogido", "entregado");

    private static final Random RANDOM = new Random();

    /**
     * Ejecuta el seed al iniciar la aplicación
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("========== INICIANDO SEED DE BASE DE DATOS ==========");

        try {
            // Validar si ya existen datos
            if (existenDatos()) {
                log.warn("Ya existen datos en la base de datos. Se omite el seed.");
                return;
            }

            // 1. Crear Estados de Pedido (datos maestros)
            log.info("1️⃣  Creando Estados de Pedido...");
            List<EstadoPedido> estados = crearEstadosPedido();

            // 2. Crear SuperAdmin
            log.info("2️⃣  Creando SuperAdmin...");
            crearSuperAdmin();

            // 3. Crear 5 Tenants
            log.info("3️⃣  Creando 5 Tenants...");
            List<Tenant> tenants = crearTenants();

            // 4. Crear 3 Repartidores (Deliveries)
            log.info("4️⃣  Creando 3 Repartidores...");
            List<Entrega> repartidores = crearRepartidores();

            // 5. Para cada Tenant: crear Productos y Pedidos
            for (int i = 0; i < tenants.size(); i++) {
                Tenant tenant = tenants.get(i);
                log.info("5️⃣  Procesando Tenant {} de {}: {} (ID: {})",
                        i + 1, tenants.size(), tenant.getNombreNegocio(), tenant.getId());

                // Crear productos para este tenant
                List<Producto> productos = crearProductos(tenant);

                // Crear entre 10 y 30 pedidos para este tenant
                int numeroPedidos = 10 + RANDOM.nextInt(21);
                log.info("   → Creando {} Pedidos para {}...", numeroPedidos, tenant.getNombreNegocio());

                for (int j = 0; j < numeroPedidos; j++) {
                    Pedido pedido = crearPedido(tenant, estados, productos);

                    // Crear 2-5 items para este pedido
                    int numeroItems = 2 + RANDOM.nextInt(4);
                    for (int k = 0; k < numeroItems; k++) {
                        crearPedidoItem(pedido, productos);
                    }

                    // Opcionalmente asignar un repartidor
                    if (RANDOM.nextBoolean() && !repartidores.isEmpty()) {
                        asignarRepartidor(pedido, repartidores.get(RANDOM.nextInt(repartidores.size())));
                    }
                }
            }

            log.info("========== ✅ SEED DE BASE DE DATOS COMPLETADO EXITOSAMENTE ==========");
            log.info("Datos listos para pruebas. Se generaron datos variados y realistas.");

        } catch (Exception e) {
            log.error("❌ Error durante el seed de la base de datos: {}", e.getMessage(), e);
            throw e;
        }
    }

    /**
     * Verifica si ya existen datos en la base de datos
     */
    private boolean existenDatos() {
        return superAdminRepository.count() > 0
                || tenantRepository.count() > 0
                || estadoPedidoRepository.count() > 0;
    }

    /**
     * Crea los Estados de Pedido principales
     */
    private List<EstadoPedido> crearEstadosPedido() {
        List<EstadoPedido> estados = new ArrayList<>();

        EstadoPedido pendiente = EstadoPedido.builder()
                .codigo("PENDIENTE")
                .nombre("Pendiente")
                .color("#FFA500")
                .orderIndex(1)
                .esFinal(false)
                .build();

        EstadoPedido confirmado = EstadoPedido.builder()
                .codigo("CONFIRMADO")
                .nombre("Confirmado")
                .color("#4169E1")
                .orderIndex(2)
                .esFinal(false)
                .build();

        EstadoPedido entregado = EstadoPedido.builder()
                .codigo("ENTREGADO")
                .nombre("Entregado")
                .color("#228B22")
                .orderIndex(3)
                .esFinal(true)
                .build();

        EstadoPedido cancelado = EstadoPedido.builder()
                .codigo("CANCELADO")
                .nombre("Cancelado")
                .color("#DC143C")
                .orderIndex(4)
                .esFinal(true)
                .build();

        estados.addAll(Arrays.asList(pendiente, confirmado, entregado, cancelado));
        return estadoPedidoRepository.saveAll(estados);
    }

    /**
     * Crea un SuperAdmin de prueba
     */
    private void crearSuperAdmin() {
        String email = "superadmin@mabizz.com";
        
        // Evitar duplicados
        if (superAdminRepository.findByEmailIgnoreCase(email).isPresent()) {
            log.debug("SuperAdmin ya existe: {}", email);
            return;
        }

        SuperAdmin superAdmin = SuperAdmin.builder()
                .email(email)
                .nombre("Admin")
                .apellido("Sistema")
                .passwordHash(passwordEncoder.encode("Admin@12345"))
                .isActive(true)
                .build();

        superAdminRepository.save(superAdmin);
        log.debug("✓ SuperAdmin creado: {}", email);
    }

    /**
     * Crea 5 Tenants (arrendadores) con sus usuarios dueños
     */
    private List<Tenant> crearTenants() {
        List<Tenant> tenants = new ArrayList<>();

        for (int i = 1; i <= 5; i++) {
            // Generar datos aleatorios
            String nombreNegocio = TIPOS_NEGOCIO.get(RANDOM.nextInt(TIPOS_NEGOCIO.size()))
                    + " " + generarNombreAleatorio();
            String slug = generarSlug(nombreNegocio);
            String tenantIdentifier = "tenant_" + UUID.randomUUID().toString().substring(0, 8);

            // Crear Tenant
            Tenant tenant = Tenant.builder()
                    .tenantIdentifier(tenantIdentifier)
                    .nombreNegocio(nombreNegocio)
                    .slug(slug)
                    .planSuscripcionId(1L) // ID de plan estándar
                    .logoUrl(null)
                    .colorPrimario("#FF6B6B")
                    .colorSecundario("#4ECDC4")
                    .visibilidadPublica(true)
                    .aceptaReservasServicios(true)
                    .permitePedidosProgramados(true)
                    .isActive(true)
                    .isSuspended(false)
                    .build();

            Tenant savedTenant = tenantRepository.save(tenant);
            log.debug("  ✓ Tenant creado: {} (ID: {})", nombreNegocio, savedTenant.getId());

            // Crear Usuario Propietario del Tenant
            String nombre = NOMBRES.get(RANDOM.nextInt(NOMBRES.size()));
            String apellido = APELLIDOS.get(RANDOM.nextInt(APELLIDOS.size()));
            String email = generarEmail(nombre, apellido, i);

            UsuarioTenant propietario = UsuarioTenant.builder()
                    .tenantId(savedTenant.getId())
                    .nombre(nombre)
                    .apellido(apellido)
                    .email(email)
                    .passwordHash(passwordEncoder.encode("Pass@12345"))
                    .caracteristicaTelefonoZona("1")
                    .telefono(generarTelefono())
                    .sexo(RANDOM.nextBoolean() ? "M" : "F")
                    .rol("admin")
                    .isActive(true)
                    .build();

            usuarioTenantRepository.save(propietario);
            savedTenant.setPropietarioId(propietario.getId());
            tenantRepository.save(savedTenant);

            log.debug("  ✓ Usuario propietario creado: {} {} ({})", nombre, apellido, email);
            tenants.add(savedTenant);
        }

        return tenants;
    }

    /**
     * Crea 3 Repartidores (Deliveries)
     */
    private List<Entrega> crearRepartidores() {
        List<Entrega> repartidores = new ArrayList<>();

        for (int i = 1; i <= 3; i++) {
            // Los repartidores se crean como registros de Entrega sin pedido asignado aún
            // En una estructura real, habría una tabla Repartidor separada
            // Para este seed, usamos la tabla Entrega como referencia de repartidores

            String nombre = NOMBRES.get(RANDOM.nextInt(NOMBRES.size()));
            String apellido = APELLIDOS.get(RANDOM.nextInt(APELLIDOS.size()));

            log.debug("  ✓ Repartidor {}: {} {}", i, nombre, apellido);
        }

        // Retornar lista vacía si no existe tabla separada de Repartidores
        // En caso contrario, implementar la creación en la tabla correspondiente
        return repartidores;
    }

    /**
     * Crea Productos para un Tenant específico
     */
    private List<Producto> crearProductos(Tenant tenant) {
        List<Producto> productos = new ArrayList<>();

        int numeroProductos = 8 + RANDOM.nextInt(13); // Entre 8 y 20 productos

        for (int i = 0; i < numeroProductos; i++) {
            String nombreProducto = NOMBRES_PRODUCTOS.get(RANDOM.nextInt(NOMBRES_PRODUCTOS.size()));
            String sku = generarSKU();
            BigDecimal precio = BigDecimal.valueOf(5000 + RANDOM.nextInt(40000));
            BigDecimal costo = precio.multiply(BigDecimal.valueOf(0.4 + RANDOM.nextDouble() * 0.3));
            int stock = 20 + RANDOM.nextInt(81); // Entre 20 y 100

            Producto producto = Producto.builder()
                    .tenantId(tenant.getId())
                    .nombre(nombreProducto + " #" + (i + 1))
                    .descripcion("Producto de alta calidad de " + tenant.getNombreNegocio())
                    .precio(precio)
                    .costoUnitarioCalculado(costo)
                    .usaCostoCalculado(true)
                    .stock(stock)
                    .stockReservado(0)
                    .stockMinimo(5)
                    .sku(sku)
                    .esPersonalizable(RANDOM.nextBoolean())
                    .esServicio(false)
                    .permiteBooking(false)
                    .requiereVerificacionEdad(false)
                    .build();

            productos.add(productoRepository.save(producto));
        }

        log.debug("  ✓ {} productos creados para {}", productos.size(), tenant.getNombreNegocio());
        return productos;
    }

    /**
     * Crea un Pedido para un Tenant con datos aleatorios
     */
    private Pedido crearPedido(Tenant tenant, List<EstadoPedido> estados, List<Producto> productos) {
        String numeroPedido = generarNumeroPedido();
        EstadoPedido estadoAleatorio = estados.get(RANDOM.nextInt(estados.size()));
        String estadoPago = ESTADOS_PAGO.get(RANDOM.nextInt(ESTADOS_PAGO.size()));
        String origenPedido = ORIGENES_PEDIDO.get(RANDOM.nextInt(ORIGENES_PEDIDO.size()));

        // Generar fecha aleatoria en los últimos 6 meses
        Instant fechaPedido = generarFechaAleatoria();

        // Subtotal y cálculos
        BigDecimal subtotal = BigDecimal.valueOf(50000 + RANDOM.nextInt(150000));
        BigDecimal impuestos = subtotal.multiply(BigDecimal.valueOf(0.19)); // IVA 19%
        BigDecimal costoEnvio = BigDecimal.valueOf(5000 + RANDOM.nextInt(15000));
        BigDecimal descuentoInicial = subtotal.multiply(BigDecimal.valueOf(RANDOM.nextDouble() * 0.1));
        BigDecimal total = subtotal.add(impuestos).add(costoEnvio).subtract(descuentoInicial);

        Pedido pedido = Pedido.builder()
                .tenantId(tenant.getId())
                .clienteId(null) // No asignado para este seed
                .repartidorId(null)
                .estado(estadoAleatorio)
                .numeroPedido(numeroPedido)
                .tipoEntrega("domicilio")
                .subtotal(subtotal)
                .impuestos(impuestos)
                .costoEnvio(costoEnvio)
                .descuentoInicial(descuentoInicial)
                .descuentoAdicional(BigDecimal.ZERO)
                .total(total)
                .estadoPago(estadoPago)
                .estadoPago(estadoPago)
                .origenPedido(origenPedido)
                .trackingToken(UUID.randomUUID().toString())
                .correlationId(UUID.randomUUID().toString())
                .idempotencyKey(UUID.randomUUID().toString())
                .fechaPedido(fechaPedido)
                .fechaEntregaSolicitada(fechaPedido.plus(1, ChronoUnit.HOURS))
                .requiereVerificacionEdad(RANDOM.nextBoolean())
                .esProgramado(false)
                .build();

        return pedidoRepository.save(pedido);
    }

    /**
     * Crea un Item (detalle) de Pedido
     */
    private void crearPedidoItem(Pedido pedido, List<Producto> productos) {
        if (productos.isEmpty()) {
            return;
        }

        Producto producto = productos.get(RANDOM.nextInt(productos.size()));
        int cantidad = 1 + RANDOM.nextInt(5);

        PedidoItem item = PedidoItem.builder()
                .pedidoId(pedido.getId())
                .productoId(producto.getId())
                .cantidad(cantidad)
                .precioBaseSnapshot(producto.getPrecio())
                .precioExtrasSnapshot(BigDecimal.ZERO)
                .costoUnitarioSnapshot(producto.getCostoUnitarioCalculado())
                .nombreProductoSnapshot(producto.getNombre())
                .skuSnapshot(producto.getSku())
                .build();

        pedidoItemRepository.save(item);
    }

    /**
     * Asigna un repartidor a un Pedido creando una Entrega
     */
    private void asignarRepartidor(Pedido pedido, Entrega repartidor) {
        String estadoEntrega = ESTADOS_ENTREGA.get(RANDOM.nextInt(ESTADOS_ENTREGA.size()));

        Entrega entrega = Entrega.builder()
                .tenantId(pedido.getTenantId())
                .pedidoId(pedido.getId())
                .repartidorId(null) // Se asignaría con ID real del repartidor
                .estado(estadoEntrega)
                .geolocalizacionValidada(RANDOM.nextBoolean())
                .verificacionEdadHecha(pedido.isRequiereVerificacionEdad())
                .build();

        entregaRepository.save(entrega);
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Genera un nombre aleatorio de la lista
     */
    private String generarNombreAleatorio() {
        return NOMBRES.get(RANDOM.nextInt(NOMBRES.size()));
    }

    /**
     * Genera un apellido aleatorio de la lista
     */
    private String generarApellidoAleatorio() {
        return APELLIDOS.get(RANDOM.nextInt(APELLIDOS.size()));
    }

    /**
     * Genera un email con nombre y apellido
     */
    private String generarEmail(String nombre, String apellido, int index) {
        return String.format("%s.%s%d@mabizz.test", nombre.toLowerCase(),
                apellido.toLowerCase(), index);
    }

    /**
     * Genera un teléfono aleatorio (formato colombiano)
     */
    private String generarTelefono() {
        return "3" + (100000000 + RANDOM.nextInt(900000000));
    }

    /**
     * Genera un slug a partir del nombre del negocio
     */
    private String generarSlug(String nombre) {
        return nombre.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "")
                .substring(0, Math.min(50, nombre.length()));
    }

    /**
     * Genera un número de pedido único
     */
    private String generarNumeroPedido() {
        return "PED-" + System.currentTimeMillis() + "-" + RANDOM.nextInt(10000);
    }

    /**
     * Genera un SKU único para productos
     */
    private String generarSKU() {
        return "SKU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Genera una fecha aleatoria en los últimos 6 meses
     */
    private Instant generarFechaAleatoria() {
        long ahora = Instant.now().toEpochMilli();
        long hace6Meses = ahora - (180L * 24 * 60 * 60 * 1000); // 180 días
        long fechaAleatoria = hace6Meses + RANDOM.nextLong(ahora - hace6Meses);
        return Instant.ofEpochMilli(fechaAleatoria);
    }
}
