package ec.edu.espe.master_gateway.contexts.module.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Asignación de un módulo a un rol del sistema.
 *
 * <p>Relaciona un rol con un módulo, indicando qué roles tienen acceso
 * a cada módulo funcional. Soporta revocación lógica cambiando el estado
 * a {@link EstadoRegistro#INACTIVO}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class RoleModuleAssignment {

    private Long id;
    private final Long roleId;
    private final Long moduleId;
    private final String assignedBy;
    private final LocalDateTime assignedAt;
    private EstadoRegistro estado;

    public RoleModuleAssignment(Long roleId, Long moduleId, String assignedBy) {
        this.roleId = Objects.requireNonNull(roleId, "roleId no puede ser null");
        this.moduleId = Objects.requireNonNull(moduleId, "moduleId no puede ser null");
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

    public Long getModuleId() {
        return moduleId;
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
