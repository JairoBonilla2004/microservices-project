package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import jakarta.validation.constraints.Size;

/**
 * Solicitud para la actualización de un rol existente.
 *
 * <p>Permite modificar el nombre y la descripción de un rol. Ambos
 * campos son opcionales; solo se actualizarán aquellos que se
 * proporcionen en la solicitud. El nombre, si se envía, debe tener
 * entre 2 y 50 caracteres.</p>
 *
 * @param nombre      nuevo nombre del rol, debe tener entre 2 y 50 caracteres (opcional)
 * @param descripcion nueva descripción del rol (opcional)
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record UpdateRoleRequest(
    @Size(min = 2, max = 50) String nombre,
    String descripcion
) {}
