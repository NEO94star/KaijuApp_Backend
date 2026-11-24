package com.kaiju.backend.movimiento.controller;

import com.kaiju.backend.movimiento.dto.MovimientoDtos;
import com.kaiju.backend.movimiento.repository.MovimientoRepository;
import com.kaiju.backend.producto.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${CORS_ALLOWED_ORIGINS:http://localhost:3000}")
@Tag(name = "Movimientos", description = "Historial de entradas y ventas")
public class MovimientoController {
    private final ProductoService productoService;
    private final MovimientoRepository movimientoRepository;

    public MovimientoController(ProductoService productoService,
                                MovimientoRepository movimientoRepository) {
        this.productoService = productoService;
        this.movimientoRepository = movimientoRepository;
    }

    @GetMapping("/productos/{id}/movimientos")
    @Operation(summary = "Listar movimientos de un producto")
    public List<MovimientoDtos.Response> listar(@PathVariable String id) {
        productoService.obtener(id);
        return movimientoRepository.findByProductoIdOrderByFechaDesc(id).stream()
            .map(MovimientoDtos.Response::from)
            .toList();
    }
}
