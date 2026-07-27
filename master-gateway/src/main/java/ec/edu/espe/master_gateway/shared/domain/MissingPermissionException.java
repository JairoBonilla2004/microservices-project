package ec.edu.espe.master_gateway.shared.domain;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.Set;

/**
 * Excepción lanzada cuando un usuario no tiene un permiso específico.
 *
 * <p>Extiende {@link AuthorizationException} e incluye información
 * estructurada sobre el permiso faltante y los permisos sugeridos
 * (dependencias) que un administrador debería asignar para que la
 * operación funcione correctamente. Esta información se expone al
 * cliente en la respuesta 403 a través de {@code detalles}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class MissingPermissionException extends AuthorizationException {

    private final Permission missingPermission;
    private final Set<Permission> suggestedPermissions;

    public MissingPermissionException(Permission missingPermission) {
        super("Permiso requerido: " + missingPermission.name());
        this.missingPermission = missingPermission;
        this.suggestedPermissions = missingPermission.withDependencies();
        addDetalle("missingPermission", missingPermission.name());
        addDetalle("suggestedPermissions",
                suggestedPermissions.stream().map(Permission::name).toList());
    }

    public Permission getMissingPermission() {
        return missingPermission;
    }

    public Set<Permission> getSuggestedPermissions() {
        return suggestedPermissions;
    }
}
