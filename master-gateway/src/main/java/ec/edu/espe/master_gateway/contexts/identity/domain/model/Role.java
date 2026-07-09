package ec.edu.espe.master_gateway.contexts.identity.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un rol dentro del dominio de identidad del sistema.
 *
 * <p>Un rol define un conjunto de responsabilidades y permisos que pueden ser
 * asignados a los usuarios para controlar el acceso a los diferentes módulos
 * y funcionalidades de la aplicación. Además de su información descriptiva,
 * mantiene su estado de activación y los datos de auditoría asociados a su
 * persistencia.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class Role {

    private Long id;
    private final String nombre;
    private final String descripcion;
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

    public boolean isActive() {
        return estado == EstadoRegistro.ACTIVO;
    }

    public void markAsPersisted(Long id, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                                String creadoPor, String actualizadoPor) {
        if (this.id != null) {
            throw new IllegalStateException("El rol ya fue persistido");
        }
        this.id = Objects.requireNonNull(id);
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion);
        this.fechaActualizacion = Objects.requireNonNull(fechaActualizacion);
        this.creadoPor = creadoPor;
        this.actualizadoPor = actualizadoPor;
    }

    public Long getId() {
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

    public String getCreadoPor() {
        return creadoPor;
    }

    public String getActualizadoPor() {
        return actualizadoPor;
    }
}
