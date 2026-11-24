package com.kaiju.backend.sincronizacion.entity;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sincronizacion_sucursal")
public class SincronizacionSucursal {
    @Id
    private String sucursal;

    @Column(name = "ultima_sincronizacion")
    private OffsetDateTime ultimaSincronizacion;

    @Column(name = "cambios_reales", nullable = false)
    private boolean cambiosReales;

    @Column(nullable = false, length = 20)
    private String estado;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    public String getSucursal() { return sucursal; }
    public void setSucursal(String sucursal) { this.sucursal = sucursal; }
    public OffsetDateTime getUltimaSincronizacion() { return ultimaSincronizacion; }
    public void setUltimaSincronizacion(OffsetDateTime ultimaSincronizacion) { this.ultimaSincronizacion = ultimaSincronizacion; }
    public boolean isCambiosReales() { return cambiosReales; }
    public void setCambiosReales(boolean cambiosReales) { this.cambiosReales = cambiosReales; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
