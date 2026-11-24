package com.kaiju.backend.sincronizacion.repository;

import com.kaiju.backend.sincronizacion.entity.SincronizacionSucursal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SincronizacionSucursalRepository
    extends JpaRepository<SincronizacionSucursal, String> {
}
