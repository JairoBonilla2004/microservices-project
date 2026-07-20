package ec.edu.espe.master_gateway.shared.domain;

import java.util.List;

/**
 * Resultado paginado genérico para el dominio.
 *
 * <p>Evita filtrar tipos de paginación de Spring Data (como {@code Page})
 * a la capa de dominio, manteniendo el desacoplo de la Arquitectura
 * Hexagonal entre el dominio y la tecnología de persistencia.</p>
 *
 * @param content        elementos de la página actual.
 * @param totalElements  número total de elementos que cumplen el filtro.
 * @param page           número de página actual (0-indexado).
 * @param size           tamaño de página solicitado.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public record PageResult<T>(List<T> content, long totalElements, int page, int size) {

    public int totalPages() {
        if (size <= 0) {
            return 0;
        }
        return (int) Math.ceil((double) totalElements / size);
    }
}
