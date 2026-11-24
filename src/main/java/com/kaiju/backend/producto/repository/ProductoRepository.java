package com.kaiju.backend.producto.repository;

import com.kaiju.backend.producto.entity.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, String> {
    List<Producto> findBySucursalIgnoreCaseOrderByNombreAsc(String sucursal);

    @Query("select distinct p.sucursal from Producto p order by p.sucursal")
    List<String> listarSucursales();

    @Query("select p from Producto p where lower(p.sucursal) = lower(:sucursal) "
        + "and p.cantidadDisponible <= p.stockMinimo")
    List<Producto> listarStockBajoPorSucursal(@Param("sucursal") String sucursal);
}
