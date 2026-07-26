package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.in.web;

/**
 * Controlador REST para la gestión del árbol de menús.
 *
 * <p>Proporciona endpoints para el mantenimiento completo de la estructura
 * jerárquica de menús incluyendo operaciones CRUD, consulta del árbol
 * por rol, reordenamiento de nodos mediante movimiento, asignación y
 * remoción de menús a roles. La integridad del árbol se garantiza mediante
 * detección de ciclos en operaciones de movimiento.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.AssignMenuToRoleUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.CreateMenuItemUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.DeactivateMenuItemUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.GetAllMenuItemsUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.GetMenuTreeUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.MoveMenuItemUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.RemoveMenuFromRoleUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.UpdateMenuItemUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.AssignMenuToRoleRequest;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.CreateMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuItemResponse;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuNodeResponse;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MoveMenuItemRequest;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.UpdateMenuItemRequest;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/menus")
@SuppressWarnings("java:S107")
public class MenuController {

    private final GetMenuTreeUseCase getMenuTreeUseCase;
    private final GetAllMenuItemsUseCase getAllMenuItemsUseCase;
    private final CreateMenuItemUseCase createMenuItemUseCase;
    private final UpdateMenuItemUseCase updateMenuItemUseCase;
    private final DeactivateMenuItemUseCase deactivateMenuItemUseCase;
    private final MoveMenuItemUseCase moveMenuItemUseCase;
    private final AssignMenuToRoleUseCase assignMenuToRoleUseCase;
    private final RemoveMenuFromRoleUseCase removeMenuFromRoleUseCase;

    public MenuController(GetMenuTreeUseCase getMenuTreeUseCase,
                          GetAllMenuItemsUseCase getAllMenuItemsUseCase,
                          CreateMenuItemUseCase createMenuItemUseCase,
                          UpdateMenuItemUseCase updateMenuItemUseCase,
                          DeactivateMenuItemUseCase deactivateMenuItemUseCase,
                          MoveMenuItemUseCase moveMenuItemUseCase,
                          AssignMenuToRoleUseCase assignMenuToRoleUseCase,
                          RemoveMenuFromRoleUseCase removeMenuFromRoleUseCase) {
        this.getMenuTreeUseCase = getMenuTreeUseCase;
        this.getAllMenuItemsUseCase = getAllMenuItemsUseCase;
        this.createMenuItemUseCase = createMenuItemUseCase;
        this.updateMenuItemUseCase = updateMenuItemUseCase;
        this.deactivateMenuItemUseCase = deactivateMenuItemUseCase;
        this.moveMenuItemUseCase = moveMenuItemUseCase;
        this.assignMenuToRoleUseCase = assignMenuToRoleUseCase;
        this.removeMenuFromRoleUseCase = removeMenuFromRoleUseCase;
    }

    @GetMapping
    public ResponseEntity<List<MenuItemResponse>> getAllMenuItems() {
        return ResponseEntity.ok(getAllMenuItemsUseCase.execute());
    }

    @GetMapping("/tree")
    public ResponseEntity<List<MenuNodeResponse>> getMenuTree(@RequestParam UUID roleId) {
        return ResponseEntity.ok(getMenuTreeUseCase.execute(roleId));
    }

    @PostMapping
    public ResponseEntity<MenuItemResponse> createMenuItem(@RequestBody @Valid CreateMenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createMenuItemUseCase.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuItemResponse> updateMenuItem(@PathVariable UUID id,
                                                           @RequestBody @Valid UpdateMenuItemRequest request) {
        return ResponseEntity.ok(updateMenuItemUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateMenuItem(@PathVariable UUID id) {
        deactivateMenuItemUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/move")
    public ResponseEntity<Void> moveMenuItem(@PathVariable UUID id,
                                             @RequestBody @Valid MoveMenuItemRequest request) {
        moveMenuItemUseCase.execute(new MoveMenuItemRequest(id, request.newParentId()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/roles/{roleId}/menus")
    public ResponseEntity<Void> assignMenuToRole(@PathVariable UUID roleId,
                                                 @RequestBody @Valid AssignMenuToRoleRequest request) {
        assignMenuToRoleUseCase.execute(new AssignMenuToRoleRequest(roleId, request.menuNodeId()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/roles/{roleId}/menus/{menuId}")
    public ResponseEntity<Void> removeMenuFromRole(@PathVariable UUID roleId, @PathVariable UUID menuId) {
        removeMenuFromRoleUseCase.execute(roleId, menuId);
        return ResponseEntity.noContent().build();
    }
}
