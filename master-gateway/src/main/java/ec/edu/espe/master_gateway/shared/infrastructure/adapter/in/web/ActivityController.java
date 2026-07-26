package ec.edu.espe.master_gateway.shared.infrastructure.adapter.in.web;

import ec.edu.espe.master_gateway.shared.application.port.in.GetRecentActivityUseCase;
import ec.edu.espe.master_gateway.shared.application.port.in.dto.ActivityEntryResponse;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para la actividad reciente del sistema.
 *
 * <p>Expone la consulta de los últimos cambios registrados en las
 * entidades auditables, con un límite configurable y acotado.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@RestController
@RequestMapping("/api/activity")
public class ActivityController {

    private static final int MAX_LIMIT = 50;

    private final GetRecentActivityUseCase getRecentActivityUseCase;

    public ActivityController(GetRecentActivityUseCase getRecentActivityUseCase) {
        this.getRecentActivityUseCase = getRecentActivityUseCase;
    }

    @GetMapping("/recent")
    public ResponseEntity<List<ActivityEntryResponse>> getRecentActivity(
            @RequestParam(defaultValue = "10") int limit) {
        int safeLimit = Math.clamp(limit, 1, MAX_LIMIT);
        return ResponseEntity.ok(getRecentActivityUseCase.execute(safeLimit));
    }
}
