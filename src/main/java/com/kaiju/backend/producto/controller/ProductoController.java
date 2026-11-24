package com.kaiju.backend.producto.controller;

import com.kaiju.backend.producto.dto.ProductoDtos;
import com.kaiju.backend.producto.service.ProductoService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${CORS_ALLOWED_ORIGINS:http://localhost:3000}")
@Tag(name = "Productos", description = "Gestión del inventario de productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/productos")
    @Operation(summary = "Listar productos")
    public List<ProductoDtos.Response> listar(
            @RequestParam(required = false) String sucursal) {
        return sucursal == null || sucursal.isBlank()
            ? productoService.listar()
            : productoService.listarPorSucursal(sucursal);
    }

    @GetMapping("/sucursales")
    @Operation(summary = "Listar sucursales disponibles")
    public List<String> listarSucursales() {
        return productoService.listarSucursales();
    }

    @GetMapping("/sucursales/{sucursal}/stock-bajo")
    @Operation(summary = "Listar productos con stock bajo en una sucursal")
    public List<ProductoDtos.Response> listarStockBajo(@PathVariable String sucursal) {
        return productoService.listarStockBajoPorSucursal(sucursal);
    }

    @GetMapping("/productos/{id}")
    @Operation(summary = "Obtener un producto")
    public ProductoDtos.Response obtener(@PathVariable String id) {
        return productoService.obtener(id);
    }

    @PostMapping("/productos")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear un producto")
    public ProductoDtos.Response crear(@Valid @RequestBody ProductoDtos.CreateRequest request) {
        return productoService.crear(request);
    }

    @PutMapping("/productos/{id}")
    @Operation(summary = "Actualizar un producto")
    public ProductoDtos.Response actualizar(@PathVariable String id,
                                             @Valid @RequestBody ProductoDtos.UpdateRequest request) {
        return productoService.actualizar(id, request);
    }

    @DeleteMapping("/productos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar un producto")
    public void eliminar(@PathVariable String id) {
        productoService.eliminar(id);
    }

    @PostMapping("/productos/{id}/ventas")
    @Operation(summary = "Registrar una venta")
    public ProductoDtos.Response vender(@PathVariable String id,
                                        @Valid @RequestBody ProductoDtos.SaleRequest request) {
        return productoService.vender(id, request);
    }

}
