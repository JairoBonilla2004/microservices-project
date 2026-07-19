package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import java.util.UUID;

/**
 * Caso de uso para la desactivación de un usuario en el sistema.
 *
 * <p>Marca a un usuario como inactivo, impidiendo que pueda iniciar
 * sesión o realizar operaciones en el sistema. El usuario no es
 * eliminado físicamente, solo se cambia su estado.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface DeactivateUserUseCase {
    /**
     * Ejecuta la desactivación de un usuario.
     *
     * @param id identificador único del usuario a desactivar
     */
    void execute(UUID id);
}
