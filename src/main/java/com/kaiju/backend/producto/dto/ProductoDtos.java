package com.kaiju.backend.producto.dto;

import com.kaiju.backend.producto.entity.Producto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public class ProductoDtos {
    public record CreateRequest(
        @NotBlank @Size(min = 3, max = 40) String nombre,
        @Size(max = 30) String categoria,
        @NotNull @Positive BigDecimal precio,
        @NotNull @PositiveOrZero Integer cantidadDisponible,
        @NotNull @PositiveOrZero Integer stockMinimo,
        @NotBlank @Size(max = 50) String sucursal,
        @Size(max = 2048) String imagenUri
    ) {}

    public record UpdateRequest(
        @NotBlank @Size(min = 3, max = 40) String nombre,
        @Size(max = 30) String categoria,
        @NotNull @Positive BigDecimal precio,
        @NotNull @PositiveOrZero Integer cantidadDisponible,
        @NotNull @PositiveOrZero Integer stockMinimo,
        @NotBlank @Size(max = 50) String sucursal,
        @Size(max = 2048) String imagenUri
    ) {}

    public record SaleRequest(
        @NotNull @Positive Integer cantidad
    ) {}

    public record Response(
        String id,
        String nombre,
        String categoria,
        BigDecimal precio,
        Integer cantidadDisponible,
        Integer stockMinimo,
        String imagenUri,
        String sucursal,
        boolean stockBajo
    ) {
        public static Response from(Producto producto) {
            return new Response(
                producto.getId(),
                producto.getNombre(),
                producto.getCategoria(),
                producto.getPrecio(),
                producto.getCantidadDisponible(),
                producto.getStockMinimo(),
                producto.getImagenUri(),
                producto.getSucursal(),
                producto.getCantidadDisponible() <= producto.getStockMinimo()
            );
        }
    }
}
