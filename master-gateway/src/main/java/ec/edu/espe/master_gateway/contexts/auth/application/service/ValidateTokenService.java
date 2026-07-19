package ec.edu.espe.master_gateway.contexts.auth.application.service;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.ValidateTokenUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.TokenValidationResponse;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.TokenIssuerFactory;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio de validación de tokens JWT.
 *
 * <p>Implementa la validación de tokens JWT delegando en el puerto de
 * validación correspondiente. Retorna una respuesta estructurada con
 * el resultado de la validación y los datos asociados al token.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class ValidateTokenService implements ValidateTokenUseCase {

    private static final Logger log = LoggerFactory.getLogger(ValidateTokenService.class);

    private final TokenIssuerFactory tokenIssuerFactory;
    private final ServiceRegistryRepositoryPort serviceRegistryRepositoryPort;

    /**
     * Inicializa el servicio con los componentes necesarios.
     *
     * @param tokenIssuerFactory           fábrica para seleccionar el validador
     * @param serviceRegistryRepositoryPort repositorio de servicios registrados
     */
    public ValidateTokenService(TokenIssuerFactory tokenIssuerFactory,
                                ServiceRegistryRepositoryPort serviceRegistryRepositoryPort) {
        this.tokenIssuerFactory = Objects.requireNonNull(tokenIssuerFactory);
        this.serviceRegistryRepositoryPort = Objects.requireNonNull(serviceRegistryRepositoryPort);
    }

    /**
     * Ejecuta la validación del token JWT contra el servicio especificado.
     *
     * <p>Si el serviceCode es nulo o vacío, se usa el validador por defecto.
     * Si el servicio no está registrado, se rechaza la validación.</p>
     *
     * @param token       token JWT a validar
     * @param serviceCode código del servicio que solicita la validación
     * @return respuesta con el estado de validez y datos del token
     */
    @Override
    public TokenValidationResponse execute(String token, String serviceCode) {
        log.debug("Validating token for serviceCode: {}", serviceCode);
        try {
            var validator = resolveValidator(serviceCode);
            var claims = validator.validate(token);
            log.debug("Token validated successfully for userId: {}", claims.getUserId());
            return new TokenValidationResponse(true, claims.getUserId(), claims.getRoleId(),
                    claims.getTokenType(), null);
        } catch (Exception e) {
            log.debug("Token validation failed: {}", e.getMessage());
            return new TokenValidationResponse(false, null, null, null, e.getMessage());
        }
    }

    private TokenValidationPort resolveValidator(String serviceCode) {
        if (serviceCode == null || serviceCode.isBlank()) {
            return tokenIssuerFactory.getDefaultValidator();
        }
        var service = serviceRegistryRepositoryPort.findByServiceCode(serviceCode)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Servicio no registrado: " + serviceCode));
        return tokenIssuerFactory.getValidator(service.getValidationMode(), service.getPublicKey());
    }
}
