package ec.edu.espe.master_gateway.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * Repositorio base con soporte de borrado lógico.
 *
 * <p>Extiende {@link JpaRepository} y sobrescribe los métodos de
 * eliminación para realizar un borrado lógico, cambiando el estado
 * de la entidad a {@link EstadoRegistro#INACTIVO} en lugar de
 * eliminarla físicamente de la base de datos.</p>
 *
 * @param <T> tipo de la entidad auditable.
 * @param <I> tipo del identificador de la entidad.
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@NoRepositoryBean // Indica a Spring Data que esta interfaz no debe ser instanciada directamente como un bean de repositorio, es decir, no se creará un bean de Spring para esta interfaz, sino que se utilizará como base para otros repositorios.
public interface SoftDeleteRepository<T extends JpaAuditableEntity, I> extends JpaRepository<T, I> {

    @Override
    default void deleteById(I id) {
        findById(id).ifPresent(entity -> {
            entity.setEstado(EstadoRegistro.INACTIVO);
            save(entity);
        });
    }

    @Override
    default void delete(T entity) {
        entity.setEstado(EstadoRegistro.INACTIVO);
        save(entity);
    }

    @Override
    default void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(entity -> {
            entity.setEstado(EstadoRegistro.INACTIVO);
            save(entity);
        });
    }

    @Override
    default void deleteAllById(Iterable<? extends I> ids) {
        ids.forEach(this::deleteById);
    }
}
