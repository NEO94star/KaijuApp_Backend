package com.kaiju.backend.movimiento.dto;

import com.kaiju.backend.movimiento.entity.Movimiento;
import java.time.OffsetDateTime;

public class MovimientoDtos {
    public record Response(
        String id,
        String productoId,
        String tipo,
        Integer cantidad,
        OffsetDateTime fecha
    ) {
        public static Response from(Movimiento movimiento) {
            return new Response(
                movimiento.getId(),
                movimiento.getProductoId(),
                movimiento.getTipo(),
                movimiento.getCantidad(),
                movimiento.getFecha()
            );
        }
    }
}
