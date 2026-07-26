package ec.edu.espe.master_gateway.contexts.identity.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.PageResult;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ListUsersServiceTest {

    @Mock
    private UserRepositoryPort userRepository;
    @Mock
    private AuthorizationPort authorizationPort;

    private ListUsersService service;

    @BeforeEach
    void setUp() {
        service = new ListUsersService(userRepository, authorizationPort);
    }

    @Test
    void should_listActiveUsers() {
        var user = new User("jdoe", "jdoe@example.com", "hash", "John Doe");
        user.markAsPersisted(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), "admin", "admin");
        var pageResult = new PageResult<>(List.of(user), 1L, 0, 10);
        when(userRepository.findActivePage(0, 10)).thenReturn(pageResult);

        var result = service.execute(0, 10);

        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).username()).isEqualTo("jdoe");
        assertThat(result.totalElements()).isEqualTo(1);
    }

    @Test
    void should_clampPageToNonNegative() {
        when(userRepository.findActivePage(0, 10)).thenReturn(new PageResult<>(List.of(), 0L, 0, 10));

        var result = service.execute(-5, 10);

        assertThat(result.content()).isEmpty();
    }
}
