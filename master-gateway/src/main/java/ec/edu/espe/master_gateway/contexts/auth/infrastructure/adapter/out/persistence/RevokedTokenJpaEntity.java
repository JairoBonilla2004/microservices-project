package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence;

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
@Table(name = "revoked_tokens")
public class RevokedTokenJpaEntity extends JpaAuditableEntity {

    @Column(nullable = false, unique = true, columnDefinition = "TEXT")
    private String token;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id",
                insertable = false, updatable = false,
                foreignKey = @ForeignKey(name = "fk_revoked_tokens_user"))
    private UserJpaEntity user;

    @Column(name = "revoked_at", nullable = false)
    private LocalDateTime revokedAt;

    public RevokedTokenJpaEntity() {}

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
}
