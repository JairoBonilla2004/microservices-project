package ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Solicitud para actualizar un servicio existente en el registro.
 *
 * <p>Permite modificar el nombre y/o la URL base de un servicio
 * previamente registrado. Todos los campos son opcionales; solo se
 * actualizar&aacute;n aquellos que no sean {@code null}.</p>
 *
 * @param nombre  Nuevo nombre del servicio (2-100 caracteres, opcional).
 * @param baseUrl Nueva URL base del servicio, debe comenzar con http:// o https:// (opcional).
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record UpdateServiceRequest(
    @Size(min = 2, max = 100) String nombre,
    @Pattern(regexp = "^https?://.+") String baseUrl
) {}
