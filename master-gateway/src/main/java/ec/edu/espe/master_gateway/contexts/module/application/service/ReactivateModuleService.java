package ec.edu.espe.master_gateway.contexts.module.application.service;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.ReactivateModuleUseCase;
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

@Service
@Transactional
public class ReactivateModuleService implements ReactivateModuleUseCase {

    private static final Logger log = LoggerFactory.getLogger(ReactivateModuleService.class);

    private final ModuleRepositoryPort moduleRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public ReactivateModuleService(ModuleRepositoryPort moduleRepositoryPort,
                                   AuthorizationPort authorizationPort) {
        this.moduleRepositoryPort = Objects.requireNonNull(moduleRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public void execute(UUID id) {
        authorizationPort.requirePermission(Permission.MODULES_DELETE);

        var module = moduleRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Module", id));
        module.reactivate();
        moduleRepositoryPort.save(module);

        log.info("Module reactivated: id={}, name={}", id, module.getNombre());
    }
}
