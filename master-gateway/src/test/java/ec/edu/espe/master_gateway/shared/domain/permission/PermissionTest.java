package ec.edu.espe.master_gateway.shared.domain.permission;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;

class PermissionTest {

    @Test
    void should_haveExpectedPermissions() {
        assertThat(Permission.valueOf("USERS_CREATE")).isEqualTo(Permission.USERS_CREATE);
        assertThat(Permission.valueOf("USERS_READ")).isEqualTo(Permission.USERS_READ);
        assertThat(Permission.valueOf("USERS_UPDATE")).isEqualTo(Permission.USERS_UPDATE);
        assertThat(Permission.valueOf("USERS_DELETE")).isEqualTo(Permission.USERS_DELETE);
        assertThat(Permission.valueOf("USERS_ASSIGN_ROLE")).isEqualTo(Permission.USERS_ASSIGN_ROLE);
        assertThat(Permission.valueOf("USERS_REVOKE_ROLE")).isEqualTo(Permission.USERS_REVOKE_ROLE);
        assertThat(Permission.valueOf("ROLES_CREATE")).isEqualTo(Permission.ROLES_CREATE);
        assertThat(Permission.valueOf("ROLES_READ")).isEqualTo(Permission.ROLES_READ);
        assertThat(Permission.valueOf("ROLES_UPDATE")).isEqualTo(Permission.ROLES_UPDATE);
        assertThat(Permission.valueOf("ROLES_DELETE")).isEqualTo(Permission.ROLES_DELETE);
        assertThat(Permission.valueOf("ROLES_ASSIGN_USERS")).isEqualTo(Permission.ROLES_ASSIGN_USERS);
        assertThat(Permission.valueOf("MODULES_CREATE")).isEqualTo(Permission.MODULES_CREATE);
        assertThat(Permission.valueOf("MODULES_READ")).isEqualTo(Permission.MODULES_READ);
        assertThat(Permission.valueOf("MODULES_UPDATE")).isEqualTo(Permission.MODULES_UPDATE);
        assertThat(Permission.valueOf("MODULES_DELETE")).isEqualTo(Permission.MODULES_DELETE);
        assertThat(Permission.valueOf("MODULES_ASSIGN")).isEqualTo(Permission.MODULES_ASSIGN);
        assertThat(Permission.valueOf("MENUS_CREATE")).isEqualTo(Permission.MENUS_CREATE);
        assertThat(Permission.valueOf("MENUS_READ")).isEqualTo(Permission.MENUS_READ);
        assertThat(Permission.valueOf("MENUS_UPDATE")).isEqualTo(Permission.MENUS_UPDATE);
        assertThat(Permission.valueOf("MENUS_DELETE")).isEqualTo(Permission.MENUS_DELETE);
        assertThat(Permission.valueOf("MENUS_ASSIGN")).isEqualTo(Permission.MENUS_ASSIGN);
        assertThat(Permission.valueOf("SERVICES_CREATE")).isEqualTo(Permission.SERVICES_CREATE);
        assertThat(Permission.valueOf("SERVICES_READ")).isEqualTo(Permission.SERVICES_READ);
        assertThat(Permission.valueOf("SERVICES_UPDATE")).isEqualTo(Permission.SERVICES_UPDATE);
        assertThat(Permission.valueOf("SERVICES_DELETE")).isEqualTo(Permission.SERVICES_DELETE);
    }

    @Test
    void should_matchExpectedCount() {
        assertThat(Permission.values()).hasSize(25);
    }

    @Test
    void should_haveNoDependencies_when_readPermission() {
        assertThat(Permission.USERS_READ.getDependencies()).isEmpty();
        assertThat(Permission.ROLES_READ.getDependencies()).isEmpty();
        assertThat(Permission.MODULES_READ.getDependencies()).isEmpty();
        assertThat(Permission.MENUS_READ.getDependencies()).isEmpty();
        assertThat(Permission.SERVICES_READ.getDependencies()).isEmpty();
    }

    @Test
    void should_dependOnRead_when_createPermission() {
        assertThat(Permission.USERS_CREATE.getDependencies()).containsExactly(Permission.USERS_READ);
        assertThat(Permission.ROLES_CREATE.getDependencies()).containsExactly(Permission.ROLES_READ);
        assertThat(Permission.MODULES_CREATE.getDependencies()).containsExactly(Permission.MODULES_READ);
        assertThat(Permission.MENUS_CREATE.getDependencies()).containsExactly(Permission.MENUS_READ, Permission.MODULES_READ);
        assertThat(Permission.SERVICES_CREATE.getDependencies()).containsExactly(Permission.SERVICES_READ);
    }

    @Test
    void should_dependOnMultipleReads_when_assignPermission() {
        assertThat(Permission.MODULES_ASSIGN.getDependencies())
                .containsExactlyInAnyOrder(Permission.MODULES_READ, Permission.ROLES_READ);
        assertThat(Permission.ROLES_ASSIGN_USERS.getDependencies())
                .containsExactlyInAnyOrder(Permission.ROLES_READ, Permission.USERS_READ);
        assertThat(Permission.USERS_ASSIGN_ROLE.getDependencies())
                .containsExactlyInAnyOrder(Permission.USERS_READ, Permission.ROLES_READ);
        assertThat(Permission.MENUS_ASSIGN.getDependencies())
                .containsExactlyInAnyOrder(Permission.MENUS_READ, Permission.ROLES_READ);
    }

    @Test
    void should_resolveTransitiveDependencies() {
        Set<Permission> menuCreateDeps = Permission.MENUS_CREATE.withDependencies();
        assertThat(menuCreateDeps).contains(Permission.MENUS_READ, Permission.MODULES_READ);

        Set<Permission> userAssignRoleDeps = Permission.USERS_ASSIGN_ROLE.withDependencies();
        assertThat(userAssignRoleDeps).contains(Permission.USERS_READ, Permission.ROLES_READ);

        Set<Permission> moduleAssignDeps = Permission.MODULES_ASSIGN.withDependencies();
        assertThat(moduleAssignDeps).contains(Permission.MODULES_READ, Permission.ROLES_READ);
    }

    @Test
    void should_notIncludeSelf_inWithDependencies() {
        Set<Permission> deps = Permission.MODULES_ASSIGN.withDependencies();
        assertThat(deps).doesNotContain(Permission.MODULES_ASSIGN);
    }

    @Test
    void should_returnEmptySet_when_readPermission_withDependencies() {
        assertThat(Permission.USERS_READ.withDependencies()).isEmpty();
        assertThat(Permission.ROLES_READ.withDependencies()).isEmpty();
    }

    @Test
    void should_beImmutable_withDependencies() {
        Set<Permission> deps = Permission.MODULES_ASSIGN.withDependencies();
        assertThat(deps).isUnmodifiable();
    }
}
