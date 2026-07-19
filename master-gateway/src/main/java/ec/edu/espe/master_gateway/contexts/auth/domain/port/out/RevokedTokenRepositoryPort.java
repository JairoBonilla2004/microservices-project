package ec.edu.espe.master_gateway.contexts.auth.domain.port.out;

import ec.edu.espe.master_gateway.contexts.auth.domain.model.RevokedToken;

public interface RevokedTokenRepositoryPort {

    RevokedToken save(RevokedToken revokedToken);

    boolean existsByToken(String token);
}
