package ec.edu.espe.master_gateway.shared.domain;

/**
 * Excepción lanzada cuando se intenta crear una entidad duplicada.
 *
 * <p>Se produce cuando ya existe un registro con el mismo valor único
 * para un campo específico, utilizando el código de error
 * {@code DUPLICATE_ENTRY}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class DuplicateException extends DomainException {

    public DuplicateException(String entidad, String campo, String valor) {
        super(entidad + " ya existe con " + campo + ": " + valor, "DUPLICATE_ENTRY");
        addDetalle("entidad", entidad);
        addDetalle("campo", campo);
        addDetalle("valor", valor);
    }
}
