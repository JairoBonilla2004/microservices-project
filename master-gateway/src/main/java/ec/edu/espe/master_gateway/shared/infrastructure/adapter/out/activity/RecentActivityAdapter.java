package ec.edu.espe.master_gateway.shared.infrastructure.adapter.out.activity;

import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.RoleJpaRepository;
import ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence.UserJpaRepository;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence.MenuNodeJpaRepository;
import ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence.ModuleJpaRepository;
import ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.out.persistence.RegisteredServiceJpaRepository;
import ec.edu.espe.master_gateway.shared.domain.model.ActivityRecord;
import ec.edu.espe.master_gateway.shared.domain.port.out.RecentActivityPort;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Component;

/**
 * Adaptador de salida que consulta la actividad reciente de cada contexto.
 *
 * <p>Inyecta los repositorios JPA de usuarios, roles, módulos, menús y
 * servicios registrados, recupera los registros activos más recientes de
 * cada uno y los transforma al modelo de dominio {@link ActivityRecord}.
 * Al ser un caso transversal a varios contextos delimitados, reside en la
 * capa compartida.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class RecentActivityAdapter implements RecentActivityPort {

    private final UserJpaRepository userJpaRepository;
    private final RoleJpaRepository roleJpaRepository;
    private final ModuleJpaRepository moduleJpaRepository;
    private final MenuNodeJpaRepository menuNodeJpaRepository;
    private final RegisteredServiceJpaRepository registeredServiceJpaRepository;

    public RecentActivityAdapter(UserJpaRepository userJpaRepository,
                                 RoleJpaRepository roleJpaRepository,
                                 ModuleJpaRepository moduleJpaRepository,
                                 MenuNodeJpaRepository menuNodeJpaRepository,
                                 RegisteredServiceJpaRepository registeredServiceJpaRepository) {
        this.userJpaRepository = Objects.requireNonNull(userJpaRepository);
        this.roleJpaRepository = Objects.requireNonNull(roleJpaRepository);
        this.moduleJpaRepository = Objects.requireNonNull(moduleJpaRepository);
        this.menuNodeJpaRepository = Objects.requireNonNull(menuNodeJpaRepository);
        this.registeredServiceJpaRepository = Objects.requireNonNull(registeredServiceJpaRepository);
    }

    @Override
    public List<ActivityRecord> findRecentUsers(int limit) {
        return limitList(userJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO), limit)
                .stream()
                .map(entity -> new ActivityRecord(
                        entity.getNombreCompleto() != null ? entity.getNombreCompleto() : entity.getUsername(),
                        actorOf(entity.getActualizadoPor(), entity.getCreadoPor()),
                        entity.getFechaCreacion(),
                        entity.getFechaActualizacion()))
                .toList();
    }

    @Override
    public List<ActivityRecord> findRecentRoles(int limit) {
        return limitList(roleJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO), limit)
                .stream()
                .map(entity -> new ActivityRecord(
                        entity.getNombre(),
                        actorOf(entity.getActualizadoPor(), entity.getCreadoPor()),
                        entity.getFechaCreacion(),
                        entity.getFechaActualizacion()))
                .toList();
    }

    @Override
    public List<ActivityRecord> findRecentModules(int limit) {
        return limitList(moduleJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO), limit)
                .stream()
                .map(entity -> new ActivityRecord(
                        entity.getNombre(),
                        actorOf(entity.getActualizadoPor(), entity.getCreadoPor()),
                        entity.getFechaCreacion(),
                        entity.getFechaActualizacion()))
                .toList();
    }

    @Override
    public List<ActivityRecord> findRecentMenuItems(int limit) {
        return limitList(menuNodeJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO), limit)
                .stream()
                .map(entity -> new ActivityRecord(
                        entity.getNombre(),
                        actorOf(entity.getActualizadoPor(), entity.getCreadoPor()),
                        entity.getFechaCreacion(),
                        entity.getFechaActualizacion()))
                .toList();
    }

    @Override
    public List<ActivityRecord> findRecentServices(int limit) {
        return limitList(registeredServiceJpaRepository.findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro.ACTIVO), limit)
                .stream()
                .map(entity -> new ActivityRecord(
                        entity.getNombre(),
                        actorOf(entity.getActualizadoPor(), entity.getCreadoPor()),
                        entity.getFechaCreacion(),
                        entity.getFechaActualizacion()))
                .toList();
    }

    private String actorOf(String actualizadoPor, String creadoPor) {
        return actualizadoPor != null ? actualizadoPor : creadoPor;
    }

    private <T> List<T> limitList(List<T> source, int limit) {
        if (limit <= 0 || source.size() <= limit) {
            return source;
        }
        return source.subList(0, limit);
    }
}
