package ec.edu.espe.master_gateway.contexts.identity.domain.port.out;

/**
 * Puerto de salida del dominio para la gestión de contraseñas.
 *
 * <p>Define las operaciones necesarias para generar y verificar hashes de
 * contraseñas utilizadas durante los procesos de autenticación. La
 * implementación de este puerto corresponde a la capa de infraestructura,
 * donde se emplea el algoritmo de cifrado o hashing definido por la
 * aplicación, manteniendo el dominio desacoplado de tecnologías específicas.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface PasswordHasherPort {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String hashedPassword);
}