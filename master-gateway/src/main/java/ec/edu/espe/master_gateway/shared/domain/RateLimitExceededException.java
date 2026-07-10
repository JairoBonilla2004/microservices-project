package ec.edu.espe.master_gateway.shared.domain;

/**
 * Excepción lanzada cuando se supera el límite de tasa de solicitudes.
 *
 * <p>Protege los recursos del servidor limitando el número de intentos
 * permitidos en una ventana de tiempo, usando el código
 * {@code RATE_LIMIT_EXCEEDED} e incluyendo el tiempo recomendado de
 * espera antes de reintentar.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class RateLimitExceededException extends DomainException {

    private final int retryAfterSeconds;

    public RateLimitExceededException(String mensaje, int retryAfterSeconds) {
        super(mensaje, "RATE_LIMIT_EXCEEDED");
        this.retryAfterSeconds = retryAfterSeconds;
        addDetalle("retryAfterSeconds", retryAfterSeconds);
    }

    public int getRetryAfterSeconds() {
        return retryAfterSeconds;
    }
}
