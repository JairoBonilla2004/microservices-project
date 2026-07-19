package ec.edu.espe.master_gateway.contexts.auth.domain.port.out;

/**
 * Puerto de salida del dominio para la validación de tokens de autenticación.
 *
 * <p>Define las operaciones necesarias para verificar la validez de los
 * diferentes tipos de tokens utilizados por el sistema y obtener la
 * información contenida en sus reclamaciones (Claims). Asimismo, contempla
 * la invalidación de tokens temporales cuando estos dejan de ser válidos o
 * han sido utilizados durante el proceso de autenticación.</p>
 *
 * <p>La implementación de este puerto es responsabilidad de la capa de
 * infraestructura, permitiendo que el dominio permanezca independiente de
 * la tecnología utilizada para validar y administrar los JWT.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface TokenValidationPort {

    TokenClaims validate(String token);

    TokenClaims validateTempToken(String token);

    void invalidateTempToken(String token);

    void revokeAccessToken(String token);
}