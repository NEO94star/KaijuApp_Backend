ALTER TABLE sincronizacion_sucursal
    ALTER COLUMN estado SET DEFAULT 'DISPONIBLE';

UPDATE sincronizacion_sucursal
SET estado = 'DISPONIBLE',
    updated_at = CURRENT_TIMESTAMP
WHERE estado IN ('PENDIENTE', 'ACTUALIZADA', 'SIN_CAMBIOS',
                 'REVISION_PENDIENTE', 'REVISION_RECOMENDADA', 'CRITICO');
