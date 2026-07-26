package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.auth.domain.model.RefreshToken;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.mapper.AuthMapper;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class RefreshTokenRepositoryAdapterTest {

    @Mock
    private RefreshTokenJpaRepository jpaRepository;

    @Mock
    private AuthMapper authMapper;

    private RefreshTokenRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenRepositoryAdapter(jpaRepository, authMapper);
    }

    @Test
    void save_shouldPersistAndReturnRefreshToken() {
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();
        var refreshToken = new RefreshToken("token-value", userId, roleId, LocalDateTime.now().plusDays(1));
        var entity = new RefreshTokenJpaEntity();
        var savedEntity = new RefreshTokenJpaEntity();

        when(authMapper.toJpaEntity(refreshToken)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(authMapper.toDomainEntity(savedEntity)).thenReturn(refreshToken);

        var result = adapter.save(refreshToken);

        assertThat(result).isEqualTo(refreshToken);
    }

    @Test
    void save_shouldThrowPersistenceException_onDataIntegrity() {
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();
        var refreshToken = new RefreshToken("token-value", userId, roleId, LocalDateTime.now().plusDays(1));
        var entity = new RefreshTokenJpaEntity();

        when(authMapper.toJpaEntity(refreshToken)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenThrow(new DataIntegrityViolationException("error"));

        var exception = assertThrows(PersistenceException.class, () -> adapter.save(refreshToken));

        assertThat(exception.getMessage()).contains("Error al guardar el refresh token");
    }

    @Test
    void findByToken_shouldReturnRefreshToken_whenFound() {
        var token = "token-value";
        var entity = new RefreshTokenJpaEntity();
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();
        var refreshToken = new RefreshToken(token, userId, roleId, LocalDateTime.now().plusDays(1));

        when(jpaRepository.findByToken(token)).thenReturn(Optional.of(entity));
        when(authMapper.toDomainEntity(entity)).thenReturn(refreshToken);

        var result = adapter.findByToken(token);

        assertThat(result).isPresent().contains(refreshToken);
    }

    @Test
    void findByToken_shouldReturnEmpty_whenNotFound() {
        var token = "token-value";

        when(jpaRepository.findByToken(token)).thenReturn(Optional.empty());

        var result = adapter.findByToken(token);

        assertThat(result).isEmpty();
    }

    @Test
    void findByToken_shouldThrowPersistenceException_onDataIntegrity() {
        var token = "token-value";

        when(jpaRepository.findByToken(token)).thenThrow(new DataIntegrityViolationException("error"));

        var exception = assertThrows(PersistenceException.class, () -> adapter.findByToken(token));

        assertThat(exception.getMessage()).contains("Error al buscar el refresh token por token");
    }

    @Test
    void revokeByUserId_shouldDeleteAllTokensForUser() {
        var userId = UUID.randomUUID();
        var entity1 = new RefreshTokenJpaEntity();
        var entity2 = new RefreshTokenJpaEntity();

        when(jpaRepository.findByUserId(userId)).thenReturn(List.of(entity1, entity2));

        adapter.revokeByUserId(userId);

        verify(jpaRepository).delete(entity1);
        verify(jpaRepository).delete(entity2);
    }

    @Test
    void revokeByUserId_shouldDoNothing_whenNoTokens() {
        var userId = UUID.randomUUID();

        when(jpaRepository.findByUserId(userId)).thenReturn(List.of());

        adapter.revokeByUserId(userId);

        verify(jpaRepository).findByUserId(userId);
    }

    @Test
    void revokeByUserId_shouldThrowPersistenceException_onDataIntegrity() {
        var userId = UUID.randomUUID();

        when(jpaRepository.findByUserId(userId)).thenThrow(new DataIntegrityViolationException("error"));

        var exception = assertThrows(PersistenceException.class, () -> adapter.revokeByUserId(userId));

        assertThat(exception.getMessage()).contains("Error al revocar refresh tokens del usuario");
    }
}
