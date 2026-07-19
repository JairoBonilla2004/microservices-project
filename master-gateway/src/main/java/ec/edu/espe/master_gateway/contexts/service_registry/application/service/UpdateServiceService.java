package ec.edu.espe.master_gateway.contexts.service_registry.application.service;

import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.UpdateServiceUseCase;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.ServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.UpdateServiceRequest;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de actualización de un servicio registrado.
 *
 * <p>Valida los permisos del usuario, busca el servicio por su código único,
 * aplica los cambios opcionales (nombre, URL base, clave pública) y persiste
 * el servicio actualizado retornando la respuesta modificada.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class UpdateServiceService implements UpdateServiceUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateServiceService.class);

    private final ServiceRegistryRepositoryPort repository;
    private final AuthorizationPort authorizationPort;

    public UpdateServiceService(ServiceRegistryRepositoryPort repository,
                                AuthorizationPort authorizationPort) {
        this.repository = Objects.requireNonNull(repository, "repository no puede ser null");
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public ServiceResponse execute(String serviceCode, UpdateServiceRequest request) {
        Objects.requireNonNull(serviceCode, "serviceCode no puede ser null");
        Objects.requireNonNull(request, "request no puede ser null");
        authorizationPort.requirePermission(Permission.SERVICES_UPDATE);

        var service = repository.findByServiceCode(serviceCode)
            .orElseThrow(() -> new NotFoundException("RegisteredService", serviceCode));

        if (request.nombre() != null) {
            service.updateNombre(request.nombre());
        }
        if (request.baseUrl() != null) {
            service.updateBaseUrl(request.baseUrl());
        }
        if (request.publicKey() != null) {
            service.updatePublicKey(request.publicKey());
        }

        var saved = repository.save(service);

        log.info("Service updated: code={}, name={}", serviceCode, saved.getNombre());

        return new ServiceResponse(
            saved.getId(),
            saved.getServiceCode(),
            saved.getNombre(),
            saved.getBaseUrl(),
            saved.getValidationMode().name(),
            saved.getPublicKey(),
            saved.getEstado().name(),
            saved.getFechaCreacion(),
            saved.getFechaActualizacion()
        );
    }
}
