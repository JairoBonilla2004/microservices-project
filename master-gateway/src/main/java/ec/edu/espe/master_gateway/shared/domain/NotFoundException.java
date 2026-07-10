package ec.edu.espe.master_gateway.shared.domain;

/**
 * Excepción lanzada cuando no se encuentra una entidad en el sistema.
 *
 * <p>Indica que el recurso solicitado no existe en la base de datos,
 * utilizando un código de error {@code NOT_FOUND} para su identificación.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class NotFoundException extends DomainException {

    public NotFoundException(String entidad, Object id) {
        super(entidad + " no encontrado con id: " + id, "NOT_FOUND");
        addDetalle("entidad", entidad);
        addDetalle("id", id);
    }
}
