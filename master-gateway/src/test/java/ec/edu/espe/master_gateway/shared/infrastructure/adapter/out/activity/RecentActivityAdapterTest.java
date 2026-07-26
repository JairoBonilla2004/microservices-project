package ec.edu.espe.master_gateway.shared.infrastructure.adapter.out.activity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.RoleJpaRepository;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.UserJpaRepository;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.UserJpaEntity;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence.MenuNodeJpaRepository;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence.MenuNodeJpaEntity;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence.ModuleJpaRepository;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence.ModuleJpaEntity;
import ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.out.persistence.RegisteredServiceJpaRepository;
import ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.out.persistence.RegisteredServiceJpaEntity;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RecentActivityAdapterTest {

    @Mock
    private UserJpaRepository userJpaRepository;
    @Mock
    private RoleJpaRepository roleJpaRepository;
    @Mock
    private ModuleJpaRepository moduleJpaRepository;
    @Mock
    private MenuNodeJpaRepository menuNodeJpaRepository;
    @Mock
    private RegisteredServiceJpaRepository registeredServiceJpaRepository;

    private RecentActivityAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new RecentActivityAdapter(userJpaRepository, roleJpaRepository,
                moduleJpaRepository, menuNodeJpaRepository, registeredServiceJpaRepository);
    }

    @Test
    void should_findRecentUsers() {
        var entity = createMockUser("John Doe", "admin");
        when(userJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity));

        var result = adapter.findRecentUsers(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("John Doe");
        assertThat(result.get(0).actor()).isEqualTo("admin");
    }

    @Test
    void should_findRecentUsers_withNullNombreCompleto() {
        var entity = createMockUser(null, "admin");
        when(entity.getUsername()).thenReturn("jdoe");
        when(userJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity));

        var result = adapter.findRecentUsers(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("jdoe");
    }

    @Test
    void should_findRecentRoles() {
        var entity = createMockRole("ADMIN");
        when(roleJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity));

        var result = adapter.findRecentRoles(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("ADMIN");
    }

    @Test
    void should_findRecentModules() {
        var entity = createMockModule("Users");
        when(moduleJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity));

        var result = adapter.findRecentModules(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Users");
    }

    @Test
    void should_findRecentMenuItems() {
        var entity = createMockMenu("Dashboard");
        when(menuNodeJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity));

        var result = adapter.findRecentMenuItems(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Dashboard");
    }

    @Test
    void should_findRecentServices() {
        var entity = createMockService("Gateway");
        when(registeredServiceJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity));

        var result = adapter.findRecentServices(10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Gateway");
    }

    @Test
    void should_useCreadoPor_whenActualizadoPorIsNull() {
        var entity = createMockUser("John Doe", null);
        when(entity.getCreadoPor()).thenReturn("creator");
        when(userJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity));

        var result = adapter.findRecentUsers(10);

        assertThat(result.get(0).actor()).isEqualTo("creator");
    }

    @Test
    void should_limitResults() {
        var entity1 = createMockUser("U1", "admin");
        var entity2 = createMockUser("U2", "admin");
        var entity3 = createMockUser("U3", "admin");
        when(userJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity1, entity2, entity3));

        var result = adapter.findRecentUsers(2);

        assertThat(result).hasSize(2);
    }

    @Test
    void should_returnAll_when_limitIsGreaterThanSize() {
        var entity1 = createMockUser("U1", "admin");
        var entity2 = createMockUser("U2", "admin");
        when(userJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity1, entity2));

        var result = adapter.findRecentUsers(10);

        assertThat(result).hasSize(2);
    }

    @Test
    void should_returnAll_when_limitIsZero() {
        var entity = createMockUser("U1", "admin");
        when(userJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO))
                .thenReturn(List.of(entity));

        var result = adapter.findRecentUsers(0);

        assertThat(result).hasSize(1);
    }

    private UserJpaEntity createMockUser(String nombreCompleto, String actualizadoPor) {
        var entity = mock(UserJpaEntity.class);
        lenient().when(entity.getNombreCompleto()).thenReturn(nombreCompleto);
        lenient().when(entity.getActualizadoPor()).thenReturn(actualizadoPor);
        lenient().when(entity.getFechaCreacion()).thenReturn(LocalDateTime.now());
        lenient().when(entity.getFechaActualizacion()).thenReturn(LocalDateTime.now());
        return entity;
    }

    private ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.RoleJpaEntity createMockRole(String nombre) {
        var entity = mock(ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.RoleJpaEntity.class);
        lenient().when(entity.getNombre()).thenReturn(nombre);
        lenient().when(entity.getActualizadoPor()).thenReturn("admin");
        lenient().when(entity.getCreadoPor()).thenReturn("admin");
        lenient().when(entity.getFechaCreacion()).thenReturn(LocalDateTime.now());
        lenient().when(entity.getFechaActualizacion()).thenReturn(LocalDateTime.now());
        return entity;
    }

    private ModuleJpaEntity createMockModule(String nombre) {
        var entity = mock(ModuleJpaEntity.class);
        lenient().when(entity.getNombre()).thenReturn(nombre);
        lenient().when(entity.getActualizadoPor()).thenReturn("admin");
        lenient().when(entity.getCreadoPor()).thenReturn("admin");
        lenient().when(entity.getFechaCreacion()).thenReturn(LocalDateTime.now());
        lenient().when(entity.getFechaActualizacion()).thenReturn(LocalDateTime.now());
        return entity;
    }

    private MenuNodeJpaEntity createMockMenu(String nombre) {
        var entity = mock(MenuNodeJpaEntity.class);
        lenient().when(entity.getNombre()).thenReturn(nombre);
        lenient().when(entity.getActualizadoPor()).thenReturn("admin");
        lenient().when(entity.getCreadoPor()).thenReturn("admin");
        lenient().when(entity.getFechaCreacion()).thenReturn(LocalDateTime.now());
        lenient().when(entity.getFechaActualizacion()).thenReturn(LocalDateTime.now());
        return entity;
    }

    private RegisteredServiceJpaEntity createMockService(String nombre) {
        var entity = mock(RegisteredServiceJpaEntity.class);
        lenient().when(entity.getNombre()).thenReturn(nombre);
        lenient().when(entity.getActualizadoPor()).thenReturn("admin");
        lenient().when(entity.getCreadoPor()).thenReturn("admin");
        lenient().when(entity.getFechaCreacion()).thenReturn(LocalDateTime.now());
        lenient().when(entity.getFechaActualizacion()).thenReturn(LocalDateTime.now());
        return entity;
    }
}
