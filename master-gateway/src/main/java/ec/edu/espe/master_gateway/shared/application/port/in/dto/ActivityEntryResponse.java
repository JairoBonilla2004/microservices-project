package ec.edu.espe.master_gateway.shared.application.port.in.dto;

import java.time.LocalDateTime;

/**
 * Respuesta con una entrada de actividad reciente.
 *
 * <p>Describe un cambio ocurrido sobre una entidad del sistema para su
 * presentación en la interfaz de usuario.</p>
 *
 * @param entityType tipo de entidad (ej. "Usuario", "Rol", "Módulo", "Menú", "Servicio").
 * @param entityName nombre legible de la entidad afectada.
 * @param action acción realizada ("creado" o "actualizado").
 * @param actor usuario responsable de la operación.
 * @param timestamp marca de tiempo de la operación.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record ActivityEntryResponse(String entityType, String entityName, String action, String actor,
                                    LocalDateTime timestamp) {
}
