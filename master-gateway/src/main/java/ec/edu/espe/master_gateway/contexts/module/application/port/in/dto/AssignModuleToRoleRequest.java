package ec.edu.espe.master_gateway.contexts.module.application.port.in.dto;

import java.util.UUID;

/**
 * Solicitud para asignar un módulo existente a un rol del sistema.
 *
 * @param roleId   identificador del rol al que se asignará el módulo
 * @param moduleId identificador del módulo que se asignará al rol
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record AssignModuleToRoleRequest(
    UUID roleId,
    UUID moduleId
) {}
