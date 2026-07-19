package ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representaci&oacute;n completa de un servicio registrado en el sistema.
 *
 * <p>Contiene toda la informaci&oacute;n de un servicio, incluyendo su estado
 * actual, fechas de creaci&oacute;n y &uacute;ltima actualizaci&oacute;n, y la clave
 * p&uacute;blica asociada.</p>
 *
 * @param id                 Identificador &uacute;nico del servicio.
 * @param serviceCode        C&oacute;digo &uacute;nico del servicio.
 * @param nombre             Nombre descriptivo del servicio.
 * @param baseUrl            URL base del servicio.
 * @param validationMode     Modo de validaci&oacute;n del servicio.
 * @param publicKey          Clave p&uacute;blica del servicio (puede ser {@code null}).
 * @param estado             Estado actual del servicio (ACTIVE, INACTIVE).
 * @param fechaCreacion      Fecha y hora de creaci&oacute;n del servicio.
 * @param fechaActualizacion Fecha y hora de la &uacute;ltima actualizaci&oacute;n.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record ServiceResponse(
    UUID id,
    String serviceCode,
    String nombre,
    String baseUrl,
    String validationMode,
    String publicKey,
    String estado,
    LocalDateTime fechaCreacion,
    LocalDateTime fechaActualizacion
) {}
