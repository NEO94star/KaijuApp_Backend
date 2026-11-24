package com.kaiju.backend.producto.service;

import com.kaiju.backend.movimiento.entity.Movimiento;
import com.kaiju.backend.movimiento.repository.MovimientoRepository;
import com.kaiju.backend.producto.dto.ProductoDtos;
import com.kaiju.backend.producto.entity.Producto;
import com.kaiju.backend.producto.repository.ProductoRepository;
import com.kaiju.backend.sincronizacion.entity.SincronizacionSucursal;
import com.kaiju.backend.sincronizacion.repository.SincronizacionSucursalRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.time.OffsetDateTime;

@Service
public class ProductoService {
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final SincronizacionSucursalRepository sincronizacionRepository;

    public ProductoService(ProductoRepository productoRepository,
                           MovimientoRepository movimientoRepository,
                           SincronizacionSucursalRepository sincronizacionRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.sincronizacionRepository = sincronizacionRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductoDtos.Response> listar() {
        return productoRepository.findAll().stream()
            .sorted((a, b) -> a.getNombre().compareToIgnoreCase(b.getNombre()))
            .map(ProductoDtos.Response::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductoDtos.Response> listarPorSucursal(String sucursal) {
        return productoRepository.findBySucursalIgnoreCaseOrderByNombreAsc(sucursal).stream()
            .map(ProductoDtos.Response::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<String> listarSucursales() {
        return productoRepository.listarSucursales();
    }

    @Transactional(readOnly = true)
    public List<ProductoDtos.Response> listarStockBajoPorSucursal(String sucursal) {
        return productoRepository.listarStockBajoPorSucursal(sucursal).stream()
            .map(ProductoDtos.Response::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public ProductoDtos.Response obtener(String id) {
        return ProductoDtos.Response.from(buscar(id));
    }

    @Transactional
    public ProductoDtos.Response crear(ProductoDtos.CreateRequest request) {
        Producto producto = new Producto();
        producto.setId(UUID.randomUUID().toString());
        aplicarDatos(producto, request.nombre(), request.categoria(), request.precio(),
            request.cantidadDisponible(), request.stockMinimo(), request.sucursal(), request.imagenUri());
        productoRepository.save(producto);
        registrarMovimiento(producto.getId(), "ENTRADA", producto.getCantidadDisponible());
        marcarCambioSucursal(producto.getSucursal());
        return ProductoDtos.Response.from(producto);
    }

    @Transactional
    public ProductoDtos.Response actualizar(String id, ProductoDtos.UpdateRequest request) {
        Producto producto = buscar(id);
        aplicarDatos(producto, request.nombre(), request.categoria(), request.precio(),
            request.cantidadDisponible(), request.stockMinimo(), request.sucursal(), request.imagenUri());
        Producto actualizado = productoRepository.save(producto);
        marcarCambioSucursal(actualizado.getSucursal());
        return ProductoDtos.Response.from(actualizado);
    }

    @Transactional
    public void eliminar(String id) {
        Producto producto = buscar(id);
        List<Movimiento> movimientos = movimientoRepository.findByProductoIdOrderByFechaDesc(id);
        movimientoRepository.deleteAll(movimientos);
        productoRepository.delete(producto);
    }

    @Transactional
    public ProductoDtos.Response vender(String id, ProductoDtos.SaleRequest request) {
        Producto producto = buscar(id);
        int cantidad = request.cantidad();
        if (cantidad > producto.getCantidadDisponible()) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Stock insuficiente (Disponible: " + producto.getCantidadDisponible() + ")"
            );
        }
        producto.setCantidadDisponible(producto.getCantidadDisponible() - cantidad);
        productoRepository.save(producto);
        registrarMovimiento(id, "VENTA", cantidad);
        marcarCambioSucursal(producto.getSucursal());
        return ProductoDtos.Response.from(producto);
    }

    private Producto buscar(String id) {
        return productoRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Producto no encontrado"));
    }

    private void aplicarDatos(Producto producto, String nombre, String categoria,
                              java.math.BigDecimal precio, Integer cantidadDisponible,
                              Integer stockMinimo, String sucursal, String imagenUri) {
        producto.setNombre(nombre.trim());
        producto.setCategoria(categoria == null || categoria.trim().isBlank()
            ? "General" : categoria.trim());
        producto.setPrecio(precio);
        producto.setCantidadDisponible(cantidadDisponible);
        producto.setStockMinimo(stockMinimo);
        producto.setSucursal(sucursal.trim());
        producto.setImagenUri(imagenUri);
    }

    private void registrarMovimiento(String productoId, String tipo, int cantidad) {
        Movimiento movimiento = new Movimiento();
        movimiento.setId(UUID.randomUUID().toString());
        movimiento.setProductoId(productoId);
        movimiento.setTipo(tipo);
        movimiento.setCantidad(cantidad);
        movimientoRepository.save(movimiento);
    }

    private void marcarCambioSucursal(String sucursal) {
        SincronizacionSucursal sync = sincronizacionRepository.findById(sucursal)
            .orElseGet(() -> {
                SincronizacionSucursal nueva = new SincronizacionSucursal();
                nueva.setSucursal(sucursal);
                nueva.setEstado("DISPONIBLE");
                nueva.setUpdatedAt(OffsetDateTime.now());
                return nueva;
            });
        sync.setCambiosReales(true);
        sync.setUltimaSincronizacion(OffsetDateTime.now());
        sync.setEstado("DISPONIBLE");
        sync.setUpdatedAt(OffsetDateTime.now());
        sincronizacionRepository.save(sync);
    }
}
