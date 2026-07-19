package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.UUID;

public interface RevokedTokenJpaRepository extends SoftDeleteRepository<RevokedTokenJpaEntity, UUID> {

    boolean existsByToken(String token);
}
