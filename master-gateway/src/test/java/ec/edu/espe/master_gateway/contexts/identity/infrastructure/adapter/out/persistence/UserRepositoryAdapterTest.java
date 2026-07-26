package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.mapper.IdentityMapper;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.PersistenceException;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository jpaRepository;

    @Mock
    private IdentityMapper mapper;

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(jpaRepository, mapper);
    }

    @Test
    void findById_shouldReturnUser_whenFound() {
        var id = UUID.randomUUID();
        var entity = new UserJpaEntity();
        var user = new User("testuser", "test@test.com", "hash", "Test User");

        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(user);

        var result = adapter.findById(id);

        assertThat(result).isPresent().contains(user);
    }

    @Test
    void findById_shouldReturnEmpty_whenNotFound() {
        var id = UUID.randomUUID();

        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = adapter.findById(id);

        assertThat(result).isEmpty();
    }

    @Test
    void findByUsername_shouldReturnUser_whenFound() {
        var username = "testuser";
        var entity = new UserJpaEntity();
        var user = new User(username, "test@test.com", "hash", "Test User");

        when(jpaRepository.findByUsername(username)).thenReturn(Optional.of(entity));
        when(mapper.toDomainEntity(entity)).thenReturn(user);

        var result = adapter.findByUsername(username);

        assertThat(result).isPresent().contains(user);
    }

    @Test
    void findByUsername_shouldReturnEmpty_whenNotFound() {
        var username = "testuser";

        when(jpaRepository.findByUsername(username)).thenReturn(Optional.empty());

        var result = adapter.findByUsername(username);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllActive_shouldReturnAllActiveUsers() {
        var entity1 = new UserJpaEntity();
        var entity2 = new UserJpaEntity();
        var user1 = new User("user1", "u1@test.com", "hash1", "User One");
        var user2 = new User("user2", "u2@test.com", "hash2", "User Two");

        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO)).thenReturn(List.of(entity1, entity2));
        lenient().when(mapper.toDomainEntity(entity1)).thenReturn(user1);
        lenient().when(mapper.toDomainEntity(entity2)).thenReturn(user2);

        var result = adapter.findAllActive();

        assertThat(result).containsExactly(user1, user2);
    }

    @Test
    void findAllActive_shouldReturnEmptyList_whenNoActiveUsers() {
        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO)).thenReturn(List.of());

        var result = adapter.findAllActive();

        assertThat(result).isEmpty();
    }

    @Test
    void findActivePage_shouldReturnPageResult() {
        var page = 0;
        var size = 10;
        var entity = new UserJpaEntity();
        var user = new User("testuser", "test@test.com", "hash", "Test User");
        var pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        var pageResult = new PageImpl<>(List.of(entity), pageable, 1);

        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO, pageable)).thenReturn(pageResult);
        lenient().when(mapper.toDomainEntity(entity)).thenReturn(user);

        var result = adapter.findActivePage(page, size);

        assertThat(result.content()).containsExactly(user);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.page()).isEqualTo(page);
        assertThat(result.size()).isEqualTo(size);
    }

    @Test
    void findActivePage_shouldReturnEmptyPage_whenNoResults() {
        var page = 0;
        var size = 10;
        var pageable = PageRequest.of(page, size, Sort.by("fechaCreacion").descending());
        var pageResult = new PageImpl<>(List.<UserJpaEntity>of(), pageable, 0);

        when(jpaRepository.findByEstado(EstadoRegistro.ACTIVO, pageable)).thenReturn(pageResult);

        var result = adapter.findActivePage(page, size);

        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isZero();
    }

    @Test
    void save_shouldPersistAndReturnUser() {
        var user = new User("testuser", "test@test.com", "hash", "Test User");
        var entity = new UserJpaEntity();
        var savedEntity = new UserJpaEntity();

        when(mapper.toJpaEntity(user)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenReturn(savedEntity);
        when(mapper.toDomainEntity(savedEntity)).thenReturn(user);

        var result = adapter.save(user);

        assertThat(result).isEqualTo(user);
    }

    @Test
    void save_shouldThrowDuplicateException_whenUsernameExists() {
        var user = new User("testuser", "test@test.com", "hash", "Test User");
        var entity = new UserJpaEntity();

        when(mapper.toJpaEntity(user)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenThrow(new DataIntegrityViolationException("duplicate"));
        when(jpaRepository.existsByUsername(user.getUsername())).thenReturn(true);

        var exception = assertThrows(DuplicateException.class, () -> adapter.save(user));

        assertThat(exception.getMessage()).contains("username");
    }

    @Test
    void save_shouldThrowDuplicateException_whenEmailExists() {
        var user = new User("testuser", "test@test.com", "hash", "Test User");
        var entity = new UserJpaEntity();

        when(mapper.toJpaEntity(user)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenThrow(new DataIntegrityViolationException("duplicate"));
        when(jpaRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(jpaRepository.existsByEmail(user.getEmail())).thenReturn(true);

        var exception = assertThrows(DuplicateException.class, () -> adapter.save(user));

        assertThat(exception.getMessage()).contains("email");
    }

    @Test
    void save_shouldThrowPersistenceException_whenUnknownConstraint() {
        var user = new User("testuser", "test@test.com", "hash", "Test User");
        var entity = new UserJpaEntity();

        when(mapper.toJpaEntity(user)).thenReturn(entity);
        when(jpaRepository.save(entity)).thenThrow(new DataIntegrityViolationException("error"));
        when(jpaRepository.existsByUsername(user.getUsername())).thenReturn(false);
        when(jpaRepository.existsByEmail(user.getEmail())).thenReturn(false);

        var exception = assertThrows(PersistenceException.class, () -> adapter.save(user));

        assertThat(exception.getMessage()).contains("Error al guardar el usuario");
    }

    @Test
    void existsByUsername_shouldReturnTrue_whenExists() {
        var username = "testuser";

        when(jpaRepository.existsByUsername(username)).thenReturn(true);

        assertThat(adapter.existsByUsername(username)).isTrue();
    }

    @Test
    void existsByUsername_shouldReturnFalse_whenNotExists() {
        var username = "testuser";

        when(jpaRepository.existsByUsername(username)).thenReturn(false);

        assertThat(adapter.existsByUsername(username)).isFalse();
    }

    @Test
    void existsByEmail_shouldReturnTrue_whenExists() {
        var email = "test@test.com";

        when(jpaRepository.existsByEmail(email)).thenReturn(true);

        assertThat(adapter.existsByEmail(email)).isTrue();
    }

    @Test
    void existsByEmail_shouldReturnFalse_whenNotExists() {
        var email = "test@test.com";

        when(jpaRepository.existsByEmail(email)).thenReturn(false);

        assertThat(adapter.existsByEmail(email)).isFalse();
    }
}
