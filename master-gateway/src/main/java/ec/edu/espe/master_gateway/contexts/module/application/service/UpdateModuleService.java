package ec.edu.espe.master_gateway.contexts.module.application.service;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.UpdateModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.UpdateModuleRequest;

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
 * Servicio que implementa el caso de uso de actualización de un módulo.
 *
 * <p>Valida los permisos del usuario, busca el módulo por su identificador,
 * aplica los cambios opcionales (nombre, descripción, icono, orden) y
 * persiste el módulo actualizado retornando la respuesta modificada.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class UpdateModuleService implements UpdateModuleUseCase {

    private static final Logger log = LoggerFactory.getLogger(UpdateModuleService.class);

    private final ModuleRepositoryPort moduleRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public UpdateModuleService(ModuleRepositoryPort moduleRepositoryPort,
                               AuthorizationPort authorizationPort) {
        this.moduleRepositoryPort = Objects.requireNonNull(moduleRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public ModuleResponse execute(UUID id, UpdateModuleRequest request) {
        authorizationPort.requirePermission(Permission.MODULES_UPDATE);

        var module = moduleRepositoryPort.findById(id)
                .orElseThrow(() -> new NotFoundException("Module", id));

        if (request.nombre() != null) module.updateNombre(request.nombre());
        if (request.descripcion() != null) module.updateDescripcion(request.descripcion());
        if (request.icono() != null) module.updateIcono(request.icono());
        if (request.orden() != null) module.updateOrden(request.orden());

        module = moduleRepositoryPort.save(module);

        log.info("Module updated: id={}, name={}", module.getId(), module.getNombre());

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
