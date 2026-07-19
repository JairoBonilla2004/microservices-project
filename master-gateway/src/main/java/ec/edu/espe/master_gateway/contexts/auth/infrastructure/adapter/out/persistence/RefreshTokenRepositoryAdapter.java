package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.RefreshTokenRepositoryPort;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.mapper.AuthMapper;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptador de salida que implementa {@link RefreshTokenRepositoryPort}
 * utilizando JPA como tecnología de persistencia.
 *
 * <p>Traduce las operaciones del dominio en llamadas al repositorio
 * JPA y al mapper correspondiente, manejando las excepciones de
 * integridad de datos y transformándolas en excepciones de dominio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepositoryPort {

    private final RefreshTokenJpaRepository jpaRepository;
    private final AuthMapper authMapper;

    public RefreshTokenRepositoryAdapter(RefreshTokenJpaRepository jpaRepository, AuthMapper authMapper) {
        this.jpaRepository = jpaRepository;
        this.authMapper = authMapper;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        try {
            RefreshTokenJpaEntity entity = authMapper.toJpaEntity(refreshToken);
            RefreshTokenJpaEntity saved = jpaRepository.save(entity);
            return authMapper.toDomainEntity(saved);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al guardar el refresh token", e);
        }
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        try {
            return jpaRepository.findByToken(token)
                    .map(authMapper::toDomainEntity);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al buscar el refresh token por token", e);
        }
    }

    @Override
    public void revokeByUserId(UUID userId) {
        try {
            jpaRepository.findByUserId(userId)
                    .forEach(jpaRepository::delete);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException("Error al revocar refresh tokens del usuario", e);
        }
    }
}
