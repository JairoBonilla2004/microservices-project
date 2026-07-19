package ec.edu.espe.master_gateway.contexts.module.application.service;

import ec.edu.espe.master_gateway.contexts.module.application.port.in.CreateModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.CreateModuleRequest;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.CreateModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.domain.model.Module;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.ModuleRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.DuplicateException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que implementa el caso de uso de creación de un módulo.
 *
 * <p>Valida los permisos del usuario, verifica que no exista un módulo con
 * el mismo nombre, crea la entidad de dominio, la persiste y retorna la
 * respuesta con los datos del módulo creado.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional
public class CreateModuleService implements CreateModuleUseCase {

    private static final Logger log = LoggerFactory.getLogger(CreateModuleService.class);

    private final ModuleRepositoryPort moduleRepositoryPort;
    private final AuthorizationPort authorizationPort;

    public CreateModuleService(ModuleRepositoryPort moduleRepositoryPort,
                               AuthorizationPort authorizationPort) {
        this.moduleRepositoryPort = Objects.requireNonNull(moduleRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    @Override
    public CreateModuleResponse execute(CreateModuleRequest request) {
        authorizationPort.requirePermission(Permission.MODULES_CREATE);

        if (moduleRepositoryPort.existsByNombre(request.nombre())) {
            throw new DuplicateException("Module", "nombre", request.nombre());
        }

        Module module = new Module(
            request.nombre(),
            request.descripcion() != null ? request.descripcion() : "",
            request.icono(),
            request.orden()
        );

        Module saved = moduleRepositoryPort.save(module);

        log.info("Module created: id={}, name={}", saved.getId(), saved.getNombre());

        return new CreateModuleResponse(
            saved.getId(),
            saved.getNombre(),
            saved.getDescripcion(),
            saved.getIcono(),
            saved.getOrden(),
            saved.getFechaCreacion()
        );
    }
}
