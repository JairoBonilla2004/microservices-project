package ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Solicitud para registrar un nuevo servicio en el registro de servicios.
 *
 * <p>Contiene los datos necesarios para crear un microservicio dentro del
 * sistema, incluyendo su identificador &uacute;nico, nombre, URL base, modo de
 * validaci&oacute;n y clave p&uacute;blica opcional.</p>
 *
 * @param serviceCode    C&oacute;digo &uacute;nico del servicio (2-50 caracteres).
 * @param nombre         Nombre descriptivo del servicio (2-100 caracteres).
 * @param baseUrl        URL base del servicio, debe comenzar con http:// o https://.
 * @param validationMode Modo de validaci&oacute;n del servicio (ej. NONE, PUBLIC_KEY).
 * @param publicKey      Clave p&uacute;blica opcional para el modo de validaci&oacute;n.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record RegisterServiceRequest(
    @NotBlank @Size(min = 2, max = 50) String serviceCode,
    @NotBlank @Size(min = 2, max = 100) String nombre,
    @NotBlank @Pattern(regexp = "^https?://.+") String baseUrl,
    @NotBlank String validationMode,
    String publicKey
) {}
