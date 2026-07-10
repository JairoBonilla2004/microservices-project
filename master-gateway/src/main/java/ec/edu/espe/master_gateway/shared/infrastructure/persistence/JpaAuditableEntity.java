package ec.edu.espe.master_gateway.shared.infrastructure.persistence;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Entidad base con campos de auditoría para todas las entidades JPA.
 *
 * <p>Proporciona un identificador autogenerado, control de estado
 * (activo/inactivo), marcas de tiempo de creación y actualización,
 * así como los usuarios responsables de cada operación. Los campos
 * de auditoría son poblados automáticamente por Spring Data JPA
 * mediante {@link AuditingEntityListener}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class JpaAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private EstadoRegistro estado = EstadoRegistro.ACTIVO;

    @CreatedDate
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    private LocalDateTime fechaActualizacion;

    @CreatedBy
    private String creadoPor;

    @LastModifiedBy
    private String actualizadoPor;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EstadoRegistro getEstado() {
        return estado;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public String getActualizadoPor() {
        return actualizadoPor;
    }
}
