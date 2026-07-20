package ec.edu.espe.master_gateway.contexts.service_registry.application.service;

import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.ListRegisteredServicesUseCase;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.ServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.domain.port.out.ServiceRegistryRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ListRegisteredServicesService implements ListRegisteredServicesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListRegisteredServicesService.class);

    private final ServiceRegistryRepositoryPort repository;
    private final AuthorizationPort authorizationPort;

    public ListRegisteredServicesService(ServiceRegistryRepositoryPort repository,
                                         AuthorizationPort authorizationPort) {
        this.repository = Objects.requireNonNull(repository, "repository no puede ser null");
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * Ejecuta la consulta de todos los servicios activos.
     *
     * @return lista de servicios activos registrados en el sistema.
     */
    @Override
    public List<ServiceResponse> execute() {
        authorizationPort.requirePermission(Permission.SERVICES_READ);
        var services = repository.findAllActive();

        log.debug("Listed {} active registered services", services.size());

        return services
            .stream()
            .map(s -> new ServiceResponse(
                s.getId(),
                s.getServiceCode(),
                s.getNombre(),
                s.getBaseUrl(),
                s.getValidationMode().name(),
                s.getEstado().name(),
                s.getFechaCreacion(),
                s.getFechaActualizacion()
            ))
            .toList();
    }
}
