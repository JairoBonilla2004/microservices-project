package ec.edu.espe.master_gateway.shared.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class ErrorResponseTest {

    @Test
    void should_createErrorResponse_withStatusAndMessage() {
        var response = ErrorResponse.of(HttpStatus.NOT_FOUND, "Recurso no encontrado");

        assertThat(response.getCodigo()).isEqualTo(404);
        assertThat(response.getMensaje()).isEqualTo("Recurso no encontrado");
        assertThat(response.getTimestamp()).isNotNull();
        assertThat(response.getErrores()).isNull();
    }

    @Test
    void should_createValidationError_withFieldErrors() {
        var errores = Map.of("email", "Formato inválido");

        var response = ErrorResponse.validationError(HttpStatus.BAD_REQUEST, "Error de validación", errores);

        assertThat(response.getCodigo()).isEqualTo(400);
        assertThat(response.getMensaje()).isEqualTo("Error de validación");
        assertThat(response.getErrores()).containsEntry("email", "Formato inválido");
    }

    @Test
    void should_createErrorResponse_withDifferentStatus() {
        var response = ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno");

        assertThat(response.getCodigo()).isEqualTo(500);
    }

    @Test
    void should_createErrorResponse_withDetalles() {
        var detalles = Map.<String, Object>of("missingPermission", "ROLES_READ",
                "suggestedPermissions", java.util.List.of("MODULES_READ", "ROLES_READ"));

        var response = ErrorResponse.withDetalles(HttpStatus.FORBIDDEN, "Permiso requerido", detalles);

        assertThat(response.getCodigo()).isEqualTo(403);
        assertThat(response.getMensaje()).isEqualTo("Permiso requerido");
        assertThat(response.getDetalles()).containsEntry("missingPermission", "ROLES_READ");
        assertThat(response.getDetalles().get("suggestedPermissions"))
                .isInstanceOf(java.util.List.class);
    }

    @Test
    void should_createErrorResponse_withDetalles_whenNull() {
        var response = ErrorResponse.withDetalles(HttpStatus.FORBIDDEN, "Acceso denegado", null);

        assertThat(response.getCodigo()).isEqualTo(403);
        assertThat(response.getDetalles()).isNull();
    }

    @Test
    void should_returnNullDetalles_whenUsingOf() {
        var response = ErrorResponse.of(HttpStatus.BAD_REQUEST, "Error");

        assertThat(response.getDetalles()).isNull();
    }
}
