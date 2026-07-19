package ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representación completa de un usuario para su consulta.
 *
 * <p>Incluye todos los datos relevantes del usuario, como su estado
 * y las fechas de creación y última actualización. Es utilizada como
 * respuesta en los casos de uso de consulta y listado de usuarios.</p>
 *
 * @param id                 identificador único del usuario
 * @param username           nombre de usuario
 * @param email              dirección de correo electrónico
 * @param nombreCompleto     nombre completo del usuario
 * @param estado             estado actual del usuario (ACTIVO/INACTIVO)
 * @param fechaCreacion      fecha y hora de creación del usuario
 * @param fechaActualizacion fecha y hora de la última actualización
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record UserResponse(
    UUID id,
    String username,
    String email,
    String nombreCompleto,
    String estado,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {}
