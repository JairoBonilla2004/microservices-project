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
}
