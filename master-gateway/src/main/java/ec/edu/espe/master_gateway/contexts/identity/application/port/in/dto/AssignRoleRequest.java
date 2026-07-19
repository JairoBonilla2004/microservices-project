package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import java.util.UUID;

/**
 * Solicitud para asignar un rol a un usuario.
 *
 * <p>Contiene los identificadores del usuario y del rol que se desea
 * asignar. Ambos campos son obligatorios y deben corresponder a
 * entidades existentes en el sistema.</p>
 *
 * @param userId identificador único del usuario
 * @param roleId identificador único del rol a asignar
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record AssignRoleRequest(
    UUID userId,
    UUID roleId
) {}
