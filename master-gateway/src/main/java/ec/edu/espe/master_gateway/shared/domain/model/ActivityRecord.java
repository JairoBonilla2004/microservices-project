package ec.edu.espe.master_gateway.shared.domain.model;

import java.time.LocalDateTime;

/**
 * Registro de actividad de dominio.
 *
 * <p>Representa un cambio reciente sobre una entidad auditable del sistema,
 * capturando su nombre, el responsable de la última operación y las marcas
 * de tiempo de creación y actualización. Es una abstracción transversal a
 * los distintos contextos delimitados.</p>
 *
 * @param name nombre legible de la entidad.
 * @param actor usuario responsable de la última operación.
 * @param createdAt fecha de creación del registro.
 * @param updatedAt fecha de la última actualización del registro.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record ActivityRecord(String name, String actor, LocalDateTime createdAt, LocalDateTime updatedAt) {
}
