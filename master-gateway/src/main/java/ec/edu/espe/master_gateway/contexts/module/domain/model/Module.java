package ec.edu.espe.master_gateway.contexts.module.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Módulo funcional del sistema de autorización.
 *
 * <p>Representa un módulo o agrupación de funcionalidades dentro del
 * sistema. Los módulos son asignados a roles mediante
 * {@link RoleModuleAssignment} y contienen nodos de menú que definen
 * la navegación disponible para cada usuario.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class Module {

    private UUID id;
    private String nombre;
    private String descripcion;
    private String icono;
    private int orden;
    private EstadoRegistro estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;

    public Module(String nombre, String descripcion, String icono, int orden) {
        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null");
        this.descripcion = Objects.requireNonNull(descripcion, "descripcion no puede ser null");
        this.icono = icono != null ? icono : "default";
        this.orden = orden;
        this.estado = EstadoRegistro.ACTIVO;
    }

    public void deactivate() {
        if (this.estado == EstadoRegistro.INACTIVO) {
            throw new IllegalStateException("El módulo ya está inactivo");
        }
        this.estado = EstadoRegistro.INACTIVO;
    }

    public void reactivate() {
        if (this.estado == EstadoRegistro.ACTIVO) {
            throw new IllegalStateException("El módulo ya está activo");
        }
        this.estado = EstadoRegistro.ACTIVO;
    }

    public void updateNombre(String nombre) {
        if (nombre != null) this.nombre = nombre;
    }

    public void updateDescripcion(String descripcion) {
        if (descripcion != null) this.descripcion = descripcion;
    }

    public void updateIcono(String icono) {
        if (icono != null) this.icono = icono;
    }

    public void updateOrden(int orden) {
        this.orden = orden;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public void setActualizadoPor(String actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public int getOrden() {
        return orden;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public String getActualizadoPor() {
        return actualizadoPor;
    }

}
