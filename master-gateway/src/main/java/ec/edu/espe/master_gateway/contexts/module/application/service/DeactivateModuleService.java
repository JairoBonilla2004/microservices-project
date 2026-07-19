package ec.edu.espe.master_gateway.contexts.module.application.service;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.DeactivateModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.ModuleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.NotFoundException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de desactivación de un módulo.
 *
 * <p>Valida los permisos del usuario, busca el módulo por su identificador,
 * lo marca como inactivo y persiste los cambios en el repositorio.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class DeactivateModuleService implements DeactivateModuleUseCase {

    private static final Logger log = LoggerFactory.getLogger(DeactivateModuleService.class);

    private final ModuleRepositoryPort moduleRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public DeactivateModuleService(ModuleRepositoryPort moduleRepositoryPort,
                                   AuthorizationPort authorizationPort) {
        this.moduleRepositoryPort = Objects.requireNonNull(moduleRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID id) {
        authorizationPort.requirePermission(Permission.MODULES_DELETE);

        var module = moduleRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Module", id));
        module.deactivate();
        moduleRepositoryPort.save(module);

        log.info("Module deactivated: id={}, name={}", id, module.getNombre());
    }
}
