package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.cycle_detection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence.MenuNodeJpaEntity;
import ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence.MenuNodeJpaRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Pruebas unitarias para {@link CycleDetectionAdapter}.
 *
 * <p>Garantiza la integridad de la estructura jerárquica de menús: mover un
 * nodo bajo un ancestro suyo (directa o indirectamente) debe detectarse como
 * ciclo, mientras que un padre válido no lo es.</p>
 */
@ExtendWith(MockitoExtension.class)
class CycleDetectionAdapterTest {

    @Mock
    private MenuNodeJpaRepository jpaRepository;

    @InjectMocks
    private CycleDetectionAdapter adapter;

    private MenuNodeJpaEntity entityWithId(UUID id) {
        // El stubbing de getId() se realiza fuera de cualquier when(...) en curso
        // para no provocar UnfinishedStubbingException por anidamiento.
        MenuNodeJpaEntity entity = mock(MenuNodeJpaEntity.class);
        org.mockito.Mockito.lenient().when(entity.getId()).thenReturn(id);
        return entity;
    }

    @Test
    void should_notDetectCycle_when_movingToValidUnrelatedParent() {
        UUID nodeId = UUID.randomUUID();
        UUID newParentId = UUID.randomUUID();
        UUID unrelatedAncestor = UUID.randomUUID();

        // Los ancestros del nuevo padre no incluyen al nodo movido.
        List<MenuNodeJpaEntity> ancestors = List.of(entityWithId(unrelatedAncestor));
        when(jpaRepository.findAncestors(newParentId)).thenReturn(ancestors);

        assertThat(adapter.wouldCreateCycle(nodeId, newParentId)).isFalse();
    }

    @Test
    void should_detectCycle_when_nodeBecomesItsOwnParent() {
        UUID nodeId = UUID.randomUUID();

        // findAncestors(nodeId) devuelve al propio nodo entre sus ancestros -> ciclo directo.
        List<MenuNodeJpaEntity> ancestors = List.of(entityWithId(nodeId));
        when(jpaRepository.findAncestors(nodeId)).thenReturn(ancestors);

        assertThat(adapter.wouldCreateCycle(nodeId, nodeId)).isTrue();
    }

    @Test
    void should_detectCycle_when_aDescendantWouldBecomeTheParent() {
        UUID nodeId = UUID.randomUUID();
        UUID descendantId = UUID.randomUUID();
        UUID intermediateId = UUID.randomUUID();

        // Se intenta mover 'node' bajo 'descendant' (un descendiente suyo).
        // Los ancestros de 'descendant' incluyen a 'node' -> ciclo indirecto.
        List<MenuNodeJpaEntity> ancestors =
                List.of(entityWithId(intermediateId), entityWithId(nodeId));
        when(jpaRepository.findAncestors(descendantId)).thenReturn(ancestors);

        assertThat(adapter.wouldCreateCycle(nodeId, descendantId)).isTrue();
    }

    @Test
    void should_notDetectCycle_when_newParentIsNullRootMove() {
        UUID nodeId = UUID.randomUUID();

        // Mover a raíz (parent null) nunca genera ciclo y no consulta ancestros.
        assertThat(adapter.wouldCreateCycle(nodeId, null)).isFalse();
    }
}
