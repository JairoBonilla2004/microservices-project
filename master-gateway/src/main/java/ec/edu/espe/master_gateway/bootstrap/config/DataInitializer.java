package ec.edu.espe.master_gateway.bootstrap.config;

/**
 * Inicializador de datos semilla para la aplicación.
 *
 * <p>Escucha el evento {@link org.springframework.boot.context.event.ApplicationReadyEvent}
 * para crear el rol de administrador ({@code ADMIN}) con todos los permisos del sistema,
 * así como el usuario administrador por defecto ({@code boss_admin}) con su respectiva
 * asignación de rol.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.identity.domain.model.Role;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.RolePermissionAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.UserRoleAssignment;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.PasswordHasherPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RolePermissionAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.RoleRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataInitializer {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private static final String ADMIN_USERNAME = "boss_admin";

    private final RoleRepositoryPort roleRepository;
    private final UserRepositoryPort userRepository;
    private final UserRoleAssignmentRepositoryPort assignmentRepository;
    private final RolePermissionAssignmentRepositoryPort permissionAssignmentRepository;
    private final PasswordHasherPort passwordHasher;

    public DataInitializer(RoleRepositoryPort roleRepository,
                           UserRepositoryPort userRepository,
                           UserRoleAssignmentRepositoryPort assignmentRepository,
                           RolePermissionAssignmentRepositoryPort permissionAssignmentRepository,
                           PasswordHasherPort passwordHasher) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.assignmentRepository = assignmentRepository;
        this.permissionAssignmentRepository = permissionAssignmentRepository;
        this.passwordHasher = passwordHasher;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void init() {
        ensureAdminRole();
        ensureRegisteredUserRole();
    }

    private void ensureRegisteredUserRole() {
        var existing = roleRepository.findAllActive().stream()
                .filter(r -> "REGISTERED_USER".equalsIgnoreCase(r.getNombre()))
                .findFirst();
        if (existing.isEmpty()) {
            var role = new Role("REGISTERED_USER", "Rol por defecto para usuarios registrados");
            roleRepository.save(role);
            log.info("Rol REGISTERED_USER creado");
        } else {
            log.debug("Rol REGISTERED_USER ya existe");
        }
    }

    private void ensureAdminRole() {
        var existingRole = roleRepository.findAllActive().stream()
                .filter(r -> "ADMIN".equalsIgnoreCase(r.getNombre()))
                .findFirst();

        UUID adminRoleId;
        if (existingRole.isPresent()) {
            adminRoleId = existingRole.get().getId();
            log.info("Rol ADMIN ya existe (ID={})", adminRoleId);
        } else {
            var adminRole = new Role("ADMIN", "Rol de administrador con todos los permisos del sistema");
            var saved = roleRepository.save(adminRole);
            adminRoleId = saved.getId();
            log.info("Rol ADMIN creado (ID={})", adminRoleId);
        }

        var existingPerms = permissionAssignmentRepository.findPermissionsByRoleId(adminRoleId);
        if (existingPerms.isEmpty()) {
            log.info("Sembrando {} permisos para ADMIN...", Permission.values().length);
            for (Permission p : Permission.values()) {
                permissionAssignmentRepository.save(
                        new RolePermissionAssignment(adminRoleId, p, "SYSTEM"));
            }
            log.info("Permisos de ADMIN inicializados");
        } else {
            log.info("{} permisos de ADMIN ya existen en BD", existingPerms.size());
        }

        if (userRepository.findByUsername(ADMIN_USERNAME).isEmpty()) {
            var adminUser = new User(ADMIN_USERNAME, "admin@sistema.com",
                    passwordHasher.hash("Admin1234"), "Administrador del Sistema");
            var savedUser = userRepository.save(adminUser);
            assignmentRepository.save(
                    new UserRoleAssignment(savedUser.getId(), adminRoleId, "SYSTEM"));
            log.info("Usuario '{}' creado con rol ADMIN", ADMIN_USERNAME);
        } else {
            log.info("Usuario '{}' ya existe", ADMIN_USERNAME);
        }
    }
}
