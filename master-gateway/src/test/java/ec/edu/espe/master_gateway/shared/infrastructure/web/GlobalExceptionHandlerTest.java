package ec.edu.espe.master_gateway.shared.infrastructure.web;

import static org.assertj.core.api.Assertions.assertThat;

import ec.edu.espe.master_gateway.shared.domain.AuthenticationException;
import ec.edu.espe.master_gateway.shared.domain.DomainException;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.InvalidInputException;
import ec.edu.espe.master_gateway.shared.domain.MissingPermissionException;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.RateLimitExceededException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import io.jsonwebtoken.JwtException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpMethod;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void should_handleNotFound() {
        var ex = new NotFoundException("Usuario", 1);

        var response = handler.handleNotFound(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMensaje()).contains("Usuario");
    }

    @Test
    void should_handleDuplicate() {
        var ex = new DuplicateException("Usuario", "email", "test@test.com");

        var response = handler.handleDuplicate(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void should_handleRateLimit() {
        var ex = new RateLimitExceededException("Demasiadas solicitudes", 60, 5, 0);

        var response = handler.handleRateLimit(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(response.getHeaders().getFirst("Retry-After")).isEqualTo("60");
    }

    @Test
    void should_handleAuthentication() {
        var ex = new AuthenticationException("Credenciales inválidas");

        var response = handler.handleAuthentication(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void should_handleMissingPermission() {
        var ex = new MissingPermissionException(Permission.ROLES_READ);

        var response = handler.handleMissingPermission(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMensaje()).contains("ROLES_READ");
        assertThat(response.getBody().getDetalles()).containsKey("missingPermission");
        assertThat(response.getBody().getDetalles().get("missingPermission")).isEqualTo("ROLES_READ");
    }

    @Test
    void should_handleDomainException_withForbiddenCode() {
        var ex = new DomainException("Acceso denegado", "FORBIDDEN") {};

        var response = handler.handleDomain(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void should_handleDomainException_withCycleDetectedCode() {
        var ex = new DomainException("Ciclo detectado", "CYCLE_DETECTED") {};

        var response = handler.handleDomain(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_handleDomainException_withValidationErrorCode() {
        var ex = new InvalidInputException("Dato inválido");

        var response = handler.handleDomain(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_handleDomainException_withDefaultCode() {
        var ex = new DomainException("Error desconocido", "UNKNOWN") {};

        var response = handler.handleDomain(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void should_handleValidation() {
        var bindingResult = mock(BindingResult.class);
        var fieldError = new FieldError("object", "email", "Formato inválido");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
        var ex = new MethodArgumentNotValidException(null, bindingResult);

        var response = handler.handleValidation(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrores()).containsKey("email");
    }

    @Test
    void should_handleMediaTypeNotSupported() {
        var ex = new HttpMediaTypeNotSupportedException("Tipo no soportado");

        var response = handler.handleMediaTypeNotSupported(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    void should_handleMethodNotAllowed() {
        var ex = new HttpRequestMethodNotSupportedException("POST");

        var response = handler.handleMethodNotAllowed(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Test
    void should_handleMessageNotReadable() {
        var ex = mock(HttpMessageNotReadableException.class);

        var response = handler.handleMessageNotReadable(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_handleTypeMismatch() {
        var ex = new MethodArgumentTypeMismatchException("valor", null, "id", null, null);

        var response = handler.handleTypeMismatch(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_handleIllegalState() {
        var ex = new IllegalStateException("Estado ilegal");

        var response = handler.handleIllegalState(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void should_handleIllegalArgument() {
        var ex = new IllegalArgumentException("Argumento inválido");

        var response = handler.handleIllegalArgument(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_handleMissingHeader() {
        var ex = new MissingRequestHeaderException("Authorization", null);

        var response = handler.handleMissingHeader(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void should_handleNoResource() {
        var ex = new NoResourceFoundException(HttpMethod.GET, "recurso", null);

        var response = handler.handleNoResource(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void should_handleJwt() {
        var ex = new JwtException("Token inválido");

        var response = handler.handleJwt(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void should_handleGeneric() {
        var ex = new RuntimeException("Error inesperado");

        var response = handler.handleGeneric(ex);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
