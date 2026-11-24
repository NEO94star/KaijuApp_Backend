package com.kaiju.backend.movimiento.repository;

import com.kaiju.backend.movimiento.entity.Movimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimientoRepository extends JpaRepository<Movimiento, String> {
    List<Movimiento> findByProductoIdOrderByFechaDesc(String productoId);
}
