CREATE TABLE IF NOT EXISTS productos (
    id VARCHAR(36) PRIMARY KEY,
    nombre VARCHAR(40) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    precio NUMERIC(12, 2) NOT NULL CHECK (precio > 0),
    cantidad_disponible INTEGER NOT NULL CHECK (cantidad_disponible >= 0),
    stock_minimo INTEGER NOT NULL CHECK (stock_minimo >= 0),
    imagen_uri TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS movimientos (
    id VARCHAR(36) PRIMARY KEY,
    producto_id VARCHAR(36) NOT NULL REFERENCES productos(id),
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('ENTRADA', 'VENTA')),
    cantidad INTEGER NOT NULL CHECK (cantidad > 0),
    fecha TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_movimientos_producto_id ON movimientos(producto_id);
