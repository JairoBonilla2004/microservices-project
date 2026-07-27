package ec.edu.espe.master_gateway.shared.infrastructure.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PermissionControllerTest {

    private final PermissionController controller = new PermissionController();

    @Test
    void should_returnAllPermissionMetadata() {
        var response = controller.getMetadata();

        assertThat(response.getStatusCode().value()).isEqualTo(200);

        var body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body).hasSize(25);
    }

    @Test
    void should_includeDependencies_forEachPermission() {
        var response = controller.getMetadata();
        var body = response.getBody();

        var usersCreate = body.stream()
                .filter(p -> p.permission().equals("USERS_CREATE"))
                .findFirst()
                .orElseThrow();

        assertThat(usersCreate.dependencies()).contains("USERS_READ");
        assertThat(usersCreate.allDependencies()).containsExactly("USERS_READ");
    }

    @Test
    void should_includeTransitiveDependencies() {
        var response = controller.getMetadata();
        var body = response.getBody();

        var menusCreate = body.stream()
                .filter(p -> p.permission().equals("MENUS_CREATE"))
                .findFirst()
                .orElseThrow();

        assertThat(menusCreate.dependencies()).containsExactlyInAnyOrder("MENUS_READ", "MODULES_READ");
        assertThat(menusCreate.allDependencies()).containsExactlyInAnyOrder("MENUS_READ", "MODULES_READ");
    }

    @Test
    void should_haveEmptyDeps_forReadPermissions() {
        var response = controller.getMetadata();
        var body = response.getBody();

        body.stream()
                .filter(p -> p.permission().endsWith("_READ"))
                .forEach(p -> {
                    assertThat(p.dependencies()).isEmpty();
                    assertThat(p.allDependencies()).isEmpty();
                });
    }
}
