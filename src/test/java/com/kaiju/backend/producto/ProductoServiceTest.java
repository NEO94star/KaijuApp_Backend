package com.kaiju.backend.producto;

import com.kaiju.backend.movimiento.entity.Movimiento;
import com.kaiju.backend.movimiento.repository.MovimientoRepository;
import com.kaiju.backend.producto.dto.ProductoDtos;
import com.kaiju.backend.producto.entity.Producto;
import com.kaiju.backend.producto.repository.ProductoRepository;
import com.kaiju.backend.producto.service.ProductoService;
import com.kaiju.backend.sincronizacion.repository.SincronizacionSucursalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductoServiceTest {
    @Test
    void ventaDescuentaStockYRegistraMovimiento() {
        ProductoRepository productos = mock(ProductoRepository.class);
        MovimientoRepository movimientos = mock(MovimientoRepository.class);
        SincronizacionSucursalRepository sincronizacion = mock(SincronizacionSucursalRepository.class);
        Producto producto = new Producto();
        producto.setId("producto-1");
        producto.setNombre("Godzilla");
        producto.setCategoria("Figura");
        producto.setPrecio(new BigDecimal("19990"));
        producto.setCantidadDisponible(10);
        producto.setStockMinimo(5);
        when(productos.findById("producto-1")).thenReturn(Optional.of(producto));

        ProductoService service = new ProductoService(productos, movimientos, sincronizacion);
        ProductoDtos.Response response = service.vender(
            "producto-1",
            new ProductoDtos.SaleRequest(3)
        );

        assertEquals(7, response.cantidadDisponible());
        verify(productos).save(producto);
        verify(movimientos).save(any(Movimiento.class));
    }

    @Test
    void ventaRechazaCantidadMayorAlStock() {
        ProductoRepository productos = mock(ProductoRepository.class);
        MovimientoRepository movimientos = mock(MovimientoRepository.class);
        SincronizacionSucursalRepository sincronizacion = mock(SincronizacionSucursalRepository.class);
        Producto producto = new Producto();
        producto.setId("producto-1");
        producto.setCantidadDisponible(2);
        when(productos.findById("producto-1")).thenReturn(Optional.of(producto));

        ProductoService service = new ProductoService(productos, movimientos, sincronizacion);

        assertThrows(ResponseStatusException.class,
            () -> service.vender("producto-1", new ProductoDtos.SaleRequest(3)));
        verify(productos, never()).save(any());
        verify(movimientos, never()).save(any());
    }
}
