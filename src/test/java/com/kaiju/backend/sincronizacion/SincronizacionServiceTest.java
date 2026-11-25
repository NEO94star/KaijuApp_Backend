package com.kaiju.backend.sincronizacion;

import com.kaiju.backend.movimiento.entity.Movimiento;
import com.kaiju.backend.movimiento.repository.MovimientoRepository;
import com.kaiju.backend.producto.entity.Producto;
import com.kaiju.backend.producto.repository.ProductoRepository;
import com.kaiju.backend.sincronizacion.dto.SincronizacionDtos;
import com.kaiju.backend.sincronizacion.repository.SincronizacionSucursalRepository;
import com.kaiju.backend.sincronizacion.service.SincronizacionService;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

class SincronizacionServiceTest {
    @Test
    void loteConProductoSinIdActualizaElExistenteEnLugarDeDuplicarlo() {
        SincronizacionSucursalRepository sincronizacion = mock(SincronizacionSucursalRepository.class);
        ProductoRepository productos = mock(ProductoRepository.class);
        MovimientoRepository movimientos = mock(MovimientoRepository.class);
        Producto existente = new Producto();
        existente.setId("producto-1");
        when(productos.findFirstBySucursalIgnoreCaseAndNombreIgnoreCase(
            "San Bernardo", "Godzilla"))
            .thenReturn(Optional.of(existente));

        SincronizacionService service = new SincronizacionService(
            sincronizacion, productos, movimientos);
        SincronizacionDtos.BatchRequest request = new SincronizacionDtos.BatchRequest(
            "San Bernardo",
            List.of(new SincronizacionDtos.Operation(
                "ADD", null, "Godzilla", "Coleccionables",
                new BigDecimal("19990"), 10, 5, null, null, null))
        );

        service.aplicarLote(request);

        verify(productos).save(existente);
        verify(productos, never()).save(argThat(producto ->
            producto.getId() != null && !producto.getId().equals("producto-1")));
        verify(movimientos, never()).save(any(Movimiento.class));
    }
}
