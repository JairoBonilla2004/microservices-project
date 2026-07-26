package ec.edu.espe.master_gateway.contexts.menu.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class RoleMenuAssignmentTest {

    @Test
    void should_createRoleMenuAssignment() {
        var roleId = UUID.randomUUID();
        var menuNodeId = UUID.randomUUID();

        var assignment = new RoleMenuAssignment(roleId, menuNodeId, "admin");

        assertThat(assignment.getRoleId()).isEqualTo(roleId);
        assertThat(assignment.getMenuNodeId()).isEqualTo(menuNodeId);
    }

    @Test
    void should_revokeRoleMenuAssignment() {
        var assignment = new RoleMenuAssignment(UUID.randomUUID(), UUID.randomUUID(), "admin");

        assignment.revoke();

        assertThat(assignment.getEstado()).isEqualTo(ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro.INACTIVO);
    }
}
