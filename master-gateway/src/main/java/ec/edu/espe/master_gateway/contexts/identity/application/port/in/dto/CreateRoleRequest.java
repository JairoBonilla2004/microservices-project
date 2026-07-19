package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Solicitud para la creación de un nuevo rol en el sistema.
 *
 * <p>Contiene el nombre y la descripción del rol. El nombre debe ser
 * único y tener una longitud mínima de 2 caracteres. Los campos son
 * validados mediante anotaciones Jakarta Validation.</p>
 *
 * @param nombre      nombre único del rol, debe tener entre 2 y 50 caracteres
 * @param descripcion descripción opcional del propósito del rol
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record CreateRoleRequest(
    @NotBlank @Size(min = 2, max = 50) String nombre,
    String descripcion
) {}
