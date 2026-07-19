package ec.edu.espe.master_gateway.contexts.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RevokedToken {

    private UUID id;
    private String token;
    private UUID userId;
    private LocalDateTime revokedAt;

    public RevokedToken() {}

    public RevokedToken(String token, UUID userId) {
        this.token = Objects.requireNonNull(token, "token no puede ser null");
        this.userId = Objects.requireNonNull(userId, "userId no puede ser null");
        this.revokedAt = LocalDateTime.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public LocalDateTime getRevokedAt() { return revokedAt; }
    public void setRevokedAt(LocalDateTime revokedAt) { this.revokedAt = revokedAt; }
}
