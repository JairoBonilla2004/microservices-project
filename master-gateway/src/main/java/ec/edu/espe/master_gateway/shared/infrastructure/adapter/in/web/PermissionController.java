package ec.edu.espe.master_gateway.shared.infrastructure.adapter.in.web;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import java.util.Arrays;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone metadatos del sistema de permisos.
 *
 * <p>Proporciona información sobre la estructura de permisos,
 * incluyendo las dependencias entre ellos. El frontend consume este
 * endpoint para saber qué permisos adicionales requiere una operación
 * y poder guiar al administrador durante la asignación de roles.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    @GetMapping("/metadata")
    public ResponseEntity<List<PermissionMetadata>> getMetadata() {
        List<PermissionMetadata> metadata = Arrays.stream(Permission.values())
                .map(p -> new PermissionMetadata(
                        p.name(),
                        Arrays.stream(p.getDependencies()).map(Permission::name).toList(),
                        p.withDependencies().stream().map(Permission::name).toList()))
                .toList();
        return ResponseEntity.ok(metadata);
    }

    private record PermissionMetadata(
            String permission,
            List<String> dependencies,
            List<String> allDependencies) {
    }
}
