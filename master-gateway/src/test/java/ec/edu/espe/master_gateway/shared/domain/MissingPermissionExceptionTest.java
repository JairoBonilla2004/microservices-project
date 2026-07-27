package ec.edu.espe.master_gateway.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import org.junit.jupiter.api.Test;

class MissingPermissionExceptionTest {

    @Test
    void should_createWithPermission() {
        var ex = new MissingPermissionException(Permission.ROLES_READ);

        assertThat(ex.getMessage()).contains("ROLES_READ");
        assertThat(ex.getCodigoError()).isEqualTo("FORBIDDEN");
        assertThat(ex.getMissingPermission()).isEqualTo(Permission.ROLES_READ);
    }

    @Test
    void should_includeSuggestedDependencies() {
        var ex = new MissingPermissionException(Permission.USERS_CREATE);

        assertThat(ex.getSuggestedPermissions()).contains(Permission.USERS_READ);
        assertThat(ex.getSuggestedPermissions()).doesNotContain(Permission.USERS_CREATE);
    }

    @Test
    void should_provideDetalles() {
        var ex = new MissingPermissionException(Permission.MODULES_ASSIGN);

        assertThat(ex.getDetalles()).containsKey("missingPermission");
        assertThat(ex.getDetalles()).containsKey("suggestedPermissions");
        assertThat(ex.getDetalles().get("missingPermission")).isEqualTo("MODULES_ASSIGN");
    }

    @Test
    void should_returnEmptyDeps_when_readPermission() {
        var ex = new MissingPermissionException(Permission.ROLES_READ);

        assertThat(ex.getSuggestedPermissions()).isEmpty();
    }
}
