package ec.edu.espe.master_gateway.contexts.service_registry.application.service;

import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.DeactivateServiceUseCase;
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
 * Servicio que implementa el caso de uso de desactivación de un servicio registrado.
 *
 * <p>Valida los permisos del usuario, busca el servicio por su código único,
 * lo marca como inactivo y persiste los cambios en el repositorio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class DeactivateServiceService implements DeactivateServiceUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeactivateServiceService.class);

    private final ServiceRegistryRepositoryPort repository;
    private final AuthorizationPort authorizationPort;

    public DeactivateServiceService(ServiceRegistryRepositoryPort repository,
                                    AuthorizationPort authorizationPort) {
        this.repository = Objects.requireNonNull(repository, "repository no puede ser null");
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(String serviceCode) {
        Objects.requireNonNull(serviceCode, "serviceCode no puede ser null");
        authorizationPort.requirePermission(Permission.SERVICES_DELETE);

        var service = repository.findByServiceCode(serviceCode)
            .orElseThrow(() -> new NotFoundException("RegisteredService", serviceCode));

        service.deactivate();
        repository.save(service);

        log.info("Service deactivated: code={}, name={}", serviceCode, service.getNombre());
    }
}
