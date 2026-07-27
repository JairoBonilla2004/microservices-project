package ec.edu.espe.master_gateway.shared.infrastructure.web;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Respuesta de error estandarizada para toda la API.
 *
 * <p>Proporciona una estructura uniforme para todas las respuestas de
 * error, incluyendo código HTTP, mensaje descriptivo, marca de tiempo,
 * errores de validación de campos y un mapa opcional de detalles
 * adicionales ({@code detalles}). Los campos nulos son omitidos
 * automáticamente en la serialización JSON.</p>
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
    private final Map<String, Object> detalles;

    private ErrorResponse(int codigo, String mensaje,
                          Map<String, String> errores,
                          Map<String, Object> detalles) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.timestamp = LocalDateTime.now(ZoneOffset.UTC);
        this.errores = errores;
        this.detalles = detalles;
    }

    public static ErrorResponse of(HttpStatus status, String mensaje) {
        return new ErrorResponse(status.value(), mensaje, null, null);
    }

    public static ErrorResponse withDetalles(HttpStatus status, String mensaje,
                                             Map<String, Object> detalles) {
        return new ErrorResponse(status.value(), mensaje, null,
                detalles != null ? new HashMap<>(detalles) : null);
    }

    public static ErrorResponse validationError(HttpStatus status, String mensaje,
                                                Map<String, String> errores) {
        return new ErrorResponse(status.value(), mensaje, errores, null);
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

    public Map<String, Object> getDetalles() {
        return detalles;
    }
}
