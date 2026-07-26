package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.JpaAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "roles_permissions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_id", "permission"})
})
public class RolePermissionAssignmentJpaEntity extends JpaAuditableEntity {

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_roles_permissions_role"))
    private RoleJpaEntity role;

    @Enumerated(EnumType.STRING)
    @Column(name = "permission", nullable = false, length = 50)
    private Permission permission;

    @Column(name = "asignado_por", length = 100, nullable = false)
    private String assignedBy;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime assignedAt;

    public RolePermissionAssignmentJpaEntity() {
        // Required by JPA
    }

    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
    public Permission getPermission() { return permission; }
    public void setPermission(Permission permission) { this.permission = permission; }
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
