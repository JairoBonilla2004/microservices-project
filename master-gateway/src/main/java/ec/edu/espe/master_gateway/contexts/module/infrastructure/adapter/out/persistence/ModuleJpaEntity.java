package ec.edu.espe.master_gateway.contexts.module.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.JpaAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entidad JPA que representa un módulo funcional del sistema.
 *
 * <p>Mapea la tabla {@code modules} y contiene la información
 * básica del módulo: nombre único, descripción opcional, icono
 * y orden de visualizaci&oacute;n. Hereda los campos de
 * auditor&iacute;a de {@link JpaAuditableEntity}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Entity
@Table(name = "modules")
public class ModuleJpaEntity extends JpaAuditableEntity {

    @Column(unique = true, length = 100, nullable = false)
    private String nombre;

    @Column(length = 255)
    private String descripcion;

    @Column(length = 50, nullable = false)
    private String icono;

    @Column(name = "orden", nullable = false)
    private int orden;

    public ModuleJpaEntity() {
        // Required by JPA
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getIcono() {
        return icono;
    }

    public void setIcono(String icono) {
        this.icono = icono;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
