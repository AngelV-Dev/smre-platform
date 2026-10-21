-- ============================================================
-- MIGRACIÓN NECESARIA: soporte para ADMIN_TUTOR en ENUMs
-- Proyecto: SMRE (smre-tecsup)
-- Tarea: Asignación de roles exclusiva para ksalas
-- Fecha: 2026-10-19
-- ============================================================
--
-- Hibernate ddl-auto=update NO modifica automáticamente los ENUMs
-- de MySQL. Este script debe ejecutarse UNA VEZ en cada entorno
-- (local, staging, producción) antes de levantar el backend
-- con la rama feature/asignacion-roles-ksalas.
--
-- ⚠ EJECUTAR CON PERMISOS ALTER TABLE sobre smre_db.
-- ⚠ Hacer un backup antes si hay datos en producción.
-- ============================================================

-- 1. Tabla usuarios (auth)
ALTER TABLE smre_db.usuarios
    MODIFY COLUMN rol ENUM('ADMIN','TUTOR','ADMIN_TUTOR') NOT NULL;

-- 2. Tabla tutores (módulo de usuario/tutor)
ALTER TABLE smre_db.tutores
    MODIFY COLUMN rol ENUM('ADMIN','TUTOR','ADMIN_TUTOR') NOT NULL;

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================
