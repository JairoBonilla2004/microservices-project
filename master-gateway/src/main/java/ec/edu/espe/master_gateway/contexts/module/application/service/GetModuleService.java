package ec.edu.espe.master_gateway.contexts.module.application.service;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.GetModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
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
@Transactional(readOnly = true)
public class GetModuleService implements GetModuleUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetModuleService.class);

    private final ModuleRepositoryPort moduleRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public GetModuleService(ModuleRepositoryPort moduleRepositoryPort,
                            AuthorizationPort authorizationPort) {
        this.moduleRepositoryPort = Objects.requireNonNull(moduleRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public ModuleResponse execute(UUID id) {
        authorizationPort.requirePermission(Permission.MODULES_READ);
        var module = moduleRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Module", id));

        log.debug("Module retrieved: id={}, name={}", id, module.getNombre());

        return new ModuleResponse(
            module.getId(),
            module.getNombre(),
            module.getDescripcion(),
            module.getIcono(),
            module.getOrden(),
            module.getEstado().name(),
            module.getFechaCreacion()
        );
    }
}
