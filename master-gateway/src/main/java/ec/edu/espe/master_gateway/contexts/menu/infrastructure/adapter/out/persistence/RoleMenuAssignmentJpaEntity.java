package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence;

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
@Table(name = "roles_menus", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"role_id", "menu_node_id"})
})
public class RoleMenuAssignmentJpaEntity extends JpaAuditableEntity {

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "menu_node_id", nullable = false)
    private UUID menuNodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_roles_menus_role"))
    private RoleJpaEntity role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_node_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_roles_menus_menu"))
    private MenuNodeJpaEntity menuNode;

    @Column(name = "asignado_por", length = 100, nullable = false)
    private String assignedBy;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime assignedAt;

    public RoleMenuAssignmentJpaEntity() {}

    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
    public UUID getMenuNodeId() { return menuNodeId; }
    public void setMenuNodeId(UUID menuNodeId) { this.menuNodeId = menuNodeId; }
    public String getAssignedBy() { return assignedBy; }
    public void setAssignedBy(String assignedBy) { this.assignedBy = assignedBy; }
    public LocalDateTime getAssignedAt() { return assignedAt; }
    public void setAssignedAt(LocalDateTime assignedAt) { this.assignedAt = assignedAt; }
}
