package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import java.util.UUID;

/**
 * Información básica de un rol.
 *
 * <p>Representa un rol asociado a un usuario, incluyendo su identificador
 * y nombre descriptivo.</p>
 *
 * @param roleId identificador único del rol
 * @param nombre nombre del rol
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record RoleInfo(UUID roleId, String nombre) {
}
