package ec.edu.espe.master_gateway.shared.infrastructure.persistence;

/**
 * Estados de registro utilizados para el borrado lógico de entidades.
 *
 * <p>Define los valores posibles {@code ACTIVO} e {@code INACTIVO} que
 * permiten implementar eliminaciones suaves sin perder la información,
 * facilitando la recuperación y auditoría de datos.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public enum EstadoRegistro {
    ACTIVO,
    INACTIVO
}
