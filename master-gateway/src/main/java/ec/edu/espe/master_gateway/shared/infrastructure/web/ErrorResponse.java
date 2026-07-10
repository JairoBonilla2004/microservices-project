package ec.edu.espe.master_gateway.shared.infrastructure.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.http.HttpStatus;

/**
 * Respuesta de error estandarizada para toda la API.
 *
 * <p>Proporciona una estructura uniforme para todas las respuestas de
 * error, incluyendo código HTTP, mensaje descriptivo, marca de tiempo
 * y un mapa opcional de errores de validación de campos. Los campos
 * nulos son omitidos automáticamente en la serialización JSON.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final int codigo;
    private final String mensaje;
    private final LocalDateTime timestamp;
    private final Map<String, String> errores;

    private ErrorResponse(int codigo, String mensaje, Map<String, String> errores) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now();
        this.errores = errores;
    }

    public static ErrorResponse of(HttpStatus status, String mensaje) {
        return new ErrorResponse(status.value(), mensaje, null);
    }

    public static ErrorResponse validationError(HttpStatus status, String mensaje,
                                                Map<String, String> errores) {
        return new ErrorResponse(status.value(), mensaje, errores);
    }

    public int getCodigo() {
        return codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
