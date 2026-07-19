package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserResponse;

/**
 * Caso de uso para la creación administrativa de un nuevo usuario en el sistema.
 *
 * <p>Permite a un administrador registrar un nuevo usuario, asignando roles y
 * permisos de forma explícita. Este caso de uso está destinado únicamente a
 * usuarios con privilegios administrativos.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface AdminCreateUserUseCase {
    CreateUserResponse execute(CreateUserRequest request);
}
