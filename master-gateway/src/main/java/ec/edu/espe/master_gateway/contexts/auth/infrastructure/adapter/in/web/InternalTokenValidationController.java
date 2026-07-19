package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.in.web;

/**
 * Controlador REST para la validación interna de tokens JWT.
 *
 * <p>Expone un endpoint público utilizado por otros microservicios para
 * validar tokens JWT emitidos por el gateway. La validación delega en
 * {@link ValidateTokenUseCase}, que a través de {@link ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.TokenIssuerFactory}
 * selecciona el validador adecuado según el modo de validación del servicio
 * registrado.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.ValidateTokenUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.TokenValidationResponse;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.ValidateTokenRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/internals")
public class InternalTokenValidationController {

    private final ValidateTokenUseCase validateTokenUseCase;

    public InternalTokenValidationController(ValidateTokenUseCase validateTokenUseCase) {
        this.validateTokenUseCase = validateTokenUseCase;
    }

    @PostMapping("/validate-token")
    public ResponseEntity<TokenValidationResponse> validateToken(@RequestBody @Valid ValidateTokenRequest request) {
        var result = validateTokenUseCase.execute(request.token(), request.serviceCode());
        if (result.valid()) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
    }
}
