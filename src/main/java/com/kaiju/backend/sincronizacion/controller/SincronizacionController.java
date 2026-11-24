package com.kaiju.backend.sincronizacion.controller;

import com.kaiju.backend.sincronizacion.dto.SincronizacionDtos;
import com.kaiju.backend.sincronizacion.service.SincronizacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${CORS_ALLOWED_ORIGINS:http://localhost:3000}")
@Tag(name = "Sincronización", description = "Revisión de stock y sincronización por sucursal")
public class SincronizacionController {
    private final SincronizacionService service;

    public SincronizacionController(SincronizacionService service) {
        this.service = service;
    }

    @GetMapping("/sincronizacion")
    @Operation(summary = "Listar estado de sincronización de todas las sucursales")
    public List<SincronizacionDtos.Response> listar() {
        return service.listar();
    }

    @GetMapping("/sincronizacion/{sucursal}")
    @Operation(summary = "Consultar estado de sincronización de una sucursal")
    public SincronizacionDtos.Response obtener(@PathVariable String sucursal) {
        return service.obtener(sucursal);
    }

    @PostMapping("/sincronizacion/{sucursal}/revisar")
    @Operation(summary = "Registrar revisión manual o automática de una sucursal")
    public SincronizacionDtos.Response revisar(
            @PathVariable String sucursal,
            @RequestBody SincronizacionDtos.RevisionRequest request) {
        return service.revisar(sucursal, request);
    }

    @PostMapping("/sincronizacion/lote")
    @Operation(summary = "Aplicar operaciones locales de una sucursal")
    public SincronizacionDtos.BatchResponse aplicarLote(
            @RequestBody SincronizacionDtos.BatchRequest request) {
        return service.aplicarLote(request);
    }
}
