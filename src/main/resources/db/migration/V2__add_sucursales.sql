ALTER TABLE productos
    ADD COLUMN IF NOT EXISTS sucursal VARCHAR(50) NOT NULL DEFAULT 'Central';

CREATE INDEX IF NOT EXISTS idx_productos_sucursal ON productos(sucursal);

CREATE TABLE IF NOT EXISTS sincronizacion_sucursal (
    sucursal VARCHAR(50) PRIMARY KEY,
    ultima_sincronizacion TIMESTAMP WITH TIME ZONE,
    cambios_reales BOOLEAN NOT NULL DEFAULT FALSE,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);
