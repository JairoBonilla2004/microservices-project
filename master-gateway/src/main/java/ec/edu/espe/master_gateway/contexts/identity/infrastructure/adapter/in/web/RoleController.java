package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.in.web;

/**
 * Controlador REST para la gestión de roles.
 *
 * <p>Proporciona endpoints para el mantenimiento completo de roles
 * incluyendo operaciones CRUD, consulta de usuarios asignados, asignación
 * y revocación de usuarios a roles. Todos los endpoints requieren
 * autenticación JWT y autorización basada en roles.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.AssignPermissionToRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.AssignRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.CreateRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.DeactivateRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetRolePermissionsUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.GetRoleUsersUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.ListRolesUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.RemovePermissionFromRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.RevokeRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.UpdateRoleUseCase;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignPermissionToRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.AssignRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.CreateRoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UpdateRoleRequest;
import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.UserResponse;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
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
@RequestMapping("/api/roles")
public class RoleController {

    private final ListRolesUseCase listRolesUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final CreateRoleUseCase createRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeactivateRoleUseCase deactivateRoleUseCase;
    private final GetRoleUsersUseCase getRoleUsersUseCase;
    private final AssignRoleUseCase assignRoleUseCase;
    private final RevokeRoleUseCase revokeRoleUseCase;
    private final GetRolePermissionsUseCase getRolePermissionsUseCase;
    private final AssignPermissionToRoleUseCase assignPermissionToRoleUseCase;
    private final RemovePermissionFromRoleUseCase removePermissionFromRoleUseCase;

    public RoleController(ListRolesUseCase listRolesUseCase,
                          GetRoleUseCase getRoleUseCase,
                          CreateRoleUseCase createRoleUseCase,
                          UpdateRoleUseCase updateRoleUseCase,
                          DeactivateRoleUseCase deactivateRoleUseCase,
                          GetRoleUsersUseCase getRoleUsersUseCase,
                          AssignRoleUseCase assignRoleUseCase,
                          RevokeRoleUseCase revokeRoleUseCase,
                          GetRolePermissionsUseCase getRolePermissionsUseCase,
                          AssignPermissionToRoleUseCase assignPermissionToRoleUseCase,
                          RemovePermissionFromRoleUseCase removePermissionFromRoleUseCase) {
        this.listRolesUseCase = listRolesUseCase;
        this.getRoleUseCase = getRoleUseCase;
        this.createRoleUseCase = createRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.deactivateRoleUseCase = deactivateRoleUseCase;
        this.getRoleUsersUseCase = getRoleUsersUseCase;
        this.assignRoleUseCase = assignRoleUseCase;
        this.revokeRoleUseCase = revokeRoleUseCase;
        this.getRolePermissionsUseCase = getRolePermissionsUseCase;
        this.assignPermissionToRoleUseCase = assignPermissionToRoleUseCase;
        this.removePermissionFromRoleUseCase = removePermissionFromRoleUseCase;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> listRoles() {
        return ResponseEntity.ok(listRolesUseCase.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> getRole(@PathVariable UUID id) {
        return ResponseEntity.ok(getRoleUseCase.execute(id));
    }

    @PostMapping
    public ResponseEntity<CreateRoleResponse> createRole(@RequestBody @Valid CreateRoleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createRoleUseCase.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoleResponse> updateRole(@PathVariable UUID id,
                                                   @RequestBody @Valid UpdateRoleRequest request) {
        return ResponseEntity.ok(updateRoleUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateRole(@PathVariable UUID id) {
        deactivateRoleUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/users")
    public ResponseEntity<List<UserResponse>> getRoleUsers(@PathVariable UUID id) {
        return ResponseEntity.ok(getRoleUsersUseCase.execute(id));
    }

    @PostMapping("/{id}/users")
    public ResponseEntity<Void> assignUser(@PathVariable UUID id,
                                           @RequestBody @Valid AssignRoleRequest request) {
        assignRoleUseCase.execute(new AssignRoleRequest(request.userId(), id));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/users/{userId}")
    public ResponseEntity<Void> revokeUser(@PathVariable UUID id, @PathVariable UUID userId) {
        revokeRoleUseCase.execute(userId, id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/permissions")
    public ResponseEntity<List<Permission>> getRolePermissions(@PathVariable UUID id) {
        return ResponseEntity.ok(getRolePermissionsUseCase.execute(id));
    }

    @PostMapping("/{id}/permissions")
    public ResponseEntity<Void> assignPermission(@PathVariable UUID id,
                                                  @RequestBody @Valid AssignPermissionToRoleRequest request) {
        assignPermissionToRoleUseCase.execute(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/permissions/{permission}")
    public ResponseEntity<Void> removePermission(@PathVariable UUID id,
                                                  @PathVariable Permission permission) {
        removePermissionFromRoleUseCase.execute(id, permission);
        return ResponseEntity.noContent().build();
    }
}
