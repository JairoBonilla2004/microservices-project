package ec.edu.espe.master_gateway.shared.application.port.in;

import ec.edu.espe.master_gateway.shared.application.port.in.dto.ActivityEntryResponse;
import java.util.List;

/**
 * Caso de uso para obtener la actividad reciente del sistema.
 *
 * <p>Recupera los cambios más recientes registrados en las distintas
 * entidades auditables, ordenados por fecha de actualización descendente.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface GetRecentActivityUseCase {

    List<ActivityEntryResponse> execute(int limit);
}
