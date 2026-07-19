package ec.edu.espe.master_gateway.contexts.module.application.service;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.ListModulesUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.ModuleRepositoryPort;
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
public class ListModulesService implements ListModulesUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListModulesService.class);

    private final ModuleRepositoryPort moduleRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public ListModulesService(ModuleRepositoryPort moduleRepositoryPort,
                              AuthorizationPort authorizationPort) {
        this.moduleRepositoryPort = Objects.requireNonNull(moduleRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * {@inheritDoc}
     *
     * <p>Obtiene todos los módulos activos, mapea cada entidad de
     * dominio a un objeto de respuesta y retorna la lista.</p>
     *
     * @return lista de módulos activos
     */
    @Override
    public List<ModuleResponse> execute() {
        authorizationPort.requirePermission(Permission.MODULES_READ);
        var modules = moduleRepositoryPort.findAll();

        log.debug("Listed {} active modules", modules.size());

        return modules.stream()
                .map(module -> new ModuleResponse(
                    module.getId(),
                    module.getNombre(),
                    module.getDescripcion(),
                    module.getIcono(),
                    module.getOrden(),
                    module.getEstado().name(),
                    module.getFechaCreacion()
                ))
                .toList();
    }
}
