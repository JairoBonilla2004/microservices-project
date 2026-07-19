package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends SoftDeleteRepository<RefreshTokenJpaEntity, UUID> {

    Optional<RefreshTokenJpaEntity> findByToken(String token);

    java.util.List<RefreshTokenJpaEntity> findByUserId(UUID userId);
}
