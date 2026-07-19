package ec.edu.espe.master_gateway.contexts.identity.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa un rol dentro del dominio de identidad.
 *
 * <p>Un rol define un conjunto de permisos y responsabilidades dentro del sistema.
 * Permite su activación, desactivación y actualización de nombre y descripción,
 * además de gestionar la información de auditoría generada durante su persistencia.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class Role {

    private UUID id;
    private String nombre;
    private String descripcion;
    private EstadoRegistro estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;

    public Role(String nombre, String descripcion) {
        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null");
        this.descripcion = Objects.requireNonNull(descripcion, "descripcion no puede ser null");
        this.estado = EstadoRegistro.ACTIVO;
    }

    public void deactivate() {
        if (this.estado == EstadoRegistro.INACTIVO) {
            throw new IllegalStateException("El rol ya está inactivo");
        }
        this.estado = EstadoRegistro.INACTIVO;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    public void updateNombre(String nombre) {
        if (nombre != null) this.nombre = nombre;
    }

    public void updateDescripcion(String descripcion) {
        if (descripcion != null) this.descripcion = descripcion;
    }

    public void markAsPersisted(UUID id, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                                String creadoPor, String actualizadoPor) {
        if (this.id != null) {
            throw new IllegalStateException("El rol ya fue persistido");
        }
        this.id = Objects.requireNonNull(id);
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.creadoPor = creadoPor;
        this.actualizadoPor = actualizadoPor;
    }

    public UUID getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
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

}
