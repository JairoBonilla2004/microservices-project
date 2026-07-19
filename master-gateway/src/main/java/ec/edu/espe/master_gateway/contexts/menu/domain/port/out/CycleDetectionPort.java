package ec.edu.espe.master_gateway.contexts.menu.domain.port.out;

import java.util.UUID;

/**
 * Puerto para la detección de ciclos en estructuras jerárquicas.
 *
 * <p>Define la operación necesaria para validar que el movimiento de
 * un nodo de menú a un nuevo padre no genere un ciclo en el árbol,
 * garantizando la integridad de la estructura jerárquica.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface CycleDetectionPort {

    boolean wouldCreateCycle(UUID nodeId, UUID newParentId);
}
