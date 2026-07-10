package ec.edu.espe.master_gateway.shared.domain;

/**
 * Excepción lanzada cuando ocurre un error en la capa de persistencia.
 *
 * <p>Encapsula errores relacionados con operaciones de base de datos,
 * incluyendo fallos de conexión, restricciones de integridad u otros
 * problemas de infraestructura, usando el código {@code PERSISTENCE_ERROR}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class PersistenceException extends DomainException {

    public PersistenceException(String mensaje, Throwable causa) {
        super(mensaje + ": " + causa.getMessage(), "PERSISTENCE_ERROR");
    }
}
