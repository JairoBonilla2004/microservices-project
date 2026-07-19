package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import jakarta.validation.constraints.NotNull;

/**
 * Solicitud para asignar un permiso a un rol.
 *
 * <p>Contiene el permiso que se desea asignar a un rol del sistema.
 * El permiso debe ser una instancia válida del dominio de permisos.</p>
 *
 * @param permission permiso a asignar al rol
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record AssignPermissionToRoleRequest(
    @NotNull Permission permission
) {}
