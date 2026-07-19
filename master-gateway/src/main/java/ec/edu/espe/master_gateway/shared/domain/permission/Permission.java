package ec.edu.espe.master_gateway.shared.domain.permission;

/**
 * Enumeración de permisos del sistema.
 *
 * <p>Define todos los permisos disponibles para las operaciones CRUD
 * sobre las entidades del sistema (usuarios, roles, módulos, menús y
 * servicios), así como permisos especiales para asignación de roles
 * y recursos.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public enum Permission {

    USERS_CREATE,
    USERS_READ,
    USERS_UPDATE,
    USERS_DELETE,
    USERS_ASSIGN_ROLE,
    USERS_REVOKE_ROLE,

    ROLES_CREATE,
    ROLES_READ,
    ROLES_UPDATE,
    ROLES_DELETE,
    ROLES_ASSIGN_USERS,

    MODULES_CREATE,
    MODULES_READ,
    MODULES_UPDATE,
    MODULES_DELETE,
    MODULES_ASSIGN,

    MENUS_CREATE,
    MENUS_READ,
    MENUS_UPDATE,
    MENUS_DELETE,
    MENUS_ASSIGN,

    SERVICES_CREATE,
    SERVICES_READ,
    SERVICES_UPDATE,
    SERVICES_DELETE,
}
