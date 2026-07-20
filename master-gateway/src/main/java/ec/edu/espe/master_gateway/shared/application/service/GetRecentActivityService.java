package ec.edu.espe.master_gateway.shared.application.service;

import ec.edu.espe.master_gateway.shared.application.port.in.GetRecentActivityUseCase;
import ec.edu.espe.master_gateway.shared.application.port.in.dto.ActivityEntryResponse;
import ec.edu.espe.master_gateway.shared.domain.model.ActivityRecord;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.domain.port.out.RecentActivityPort;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio que obtiene la actividad reciente del sistema.
 *
 * <p>Requiere el permiso {@link Permission#USERS_READ} como proxy de
 * visibilidad administrativa, consulta los registros más recientes de
 * cada tipo de entidad, los combina, ordena por fecha de actualización
 * descendente y mapea a {@link ActivityEntryResponse}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
@Transactional(readOnly = true)
public class GetRecentActivityService implements GetRecentActivityUseCase {

    private final RecentActivityPort recentActivityPort;
    private final AuthorizationPort authorizationPort;

    public GetRecentActivityService(RecentActivityPort recentActivityPort,
                                    AuthorizationPort authorizationPort) {
        this.recentActivityPort = Objects.requireNonNull(recentActivityPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ActivityEntryResponse> execute(int limit) {
        authorizationPort.requirePermission(Permission.USERS_READ);

        List<ActivityEntryResponse> combined = new ArrayList<>();
        combined.addAll(mapAll(recentActivityPort.findRecentUsers(limit), "Usuario"));
        combined.addAll(mapAll(recentActivityPort.findRecentRoles(limit), "Rol"));
        combined.addAll(mapAll(recentActivityPort.findRecentModules(limit), "Módulo"));
        combined.addAll(mapAll(recentActivityPort.findRecentMenuItems(limit), "Menú"));
        combined.addAll(mapAll(recentActivityPort.findRecentServices(limit), "Servicio"));

        return combined.stream()
                .sorted(Comparator.comparing(ActivityEntryResponse::timestamp,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(limit)
                .toList();
    }

    private List<ActivityEntryResponse> mapAll(List<ActivityRecord> records, String entityType) {
        return records.stream().map(toResponse(entityType)).toList();
    }

    private Function<ActivityRecord, ActivityEntryResponse> toResponse(String entityType) {
        return record -> {
            LocalDateTime created = record.createdAt();
            LocalDateTime updated = record.updatedAt();
            LocalDateTime timestamp = updated != null ? updated : created;
            String action = (created != null && created.equals(updated)) ? "creado" : "actualizado";
            return new ActivityEntryResponse(
                    entityType,
                    record.name(),
                    action,
                    record.actor(),
                    timestamp);
        };
    }
}
