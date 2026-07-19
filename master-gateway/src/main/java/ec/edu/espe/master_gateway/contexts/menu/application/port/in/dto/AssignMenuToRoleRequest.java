package ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto;

import java.util.UUID;

/**
 * Solicitud para asignar un nodo de menú a un rol.
 *
 * @param roleId     Identificador del rol al que se asignará el menú.
 * @param menuNodeId Identificador del nodo de menú que se asignará al rol.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record AssignMenuToRoleRequest(
    UUID roleId,
    UUID menuNodeId
) {}
