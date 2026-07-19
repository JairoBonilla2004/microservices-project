package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.in.web;

/**
 * Controlador REST para la gestión de usuarios.
 *
 * <p>Proporciona endpoints para el mantenimiento completo de usuarios
 * incluyendo operaciones CRUD, consulta de roles asignados, asignación
 * y revocación de roles. Todos los endpoints requieren autenticación
 * JWT y autorización basada en roles según la configuración de seguridad.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.AdminCreateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.AssignRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.DeactivateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetUserRolesUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.ListUsersUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.RevokeRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.UpdateUserUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateUserResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateUserRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateUserResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final ListUsersUseCase listUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final AdminCreateUserUseCase adminCreateUserUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeactivateUserUseCase deactivateUserUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final RevokeRoleUseCase revokeRoleUseCase;
    private final GetUserRolesUseCase getUserRolesUseCase;

    public UserController(ListUsersUseCase listUsersUseCase,
                          GetUserUseCase getUserUseCase,
                          AdminCreateUserUseCase adminCreateUserUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeactivateUserUseCase deactivateUserUseCase,
                          AssignRoleUseCase assignRoleUseCase,
                          RevokeRoleUseCase revokeRoleUseCase,
                          GetUserRolesUseCase getUserRolesUseCase) {
        this.listUsersUseCase = listUsersUseCase;
        this.getUserUseCase = getUserUseCase;
        this.adminCreateUserUseCase = adminCreateUserUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deactivateUserUseCase = deactivateUserUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
        this.revokeRoleUseCase = revokeRoleUseCase;
        this.getUserRolesUseCase = getUserRolesUseCase;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> listUsers() {
        return ResponseEntity.ok(listUsersUseCase.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(getUserUseCase.execute(id));
    }

    @PostMapping
    public ResponseEntity<CreateUserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminCreateUserUseCase.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UpdateUserResponse> updateUser(@PathVariable UUID id,
                                                         @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(updateUserUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateUser(@PathVariable UUID id) {
        deactivateUserUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<List<RoleResponse>> getUserRoles(@PathVariable UUID id) {
        return ResponseEntity.ok(getUserRolesUseCase.execute(id));
    }

    @PostMapping("/{id}/roles")
    public ResponseEntity<Void> assignRole(@PathVariable UUID id,
                                           @RequestBody @Valid AssignRoleRequest request) {
        assignRoleUseCase.execute(new AssignRoleRequest(id, request.roleId()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/roles/{roleId}")
    public ResponseEntity<Void> revokeRole(@PathVariable UUID id, @PathVariable UUID roleId) {
        revokeRoleUseCase.execute(id, roleId);
        return ResponseEntity.noContent().build();
    }
}
