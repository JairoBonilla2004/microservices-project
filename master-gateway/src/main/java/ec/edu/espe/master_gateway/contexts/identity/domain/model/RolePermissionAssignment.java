package ec.edu.espe.master_gateway.contexts.identity.domain.model;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa la asignación de un permiso a un rol dentro del dominio de identidad.
 *
 * <p>Esta entidad vincula un permiso específico con un rol, registrando quién realizó
 * la asignación y en qué momento. Soporta la revocación de la asignación mediante
 * el cambio de su estado a inactivo, permitiendo la auditoría de las autorizaciones
 * concedidas en el sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class RolePermissionAssignment {

    private UUID id;
    private final UUID roleId;
    private final Permission permission;
    private final String assignedBy;
    private final LocalDateTime assignedAt;
    private EstadoRegistro estado;

    public RolePermissionAssignment(UUID roleId, Permission permission, String assignedBy) {
        this.roleId = Objects.requireNonNull(roleId, "roleId no puede ser null");
        this.permission = Objects.requireNonNull(permission, "permission no puede ser null");
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

    public void reactivate() {
        if (this.estado == EstadoRegistro.ACTIVO) {
            throw new IllegalStateException("La asignación ya está activa");
        }
        this.estado = EstadoRegistro.ACTIVO;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public void setEstado(EstadoRegistro estado) { this.estado = estado; }
    public UUID getRoleId() { return roleId; }
    public Permission getPermission() { return permission; }
    public String getAssignedBy() { return assignedBy; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public EstadoRegistro getEstado() { return estado; }
}
