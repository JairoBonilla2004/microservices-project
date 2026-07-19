package ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Respuesta devuelta tras el registro exitoso de un servicio.
 *
 * <p>Contiene los datos del servicio reci&eacute;n creado, incluyendo su
 * identificador asignado, c&oacute;digo, nombre, URL base, modo de validaci&oacute;n
 * y la fecha de creaci&oacute;n.</p>
 *
 * @param id             Identificador &uacute;nico generado para el servicio.
 * @param serviceCode    C&oacute;digo &uacute;nico del servicio registrado.
 * @param nombre         Nombre descriptivo del servicio.
 * @param baseUrl        URL base del servicio.
 * @param validationMode Modo de validaci&oacute;n configurado.
 * @param fechaCreacion  Fecha y hora en que se cre&oacute; el servicio.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record RegisterServiceResponse(
    UUID id,
    String serviceCode,
    String nombre,
    String baseUrl,
    String validationMode,
    LocalDateTime fechaCreacion
) {}
