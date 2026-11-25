DELETE FROM movimientos
WHERE producto_id IN (
    SELECT id
    FROM (
        SELECT id,
               row_number() OVER (
                   PARTITION BY lower(sucursal), lower(nombre)
                   ORDER BY updated_at DESC, id DESC
               ) AS rn
        FROM productos
    ) ranked
    WHERE rn > 1
);

DELETE FROM productos
WHERE id IN (
    SELECT id
    FROM (
        SELECT id,
               row_number() OVER (
                   PARTITION BY lower(sucursal), lower(nombre)
                   ORDER BY updated_at DESC, id DESC
               ) AS rn
        FROM productos
    ) ranked
    WHERE rn > 1
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_productos_unique_sucursal_nombre
    ON productos (lower(sucursal), lower(nombre));
