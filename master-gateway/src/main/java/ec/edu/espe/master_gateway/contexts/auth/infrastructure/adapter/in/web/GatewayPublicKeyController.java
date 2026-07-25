package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.in.web;

import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.AsymmetricJwtIssuerAdapter;
import ec.edu.espe.master_gateway.shared.infrastructure.web.ErrorResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador que expone la clave pública RSA del Gateway para que los
 * microservicios en modo {@code LOCAL} puedan validar JWT sin llamar
 * al Gateway en cada request.
 *
 * <p>Este endpoint solo está disponible cuando {@code jwt.mode=asymmetric}.
 * En modo {@code direct} (HMAC) no existe una clave pública que compartir,
 * por lo que devuelve un error 400.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@RestController
@RequestMapping("/api/internals")
public class GatewayPublicKeyController {

    private final AsymmetricJwtIssuerAdapter asymmetricIssuer;

    public GatewayPublicKeyController(
            @Autowired(required = false) AsymmetricJwtIssuerAdapter asymmetricIssuer) {
        this.asymmetricIssuer = asymmetricIssuer;
    }

    /**
     * Obtiene la clave pública RSA del Gateway en formato PEM.
     *
     * <p>Los microservicios registrados con {@code validationMode=LOCAL}
     * deben llamar a este endpoint al iniciar para obtener la clave pública
     * y cachearla, permitiendo validación local de JWT sin latencia de red.</p>
     *
     * @return 200 con la clave pública si el Gateway está en modo asymmetric,
     *         400 si está en modo direct (HMAC).
     */
    @GetMapping("/gateway-public-key")
    public ResponseEntity<Object> getGatewayPublicKey() {
        if (asymmetricIssuer == null) {
            return ResponseEntity.badRequest().body(
                ErrorResponse.of(HttpStatus.BAD_REQUEST,
                    "El Gateway está en modo directo (HMAC). " +
                    "No hay clave pública disponible. " +
                    "Cambie jwt.mode a asymmetric para usar validación LOCAL."));
        }
        return ResponseEntity.ok(Map.of("publicKey", asymmetricIssuer.getPublicKeyPem()));
    }
}
