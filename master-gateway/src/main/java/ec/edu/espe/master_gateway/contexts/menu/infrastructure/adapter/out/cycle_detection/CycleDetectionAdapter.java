package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.cycle_detection;

import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.CycleDetectionPort;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence.MenuNodeJpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Adaptador para la detecci&oacute;n de ciclos en el &aacute;rbol de men&uacute;s.
 *
 * <p>Implementa {@link CycleDetectionPort} utilizando una consulta
 * recursiva en {@link MenuNodeJpaRepository#findAncestors(UUID)} para
 * determinar si al mover un nodo bajo un nuevo padre se formaría un
 * ciclo en la estructura jer&aacute;rquica.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class CycleDetectionAdapter implements CycleDetectionPort {

    private final MenuNodeJpaRepository jpaRepository;

    public CycleDetectionAdapter(MenuNodeJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean wouldCreateCycle(UUID nodeId, UUID newParentId) {
        if (newParentId == null) {
            return false;
        }
        var ancestors = jpaRepository.findAncestors(newParentId);
        return ancestors.stream().anyMatch(a -> a.getId().equals(nodeId));
    }
}
