package ec.edu.espe.master_gateway.contexts.module.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;

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

    private Long id;
    private final String nombre;
    private final String descripcion;
    private final String icono;
    private final int orden;
    private EstadoRegistro estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;

    public Module(String nombre, String descripcion, String icono, int orden) {
        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null");
        this.descripcion = Objects.requireNonNull(descripcion, "descripcion no puede ser null");
        this.icono = Objects.requireNonNull(icono, "icono no puede ser null");
        this.orden = orden;
        this.estado = EstadoRegistro.ACTIVO;
    }

    public void deactivate() {
        if (this.estado == EstadoRegistro.INACTIVO) {
            throw new IllegalStateException("El módulo ya está inactivo");
        }
        this.estado = EstadoRegistro.INACTIVO;
    }

    public boolean isActive() {
        return estado == EstadoRegistro.ACTIVO;
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
