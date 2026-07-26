package ec.edu.espe.master_gateway.contexts.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void should_createRole_withActiveState() {
        var role = new Role("Admin", "Administrator role");

        assertThat(role.getNombre()).isEqualTo("Admin");
        assertThat(role.getDescripcion()).isEqualTo("Administrator role");
        assertThat(role.getEstado()).isEqualTo(EstadoRegistro.ACTIVO);
    }

    @Test
    void should_deactivateRole() {
        var role = new Role("Admin", "Administrator role");

        role.deactivate();

        assertThat(role.getEstado()).isEqualTo(EstadoRegistro.INACTIVO);
    }

    @Test
    void should_throwException_whenDeactivatingInactiveRole() {
        var role = new Role("Admin", "Administrator role");
        role.deactivate();

        var exception = assertThrows(IllegalStateException.class, role::deactivate);

        assertThat(exception.getMessage()).isEqualTo("El rol ya está inactivo");
    }

    @Test
    void should_updateNombre() {
        var role = new Role("Admin", "Administrator role");

        role.updateNombre("SuperAdmin");

        assertThat(role.getNombre()).isEqualTo("SuperAdmin");
    }

    @Test
    void should_updateDescripcion() {
        var role = new Role("Admin", "Administrator role");

        role.updateDescripcion("Updated description");

        assertThat(role.getDescripcion()).isEqualTo("Updated description");
    }

    @Test
    void should_markAsPersisted() {
        var role = new Role("Admin", "Administrator role");
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();

        role.markAsPersisted(id, now, now, "admin", "admin");

        assertThat(role.getId()).isEqualTo(id);
        assertThat(role.getFechaCreacion()).isEqualTo(now);
        assertThat(role.getFechaActualizacion()).isEqualTo(now);
        assertThat(role.getCreadoPor()).isEqualTo("admin");
        assertThat(role.getActualizadoPor()).isEqualTo("admin");
    }
}
