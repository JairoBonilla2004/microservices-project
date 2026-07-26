package ec.edu.espe.master_gateway.contexts.identity.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void should_createUser_withActiveState() {
        var user = new User("jdoe", "jdoe@example.com", "hash123", "John Doe");

        assertThat(user.getUsername()).isEqualTo("jdoe");
        assertThat(user.getEmail()).isEqualTo("jdoe@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("hash123");
        assertThat(user.getNombreCompleto()).isEqualTo("John Doe");
        assertThat(user.getEstado()).isEqualTo(EstadoRegistro.ACTIVO);
        assertThat(user.isActive()).isTrue();
    }

    @Test
    void should_deactivateUser() {
        var user = new User("jdoe", "jdoe@example.com", "hash123", "John Doe");

        user.deactivate();

        assertThat(user.getEstado()).isEqualTo(EstadoRegistro.INACTIVO);
        assertThat(user.isActive()).isFalse();
    }

    @Test
    void should_throwException_whenDeactivatingInactiveUser() {
        var user = new User("jdoe", "jdoe@example.com", "hash123", "John Doe");
        user.deactivate();

        var exception = assertThrows(IllegalStateException.class, user::deactivate);

        assertThat(exception.getMessage()).isEqualTo("El usuario ya está inactivo");
    }

    @Test
    void should_updatePassword() {
        var user = new User("jdoe", "jdoe@example.com", "hash123", "John Doe");

        user.updatePassword("newHash456");

        assertThat(user.getPasswordHash()).isEqualTo("newHash456");
    }

    @Test
    void should_updateEmail() {
        var user = new User("jdoe", "jdoe@example.com", "hash123", "John Doe");

        user.updateEmail("newemail@example.com");

        assertThat(user.getEmail()).isEqualTo("newemail@example.com");
    }

    @Test
    void should_updateNombreCompleto() {
        var user = new User("jdoe", "jdoe@example.com", "hash123", "John Doe");

        user.updateNombreCompleto("Jane Doe");

        assertThat(user.getNombreCompleto()).isEqualTo("Jane Doe");
    }

    @Test
    void should_markAsPersisted() {
        var user = new User("jdoe", "jdoe@example.com", "hash123", "John Doe");
        var id = UUID.randomUUID();
        var now = LocalDateTime.now();

        user.markAsPersisted(id, now, now, "admin", "admin");

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getFechaCreacion()).isEqualTo(now);
        assertThat(user.getFechaActualizacion()).isEqualTo(now);
        assertThat(user.getCreadoPor()).isEqualTo("admin");
        assertThat(user.getActualizadoPor()).isEqualTo("admin");
    }

    @Test
    void should_throwException_whenMarkingPersistedTwice() {
        var user = new User("jdoe", "jdoe@example.com", "hash123", "John Doe");
        user.markAsPersisted(UUID.randomUUID(), LocalDateTime.now(), LocalDateTime.now(), "admin", "admin");

        var secondId = UUID.randomUUID();
        var now = LocalDateTime.now();
        var exception = assertThrows(IllegalStateException.class,
                () -> user.markAsPersisted(secondId, now, now, "admin", "admin"));

        assertThat(exception.getMessage()).isEqualTo("El usuario ya fue persistido");
    }
}
