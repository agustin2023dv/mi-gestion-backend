-- ============================================================
-- reset-local-db.sql — LOCAL ONLY
-- Wipes all seeded tables and resets auto-increment sequences.
-- Run manually via psql if needed outside of --seed.reset=true
--
-- Usage:
--   psql -U postgres -d migestion_local -f reset-local-db.sql
-- ============================================================

BEGIN;

-- Reverse FK dependency order
TRUNCATE TABLE entrega              RESTART IDENTITY CASCADE;
TRUNCATE TABLE pedidoitem           RESTART IDENTITY CASCADE;
TRUNCATE TABLE pedido               RESTART IDENTITY CASCADE;
TRUNCATE TABLE producto             RESTART IDENTITY CASCADE;
TRUNCATE TABLE subcategoria         RESTART IDENTITY CASCADE;
TRUNCATE TABLE categoria            RESTART IDENTITY CASCADE;
TRUNCATE TABLE direccion            RESTART IDENTITY CASCADE;
TRUNCATE TABLE cliente              RESTART IDENTITY CASCADE;
TRUNCATE TABLE "UsuarioTenant"      RESTART IDENTITY CASCADE;
TRUNCATE TABLE tenant               RESTART IDENTITY CASCADE;
TRUNCATE TABLE super_admin          RESTART IDENTITY CASCADE;
TRUNCATE TABLE plan_suscripcion     RESTART IDENTITY CASCADE;
TRUNCATE TABLE estadopedido         RESTART IDENTITY CASCADE;

COMMIT;

-- Verify
SELECT 'reset-local-db.sql executed successfully' AS status;
