package ec.edu.espe.master_gateway.contexts.menu.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Nodo de menú del sistema de navegación.
 *
 * <p>Representa un elemento dentro de la estructura jerárquica del menú
 * de la aplicación, pudiendo ser un nodo raíz (sin padre), un nodo hoja
 * (con URL) o un nodo contenedor (con hijos pero sin URL). Soporta
 * reordenamiento mediante cambio de padre y validación de ciclos.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class MenuNode {

    private Long id;
    private final String nombre;
    private String url;
    private final Long moduleId;
    private Long parentId;
    private final int orden;
    private EstadoRegistro estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;

    public MenuNode(String nombre, Long moduleId, Long parentId, int orden) {
        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null");
        this.moduleId = Objects.requireNonNull(moduleId, "moduleId no puede ser null");
        this.parentId = parentId;
        this.orden = orden;
        this.estado = EstadoRegistro.ACTIVO;
    }

    public boolean isLeaf() {
        return url != null && !url.isBlank();
    }

    public boolean isRoot() {
        return parentId == null;
    }

    public void moveTo(Long newParentId) {
        if (newParentId != null && newParentId.equals(this.id)) {
            throw new IllegalArgumentException("Un nodo no puede ser padre de sí mismo");
        }
        this.parentId = newParentId;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void deactivate() {
        if (this.estado == EstadoRegistro.INACTIVO) {
            throw new IllegalStateException("El nodo ya está inactivo");
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

    public String getUrl() {
        return url;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public Long getParentId() {
        return parentId;
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
