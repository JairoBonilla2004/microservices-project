package ec.edu.espe.master_gateway.contexts.menu.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import ec.edu.espe.master_gateway.shared.infrastructure.persistence.SoftDeleteRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repositorio JPA para la entidad {@link MenuNodeJpaEntity}.
 *
 * <p>Extiende {@link SoftDeleteRepository} para heredar el borrado
 * lógico y proporciona consultas nativas con CTE recursivas para
 * la navegación jerárquica del árbol de menús.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public interface MenuNodeJpaRepository extends SoftDeleteRepository<MenuNodeJpaEntity, UUID> {

    List<MenuNodeJpaEntity> findByParentId(UUID parentId);

    List<MenuNodeJpaEntity> findByParentIdAndEstado(UUID parentId, EstadoRegistro estado);

    List<MenuNodeJpaEntity> findByEstadoOrderByOrden(EstadoRegistro estado);

    List<MenuNodeJpaEntity> findTop50ByEstadoOrderByFechaActualizacionDesc(EstadoRegistro estado);

    @Query(value = """
        WITH RECURSIVE menu_tree AS (
            SELECT id, nombre, url, modulo_id, parent_id, orden, estado,
                   creado_por, fecha_creacion, actualizado_por, fecha_actualizacion
            FROM menus
            WHERE parent_id IS NULL
              AND modulo_id IN (:moduleIds)
              AND estado = 'ACTIVO'
            UNION ALL
            SELECT m.id, m.nombre, m.url, m.modulo_id, m.parent_id, m.orden, m.estado,
                   m.creado_por, m.fecha_creacion, m.actualizado_por, m.fecha_actualizacion
            FROM menus m
            INNER JOIN menu_tree mt ON m.parent_id = mt.id
            WHERE m.estado = 'ACTIVO'
        )
        SELECT * FROM menu_tree
        ORDER BY orden
        """, nativeQuery = true)
    List<MenuNodeJpaEntity> findTreeByModuleIds(@Param("moduleIds") List<UUID> moduleIds);

    /**
     * Recupera, en una sola consulta CTE, los nodos indicados junto con todos
     * sus descendientes activos. Usado cuando el rol tiene ítems de menú
     * asignados directamente (en vez de por módulo completo), evitando N+1
     * al construir el subárbol de cada nodo raíz.
     */
    @Query(value = """
        WITH RECURSIVE menu_subtree AS (
            SELECT id, nombre, url, modulo_id, parent_id, orden, estado,
                   creado_por, fecha_creacion, actualizado_por, fecha_actualizacion
            FROM menus
            WHERE id IN (:nodeIds)
              AND estado = 'ACTIVO'
            UNION
            SELECT m.id, m.nombre, m.url, m.modulo_id, m.parent_id, m.orden, m.estado,
                   m.creado_por, m.fecha_creacion, m.actualizado_por, m.fecha_actualizacion
            FROM menus m
            INNER JOIN menu_subtree mt ON m.parent_id = mt.id
            WHERE m.estado = 'ACTIVO'
        )
        SELECT * FROM menu_subtree
        ORDER BY orden
        """, nativeQuery = true)
    List<MenuNodeJpaEntity> findSubtreesByNodeIds(@Param("nodeIds") List<UUID> nodeIds);

    @Query(value = """
        WITH RECURSIVE ancestors AS (
            SELECT id, nombre, url, modulo_id, parent_id, orden, estado,
                   creado_por, fecha_creacion, actualizado_por, fecha_actualizacion
            FROM menus
            WHERE id = :nodeId
            UNION
            SELECT m.id, m.nombre, m.url, m.modulo_id, m.parent_id, m.orden, m.estado,
                   m.creado_por, m.fecha_creacion, m.actualizado_por, m.fecha_actualizacion
            FROM menus m
            INNER JOIN ancestors a ON m.id = a.parent_id
            WHERE m.estado = 'ACTIVO'
        )
        SELECT * FROM ancestors
        """, nativeQuery = true)
    List<MenuNodeJpaEntity> findAncestors(@Param("nodeId") UUID nodeId);
}
