package ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto;

import java.util.UUID;

/**
 * Solicitud para mover un nodo de menú a un nuevo padre.
 *
 * @param nodeId      Identificador del nodo que se desea mover.
 * @param newParentId Identificador del nuevo nodo padre.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record MoveMenuItemRequest(
    UUID nodeId,
    UUID newParentId
) {}
