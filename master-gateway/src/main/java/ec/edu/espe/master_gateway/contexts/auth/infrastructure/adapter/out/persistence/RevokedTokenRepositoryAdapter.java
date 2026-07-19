package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.auth.domain.model.RevokedToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RevokedTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.mapper.AuthMapper;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Component
public class RevokedTokenRepositoryAdapter implements RevokedTokenRepositoryPort {

    private final RevokedTokenJpaRepository jpaRepository;
    private final AuthMapper authMapper;

    public RevokedTokenRepositoryAdapter(RevokedTokenJpaRepository jpaRepository, AuthMapper authMapper) {
        this.jpaRepository = jpaRepository;
        this.authMapper = authMapper;
    }

    @Override
    public RevokedToken save(RevokedToken revokedToken) {
        try {
            RevokedTokenJpaEntity entity = authMapper.toJpaEntity(revokedToken);
            RevokedTokenJpaEntity saved = jpaRepository.save(entity);
            return authMapper.toDomainEntity(saved);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al guardar el token revocado", e);
        }
    }

    @Override
    public boolean existsByToken(String token) {
        try {
            return jpaRepository.existsByToken(token);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al verificar token revocado", e);
        }
    }
}
