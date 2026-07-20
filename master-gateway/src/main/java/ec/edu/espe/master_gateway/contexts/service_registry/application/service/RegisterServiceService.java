package ec.edu.espe.master_gateway.contexts.service_registry.application.service;

import ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.jwt.AsymmetricJwtIssuerAdapter;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.RegisterServiceUseCase;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.RegisterServiceRequest;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.RegisterServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.model.RegisteredService;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de registro de un nuevo servicio.
 *
 * <p>Valida los permisos del usuario, verifica que el código del servicio
 * no exista previamente, crea la entidad de dominio, la persiste y retorna
 * la respuesta con los datos del servicio registrado.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class RegisterServiceService implements RegisterServiceUseCase {

    private static final Logger log = LoggerFactory.getLogger(RegisterServiceService.class);

    private final ServiceRegistryRepositoryPort repository;
    private final AuthorizationPort authorizationPort;
    private final AsymmetricJwtIssuerAdapter asymmetricIssuer;

    public RegisterServiceService(ServiceRegistryRepositoryPort repository,
                                  AuthorizationPort authorizationPort,
                                  @Autowired(required = false) AsymmetricJwtIssuerAdapter asymmetricIssuer) {
        this.repository = Objects.requireNonNull(repository, "repository no puede ser null");
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
        this.asymmetricIssuer = asymmetricIssuer;
    }

    @Override
    public RegisterServiceResponse execute(RegisterServiceRequest request) {
        Objects.requireNonNull(request, "request no puede ser null");
        authorizationPort.requirePermission(Permission.SERVICES_CREATE);

        if (repository.existsByServiceCode(request.serviceCode())) {
            throw new DuplicateException("RegisteredService", "serviceCode", request.serviceCode());
        }

        RegisteredService.ValidationMode validationMode =
            RegisteredService.ValidationMode.valueOf(request.validationMode());

        if (validationMode == RegisteredService.ValidationMode.LOCAL && asymmetricIssuer == null) {
            throw new IllegalArgumentException(
                "El modo LOCAL requiere que el Gateway esté configurado con jwt.mode=asymmetric. " +
                "Cambie la propiedad en application.yml o use el modo DELEGATE.");
        }

        var service = new RegisteredService(
            request.serviceCode(),
            request.nombre(),
            request.baseUrl(),
            validationMode
        );

        var saved = repository.save(service);

        log.info("Service registered: id={}, code={}, name={}", saved.getId(), saved.getServiceCode(), saved.getNombre());

        return new RegisterServiceResponse(
            saved.getId(),
            saved.getServiceCode(),
            saved.getNombre(),
            saved.getBaseUrl(),
            saved.getValidationMode().name(),
            saved.getFechaCreacion()
        );
    }
}
