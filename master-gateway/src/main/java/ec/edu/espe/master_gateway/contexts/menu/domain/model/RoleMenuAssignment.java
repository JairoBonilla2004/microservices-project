package ec.edu.espe.master_gateway.contexts.menu.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Asignación de un nodo de menú a un rol del sistema.
 *
 * <p>Relaciona un rol con un nodo de menú, determinando qué elementos
 * de navegación son visibles para cada rol. Soporta revocación lógica
 * mediante {@link EstadoRegistro#INACTIVO}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class RoleMenuAssignment {

    private Long id;
    private final Long roleId;
    private final Long menuNodeId;
    private final String assignedBy;
    private final LocalDateTime assignedAt;
    private EstadoRegistro estado;

    public RoleMenuAssignment(Long roleId, Long menuNodeId, String assignedBy) {
        this.roleId = Objects.requireNonNull(roleId, "roleId no puede ser null");
        this.menuNodeId = Objects.requireNonNull(menuNodeId, "menuNodeId no puede ser null");
        this.assignedBy = Objects.requireNonNull(assignedBy, "assignedBy no puede ser null");
        this.assignedAt = LocalDateTime.now();
        this.estado = EstadoRegistro.ACTIVO;
    }

    public void revoke() {
        if (this.estado == EstadoRegistro.INACTIVO) {
            throw new IllegalStateException("La asignación ya está revocada");
        }
        this.estado = EstadoRegistro.INACTIVO;
    }

    public boolean isActive() {
        return estado == EstadoRegistro.ACTIVO;
    }

    public Long getId() {
        return id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public Long getMenuNodeId() {
        return menuNodeId;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }
}
