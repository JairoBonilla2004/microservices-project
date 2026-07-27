package ec.edu.espe.master_gateway.shared.domain.permission;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Enumeración de permisos del sistema.
 *
 * <p>Define todos los permisos disponibles para las operaciones CRUD
 * sobre las entidades del sistema (usuarios, roles, módulos, menús y
 * servicios), así como permisos especiales para asignación de roles
 * y recursos. Cada permiso puede declarar dependencias directas de otros
 * permisos (por ejemplo, {@code MODULES_ASSIGN} depende de
 * {@code MODULES_READ} y {@code ROLES_READ}).</p>
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
    SERVICES_DELETE;

    private static final Map<Permission, Permission[]> DEPENDENCIES = buildDependencies();

    private static Map<Permission, Permission[]> buildDependencies() {
        Map<Permission, Permission[]> deps = new EnumMap<>(Permission.class);
        deps.put(USERS_CREATE,       new Permission[]{USERS_READ});
        deps.put(USERS_UPDATE,       new Permission[]{USERS_READ});
        deps.put(USERS_DELETE,       new Permission[]{USERS_READ});
        deps.put(USERS_ASSIGN_ROLE,  new Permission[]{USERS_READ, ROLES_READ});
        deps.put(USERS_REVOKE_ROLE,  new Permission[]{USERS_READ, ROLES_READ});
        deps.put(ROLES_CREATE,       new Permission[]{ROLES_READ});
        deps.put(ROLES_UPDATE,       new Permission[]{ROLES_READ});
        deps.put(ROLES_DELETE,       new Permission[]{ROLES_READ});
        deps.put(ROLES_ASSIGN_USERS, new Permission[]{ROLES_READ, USERS_READ});
        deps.put(MODULES_CREATE,     new Permission[]{MODULES_READ});
        deps.put(MODULES_UPDATE,     new Permission[]{MODULES_READ});
        deps.put(MODULES_DELETE,     new Permission[]{MODULES_READ});
        deps.put(MODULES_ASSIGN,     new Permission[]{MODULES_READ, ROLES_READ});
        deps.put(MENUS_CREATE,       new Permission[]{MENUS_READ, MODULES_READ});
        deps.put(MENUS_UPDATE,       new Permission[]{MENUS_READ});
        deps.put(MENUS_DELETE,       new Permission[]{MENUS_READ});
        deps.put(MENUS_ASSIGN,       new Permission[]{MENUS_READ, ROLES_READ});
        deps.put(SERVICES_CREATE,    new Permission[]{SERVICES_READ});
        deps.put(SERVICES_UPDATE,    new Permission[]{SERVICES_READ});
        deps.put(SERVICES_DELETE,    new Permission[]{SERVICES_READ});
        return deps;
    }

    Permission() {
    }

    /**
     * Retorna las dependencias directas de este permiso.
     */
    public Permission[] getDependencies() {
        Permission[] result = DEPENDENCIES.get(this);
        return result != null ? result.clone() : new Permission[0];
    }

    /**
     * Retorna el conjunto completo de permisos requeridos, incluyendo
     * dependencias transitivas resueltas recursivamente. No incluye
     * el propio permiso.
     */
    public Set<Permission> withDependencies() {
        Set<Permission> all = new HashSet<>();
        collectDependencies(all);
        return Set.copyOf(all);
    }

    private void collectDependencies(Set<Permission> acc) {
        Permission[] deps = DEPENDENCIES.get(this);
        if (deps == null) return;
        for (Permission dep : deps) {
            if (acc.add(dep)) {
                dep.collectDependencies(acc);
            }
        }
    }
}
