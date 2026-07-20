package ec.edu.espe.master_gateway.contexts.auth.domain.port.out;

import java.util.Set;
import java.util.UUID;

/**
 * Puerto de salida del dominio para la emisión de tokens de autenticación.
 *
 * <p>Define las operaciones necesarias para generar los diferentes tipos de
 * tokens utilizados por el sistema de autenticación, incluyendo tokens
 * temporales, tokens de acceso y tokens de actualización. Su implementación
 * corresponde a la capa de infraestructura, donde se encapsula la tecnología
 * utilizada para la creación de JWT y sus mecanismos de firma.</p>
 *
 * <p>Al definir esta abstracción, el dominio permanece desacoplado de
 * bibliotecas o mecanismos específicos de generación de tokens, siguiendo
 * los principios de la Arquitectura Hexagonal.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface TokenIssuerPort {

    String issueTempToken(UUID userId);

    String issueAccessToken(UUID userId, UUID roleId, Set<String> permissions, String roleName, String username);

    String issueRefreshToken(UUID userId, UUID roleId, String roleName);
}