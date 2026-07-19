package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.RoleJpaEntity;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.UserJpaEntity;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.JpaAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
public class RefreshTokenJpaEntity extends JpaAuditableEntity {

    @Column(nullable = false, unique = true, length = 1024)
    private String token;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_refresh_tokens_user"))
    private UserJpaEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_refresh_tokens_role"))
    private RoleJpaEntity role;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean revoked;

    public RefreshTokenJpaEntity() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public UUID getRoleId() { return roleId; }
    public void setRoleId(UUID roleId) { this.roleId = roleId; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }
}
