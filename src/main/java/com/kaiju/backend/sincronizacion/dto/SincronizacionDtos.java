package com.kaiju.backend.sincronizacion.dto;

import com.kaiju.backend.sincronizacion.entity.SincronizacionSucursal;
import java.time.OffsetDateTime;

public class SincronizacionDtos {
    public record RevisionRequest(
        boolean cambiosReales
    ) {}

    public record Response(
        String sucursal,
        OffsetDateTime ultimaSincronizacion,
        Boolean cambiosReales,
        String estado,
        String textoUltimaSincronizacion,
        Boolean noDisponible
    ) {
        public static Response from(SincronizacionSucursal sync, String estado,
                                    String textoUltimaSincronizacion, boolean noDisponible) {
            return new Response(
                sync.getSucursal(),
                sync.getUltimaSincronizacion(),
                sync.isCambiosReales(),
                estado,
                textoUltimaSincronizacion,
                noDisponible
            );
        }
    }

    public record Operation(
        String accion,
        String productoId,
        String nombre,
        String categoria,
        java.math.BigDecimal precio,
        Integer cantidadDisponible,
        Integer stockMinimo,
        String sucursal,
        String imagenUri,
        Integer cantidad
    ) {}

    public record BatchRequest(
        String sucursal,
        java.util.List<Operation> operaciones
    ) {}

    public record BatchResponse(
        String sucursal,
        int operacionesRecibidas,
        int operacionesAplicadas,
        OffsetDateTime ultimaSincronizacion
    ) {}
}
