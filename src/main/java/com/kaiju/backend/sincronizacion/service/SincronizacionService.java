package com.kaiju.backend.sincronizacion.service;

import com.kaiju.backend.sincronizacion.dto.SincronizacionDtos;
import com.kaiju.backend.sincronizacion.entity.SincronizacionSucursal;
import com.kaiju.backend.sincronizacion.repository.SincronizacionSucursalRepository;
import com.kaiju.backend.producto.repository.ProductoRepository;
import com.kaiju.backend.movimiento.entity.Movimiento;
import com.kaiju.backend.movimiento.repository.MovimientoRepository;
import com.kaiju.backend.producto.entity.Producto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class SincronizacionService {
    private final SincronizacionSucursalRepository repository;
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public SincronizacionService(SincronizacionSucursalRepository repository,
                                 ProductoRepository productoRepository,
                                 MovimientoRepository movimientoRepository) {
        this.repository = repository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public SincronizacionDtos.Response revisar(String sucursal,
                                                SincronizacionDtos.RevisionRequest request) {
        SincronizacionSucursal sync = repository.findById(sucursal)
            .orElseGet(() -> nueva(sucursal));
        sync.setUltimaSincronizacion(OffsetDateTime.now());
        sync.setCambiosReales(request.cambiosReales());
        sync.setEstado("DISPONIBLE");
        sync.setUpdatedAt(OffsetDateTime.now());
        return calcular(repository.save(sync));
    }

    @Transactional(readOnly = true)
    public SincronizacionDtos.Response obtener(String sucursal) {
        SincronizacionSucursal sync = repository.findById(sucursal)
            .orElseGet(() -> nueva(sucursal));
        return calcular(sync);
    }

    @Transactional(readOnly = true)
    public List<SincronizacionDtos.Response> listar() {
        return repository.findAll().stream()
            .map(this::calcular)
            .toList();
    }

    @Transactional
    public SincronizacionDtos.BatchResponse aplicarLote(SincronizacionDtos.BatchRequest request) {
        String sucursal = request.sucursal().trim();
        int recibidas = request.operaciones() == null ? 0 : request.operaciones().size();
        int aplicadas = 0;

        if (request.operaciones() != null) {
            for (SincronizacionDtos.Operation operation : request.operaciones()) {
                String accion = operation.accion().trim().toUpperCase();
                switch (accion) {
                    case "ADD", "UPDATE", "UPSERT" -> {
                        Producto producto;
                        boolean nuevo;
                        if (operation.productoId() == null) {
                            producto = productoRepository
                                .findFirstBySucursalIgnoreCaseAndNombreIgnoreCase(
                                    sucursal, operation.nombre())
                                .orElseGet(Producto::new);
                            nuevo = producto.getId() == null;
                            if (nuevo) {
                                producto.setId(UUID.randomUUID().toString());
                            }
                        } else {
                            producto = productoRepository.findById(operation.productoId())
                                .orElseGet(Producto::new);
                            nuevo = producto.getId() == null;
                            producto.setId(operation.productoId());
                        }
                        producto.setNombre(operation.nombre().trim());
                        producto.setCategoria(operation.categoria() == null || operation.categoria().isBlank()
                            ? "General" : operation.categoria().trim());
                        producto.setPrecio(operation.precio());
                        producto.setCantidadDisponible(operation.cantidadDisponible());
                        producto.setStockMinimo(operation.stockMinimo());
                        producto.setSucursal(sucursal);
                        producto.setImagenUri(operation.imagenUri());
                        productoRepository.save(producto);
                        if (nuevo) {
                            registrarMovimiento(producto.getId(), "ENTRADA",
                                operation.cantidadDisponible());
                        }
                        aplicadas++;
                    }
                    case "SALE", "VENTA" -> {
                        Producto producto = productoRepository.findById(operation.productoId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
                        if (operation.cantidad() == null
                            || operation.cantidad() <= 0
                            || operation.cantidad() > producto.getCantidadDisponible()) {
                            throw new IllegalArgumentException("Stock insuficiente");
                        }
                        producto.setCantidadDisponible(
                            producto.getCantidadDisponible() - operation.cantidad());
                        productoRepository.save(producto);
                        registrarMovimiento(producto.getId(), "VENTA", operation.cantidad());
                        aplicadas++;
                    }
                    case "DELETE", "ELIMINAR" -> {
                        Producto producto = productoRepository.findById(operation.productoId())
                            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
                        movimientoRepository.deleteAll(
                            movimientoRepository.findByProductoIdOrderByFechaDesc(operation.productoId()));
                        productoRepository.delete(producto);
                        aplicadas++;
                    }
                    default -> throw new IllegalArgumentException(
                        "Acción de sincronización no válida: " + accion);
                }
            }
        }

        OffsetDateTime ahora = OffsetDateTime.now();
        SincronizacionSucursal sync = repository.findById(sucursal)
            .orElseGet(() -> nueva(sucursal));
        sync.setUltimaSincronizacion(ahora);
        sync.setCambiosReales(aplicadas > 0);
        sync.setEstado("DISPONIBLE");
        sync.setUpdatedAt(ahora);
        repository.save(sync);

        return new SincronizacionDtos.BatchResponse(
            sucursal, recibidas, aplicadas, ahora);
    }

    private void registrarMovimiento(String productoId, String tipo, int cantidad) {
        Movimiento movimiento = new Movimiento();
        movimiento.setId(UUID.randomUUID().toString());
        movimiento.setProductoId(productoId);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimientoRepository.save(movimiento);
    }

    private SincronizacionSucursal nueva(String sucursal) {
        SincronizacionSucursal sync = new SincronizacionSucursal();
        sync.setSucursal(sucursal.trim());
        sync.setUltimaSincronizacion(null);
        sync.setCambiosReales(false);
        sync.setEstado("DISPONIBLE");
        sync.setUpdatedAt(OffsetDateTime.now());
        return sync;
    }

    private SincronizacionDtos.Response calcular(SincronizacionSucursal sync) {
        OffsetDateTime ahora = OffsetDateTime.now();
        if (sync.getUltimaSincronizacion() == null) {
            return SincronizacionDtos.Response.from(
                sync, "DISPONIBLE", "Sin registro de actualización", false);
        }

        Duration transcurrido = Duration.between(sync.getUltimaSincronizacion(), ahora);
        long minutos = Math.max(0L, transcurrido.toMinutes());
        long horas = Math.max(0L, transcurrido.toHours());
        boolean noDisponible = horas >= 744;
        String estado = noDisponible ? "NO_DISPONIBLE" : "DISPONIBLE";
        String texto;

        if (minutos < 1) {
            texto = "Hace menos de 1 minuto";
        } else if (minutos < 60) {
            texto = "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
        } else if (horas < 24) {
            texto = "Hace " + horas + (horas == 1 ? " hora" : " horas");
        } else {
            long dias = horas / 24;
            texto = "Hace " + dias + (dias == 1 ? " día" : " días");
        }

        return SincronizacionDtos.Response.from(
            sync, estado, texto, noDisponible);
    }
}
