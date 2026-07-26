package ec.edu.espe.master_gateway.shared.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

class SpringSecurityAuthorizationAdapterTest {

    private final SpringSecurityAuthorizationAdapter adapter = new SpringSecurityAuthorizationAdapter();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void should_getCurrentUserId_when_authenticated() {
        var userId = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        var result = adapter.getCurrentUserId();

        assertThat(result).isEqualTo(userId);
    }

    @Test
    void should_throw_when_notAuthenticated() {
        SecurityContextHolder.getContext().setAuthentication(null);

        assertThatThrownBy(adapter::getCurrentUserId)
                .isInstanceOf(AuthorizationException.class)
                .hasMessageContaining("no autenticado");
    }

    @Test
    void should_throw_when_anonymousUser() {
        var auth = new UsernamePasswordAuthenticationToken("anonymousUser", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(adapter::getCurrentUserId)
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_returnFalse_when_noPermission() {
        var auth = new UsernamePasswordAuthenticationToken("user", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        var result = adapter.hasPermission(Permission.USERS_CREATE);

        assertThat(result).isFalse();
    }

    @Test
    void should_returnTrue_when_hasPermission() {
        var auth = new UsernamePasswordAuthenticationToken("user", null,
                List.of(new SimpleGrantedAuthority("PERMISSION_USERS_CREATE")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        var result = adapter.hasPermission(Permission.USERS_CREATE);

        assertThat(result).isTrue();
    }

    @Test
    void should_returnFalse_when_notAuthenticatedForPermission() {
        SecurityContextHolder.getContext().setAuthentication(null);

        var result = adapter.hasPermission(Permission.USERS_READ);

        assertThat(result).isFalse();
    }

    @Test
    void should_throw_when_requirePermissionFails() {
        var auth = new UsernamePasswordAuthenticationToken("user", null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(() -> adapter.requirePermission(Permission.USERS_DELETE))
                .isInstanceOf(AuthorizationException.class);
    }

    @Test
    void should_pass_when_requirePermissionSucceeds() {
        var auth = new UsernamePasswordAuthenticationToken("user", null,
                List.of(new SimpleGrantedAuthority("PERMISSION_USERS_DELETE")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        adapter.requirePermission(Permission.USERS_DELETE);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void should_passOwnership_when_userIsOwner() {
        var userId = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        adapter.requireOwnershipOrPermission(userId, Permission.USERS_UPDATE);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void should_requirePermission_when_notOwner() {
        var userId = UUID.randomUUID();
        var otherId = UUID.randomUUID();
        var auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(() -> adapter.requireOwnershipOrPermission(otherId, Permission.USERS_UPDATE))
                .isInstanceOf(AuthorizationException.class);
    }
}
