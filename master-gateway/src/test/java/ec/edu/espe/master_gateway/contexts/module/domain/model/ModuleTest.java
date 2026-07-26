package ec.edu.espe.master_gateway.contexts.module.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class ModuleTest {

    @Test
    void should_createModule() {
        var module = new Module("User Management", "Gestión de usuarios", "/users", 1);

        assertThat(module.getNombre()).isEqualTo("User Management");
        assertThat(module.getDescripcion()).isEqualTo("Gestión de usuarios");
        assertThat(module.getIcono()).isEqualTo("/users");
        assertThat(module.getOrden()).isEqualTo(1);
    }

    @Test
    void should_createRoleModuleAssignment() {
        var roleId = UUID.randomUUID();
        var moduleId = UUID.randomUUID();

        var assignment = new RoleModuleAssignment(roleId, moduleId, "admin");

        assertThat(assignment.getRoleId()).isEqualTo(roleId);
        assertThat(assignment.getModuleId()).isEqualTo(moduleId);
    }

    @Test
    void should_deactivateModule() {
        var module = new Module("User Management", "Gestión de usuarios", "/users", 1);

        module.deactivate();

        assertThat(module.getEstado()).isEqualTo(ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro.INACTIVO);
    }
}
