package ec.edu.espe.master_gateway.shared.domain.port.out;

import ec.edu.espe.master_gateway.shared.domain.model.ActivityRecord;
import java.util.List;

/**
 * Puerto de salida para la consulta de actividad reciente.
 *
 * <p>Define un método por tipo de entidad auditable del sistema para
 * recuperar los registros modificados más recientemente. La implementación
 * concreta consulta cada contexto delimitado correspondiente.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface RecentActivityPort {

    List<ActivityRecord> findRecentUsers(int limit);

    List<ActivityRecord> findRecentRoles(int limit);

    List<ActivityRecord> findRecentModules(int limit);

    List<ActivityRecord> findRecentMenuItems(int limit);

    List<ActivityRecord> findRecentServices(int limit);
}
