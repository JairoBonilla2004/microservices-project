package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.in.web;

/**
 * Controlador REST para la gestión de módulos.
 *
 * <p>Proporciona endpoints para el mantenimiento completo de módulos
 * del sistema incluyendo operaciones CRUD, asignación y remoción de
 * módulos a roles. Los módulos representan las secciones principales
 * de la aplicación a las que los roles pueden tener acceso.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.module.application.port.in.AssignModuleToRoleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.CreateModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.DeactivateModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.GetModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.ListModulesUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.ReactivateModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.RemoveModuleFromRoleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.UpdateModuleUseCase;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.AssignModuleToRoleRequest;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.CreateModuleRequest;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.CreateModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.ModuleResponse;
import ec.edu.espe.master_gateway.contexts.module.application.port.in.dto.UpdateModuleRequest;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/modules")
@SuppressWarnings("java:S107")
public class ModuleController {

    private final GetModuleUseCase getModuleUseCase;
    private final ListModulesUseCase listModulesUseCase;
    private final CreateModuleUseCase createModuleUseCase;
    private final UpdateModuleUseCase updateModuleUseCase;
    private final DeactivateModuleUseCase deactivateModuleUseCase;
    private final ReactivateModuleUseCase reactivateModuleUseCase;
    private final AssignModuleToRoleUseCase assignModuleToRoleUseCase;
    private final RemoveModuleFromRoleUseCase removeModuleFromRoleUseCase;

    public ModuleController(GetModuleUseCase getModuleUseCase,
                            ListModulesUseCase listModulesUseCase,
                            CreateModuleUseCase createModuleUseCase,
                            UpdateModuleUseCase updateModuleUseCase,
                            DeactivateModuleUseCase deactivateModuleUseCase,
                            ReactivateModuleUseCase reactivateModuleUseCase,
                            AssignModuleToRoleUseCase assignModuleToRoleUseCase,
                            RemoveModuleFromRoleUseCase removeModuleFromRoleUseCase) {
        this.getModuleUseCase = getModuleUseCase;
        this.listModulesUseCase = listModulesUseCase;
        this.createModuleUseCase = createModuleUseCase;
        this.updateModuleUseCase = updateModuleUseCase;
        this.deactivateModuleUseCase = deactivateModuleUseCase;
        this.reactivateModuleUseCase = reactivateModuleUseCase;
        this.assignModuleToRoleUseCase = assignModuleToRoleUseCase;
        this.removeModuleFromRoleUseCase = removeModuleFromRoleUseCase;
    }

    @GetMapping
    public ResponseEntity<List<ModuleResponse>> listModules() {
        return ResponseEntity.ok(listModulesUseCase.execute());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModuleResponse> getModule(@PathVariable UUID id) {
        return ResponseEntity.ok(getModuleUseCase.execute(id));
    }

    @PostMapping
    public ResponseEntity<CreateModuleResponse> createModule(@RequestBody @Valid CreateModuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(createModuleUseCase.execute(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModuleResponse> updateModule(@PathVariable UUID id,
                                                       @RequestBody @Valid UpdateModuleRequest request) {
        return ResponseEntity.ok(updateModuleUseCase.execute(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateModule(@PathVariable UUID id) {
        deactivateModuleUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivate")
    public ResponseEntity<Void> reactivateModule(@PathVariable UUID id) {
        reactivateModuleUseCase.execute(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/roles/{roleId}/modules")
    public ResponseEntity<Void> assignModuleToRole(@PathVariable UUID roleId,
                                                   @RequestBody @Valid AssignModuleToRoleRequest request) {
        assignModuleToRoleUseCase.execute(new AssignModuleToRoleRequest(roleId, request.moduleId()));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/roles/{roleId}/modules/{moduleId}")
    public ResponseEntity<Void> removeModuleFromRole(@PathVariable UUID roleId, @PathVariable UUID moduleId) {
        removeModuleFromRoleUseCase.execute(roleId, moduleId);
        return ResponseEntity.noContent().build();
    }
}
