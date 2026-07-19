package ec.edu.espe.master_gateway.contexts.identity.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.JpaAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entidad JPA que representa un rol del sistema.
 *
 * <p>Mapea la tabla {@code roles} y soporta borrado lógico
 * mediante el campo {@code estado}. Hereda los campos de auditoría
 * de {@link JpaAuditableEntity}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Entity
@Table(name = "roles")
public class RoleJpaEntity extends JpaAuditableEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    public RoleJpaEntity() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
