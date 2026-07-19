package ec.edu.espe.master_gateway.contexts.auth.application.port.in;

import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.SelectRoleRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.SelectRoleResponse;

/**
 * Caso de uso para la selección de rol.
 *
 * <p>Valida el token temporal, verifica que el usuario tenga el rol
 * solicitado y emite los tokens de acceso y actualización.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface SelectRoleUseCase {
    /**
     * Ejecuta la selección de rol.
     *
     * @param request token temporal y rol seleccionado
     * @return respuesta con tokens de acceso y actualización
     * @throws AuthorizationException si el usuario no posee el rol solicitado
     */
    SelectRoleResponse execute(SelectRoleRequest request);
}
