package ec.edu.espe.master_gateway.contexts.identity.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa la asignación de un rol a un usuario dentro del dominio de identidad.
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */

public class UserRoleAssignment {

    private UUID id;
    private final UUID userId;
    private final UUID roleId;
    private final String assignedBy;
    private final LocalDateTime assignedAt;
    private EstadoRegistro estado;

    public UserRoleAssignment(UUID userId, UUID roleId, String assignedBy) {
        this.userId = Objects.requireNonNull(userId, "userId no puede ser null");
        this.roleId = Objects.requireNonNull(roleId, "roleId no puede ser null");
        this.assignedBy = assignedBy;
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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID getRoleId() {
        return roleId;
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
