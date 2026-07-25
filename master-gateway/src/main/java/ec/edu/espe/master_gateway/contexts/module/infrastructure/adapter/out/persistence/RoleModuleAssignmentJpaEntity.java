package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.RoleJpaEntity;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.JpaAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "roles_modules", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_id", "module_id"})
})
public class RoleModuleAssignmentJpaEntity extends JpaAuditableEntity {

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "module_id", nullable = false)
    private UUID moduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_roles_modules_role"))
    private RoleJpaEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_roles_modules_module"))
    private ModuleJpaEntity module;

    @Column(name = "asignado_por", length = 100, nullable = false)
    private String assignedBy;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime assignedAt;

    public RoleModuleAssignmentJpaEntity() {
        // Required by JPA
    }

    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
    public UUID getModuleId() { return moduleId; }
    public void setModuleId(UUID moduleId) { this.moduleId = moduleId; }
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
