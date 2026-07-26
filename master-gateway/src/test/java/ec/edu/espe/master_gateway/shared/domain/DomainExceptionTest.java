package ec.edu.espe.master_gateway.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class DomainExceptionTest {

    @Test
    void should_createNotFoundException() {
        var ex = new NotFoundException("Usuario", UUID_UTIL);

        assertThat(ex.getMessage()).contains("Usuario").contains(UUID_UTIL);
        assertThat(ex.getCodigoError()).isEqualTo("NOT_FOUND");
        assertThat(ex.getDetalles()).containsKey("entidad").containsKey("id");
    }

    @Test
    void should_createDuplicateException() {
        var ex = new DuplicateException("Usuario", "email", "test@example.com");

        assertThat(ex.getMessage()).contains("email");
        assertThat(ex.getCodigoError()).isEqualTo("DUPLICATE_ENTRY");
        assertThat(ex.getDetalles()).containsEntry("campo", "email");
    }

    @Test
    void should_createInvalidInputException() {
        var ex = new InvalidInputException("Dato inválido");

        assertThat(ex.getMessage()).isEqualTo("Dato inválido");
        assertThat(ex.getCodigoError()).isEqualTo("VALIDATION_ERROR");
    }

    @Test
    void should_createAuthenticationException() {
        var ex = new AuthenticationException("Credenciales inválidas");

        assertThat(ex.getMessage()).isEqualTo("Credenciales inválidas");
        assertThat(ex.getCodigoError()).isEqualTo("AUTH_FAILED");
    }

    @Test
    void should_createAuthorizationException() {
        var ex = new AuthorizationException("Acceso denegado");

        assertThat(ex.getMessage()).isEqualTo("Acceso denegado");
        assertThat(ex.getCodigoError()).isEqualTo("FORBIDDEN");
    }

    @Test
    void should_createRateLimitExceededException() {
        var ex = new RateLimitExceededException("Límite excedido", 60, 5, 0);

        assertThat(ex.getMessage()).isEqualTo("Límite excedido");
        assertThat(ex.getCodigoError()).isEqualTo("RATE_LIMIT_EXCEEDED");
        assertThat(ex.getRetryAfterSeconds()).isEqualTo(60);
        assertThat(ex.getMaxAttempts()).isEqualTo(5);
        assertThat(ex.getRemainingAttempts()).isZero();
    }

    private static final String UUID_UTIL = "550e8400-e29b-41d4-a716-446655440000";
}
