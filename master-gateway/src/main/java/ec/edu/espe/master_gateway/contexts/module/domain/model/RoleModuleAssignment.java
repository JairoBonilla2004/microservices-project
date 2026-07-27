package ec.edu.espe.master_gateway.contexts.module.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.UUID;

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

    private UUID id;
    private final UUID roleId;
    private final UUID moduleId;
    private final String assignedBy;
    private final LocalDateTime assignedAt;
    private EstadoRegistro estado;

    public RoleModuleAssignment(UUID roleId, UUID moduleId, String assignedBy) {
        this.roleId = Objects.requireNonNull(roleId, "roleId no puede ser null");
        this.moduleId = Objects.requireNonNull(moduleId, "moduleId no puede ser null");
        this.assignedBy = Objects.requireNonNull(assignedBy, "assignedBy no puede ser null");
        this.assignedAt = LocalDateTime.now(ZoneOffset.UTC);
        this.estado = EstadoRegistro.ACTIVO;
    }

    public void revoke() {
        if (this.estado == EstadoRegistro.INACTIVO) {
            throw new IllegalStateException("La asignación ya está revocada");
        }
        this.estado = EstadoRegistro.INACTIVO;
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

    public UUID getRoleId() {
        return roleId;
    }

    public UUID getModuleId() {
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
