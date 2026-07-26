package ec.edu.espe.master_gateway.contexts.menu.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class MenuNodeTest {

    @Test
    void should_createMenuNode_withActiveState() {
        var moduleId = UUID.randomUUID();
        var node = new MenuNode("Dashboard", moduleId, null, 1);

        assertThat(node.getNombre()).isEqualTo("Dashboard");
        assertThat(node.getModuleId()).isEqualTo(moduleId);
        assertThat(node.getParentId()).isNull();
        assertThat(node.getOrden()).isEqualTo(1);
        assertThat(node.getEstado()).isEqualTo(EstadoRegistro.ACTIVO);
    }

    @Test
    void should_returnIsLeaf_when_hasUrl() {
        var node = new MenuNode("Dashboard", UUID.randomUUID(), null, 1);

        node.setUrl("/dashboard");

        assertThat(node.isLeaf()).isTrue();
    }

    @Test
    void should_returnIsRoot_when_noParent() {
        var node = new MenuNode("Dashboard", UUID.randomUUID(), null, 1);

        assertThat(node.isRoot()).isTrue();
    }

    @Test
    void should_moveToNewParent() {
        var node = new MenuNode("Dashboard", UUID.randomUUID(), null, 1);
        var newParentId = UUID.randomUUID();

        node.moveTo(newParentId);

        assertThat(node.getParentId()).isEqualTo(newParentId);
    }

    @Test
    void should_throwException_when_movingToItself() {
        var node = new MenuNode("Dashboard", UUID.randomUUID(), null, 1);
        var nodeId = UUID.randomUUID();
        node.setId(nodeId);

        var exception = assertThrows(IllegalArgumentException.class, () -> node.moveTo(nodeId));

        assertThat(exception.getMessage()).isEqualTo("Un nodo no puede ser padre de sí mismo");
    }

    @Test
    void should_deactivate() {
        var node = new MenuNode("Dashboard", UUID.randomUUID(), null, 1);

        node.deactivate();

        assertThat(node.getEstado()).isEqualTo(EstadoRegistro.INACTIVO);

        var exception = assertThrows(IllegalStateException.class, node::deactivate);

        assertThat(exception.getMessage()).isEqualTo("El nodo ya está inactivo");
    }
}
