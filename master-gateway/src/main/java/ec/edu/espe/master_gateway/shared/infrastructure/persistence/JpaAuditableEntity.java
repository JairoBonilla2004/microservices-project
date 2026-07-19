package ec.edu.espe.master_gateway.shared.infrastructure.persistence;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
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
@MappedSuperclass // Indica que esta clase es una superclase mapeada y que sus campos se heredarán en las entidades que la extiendan, pero no se creará una tabla para esta clase en la base de datos.
@EntityListeners(AuditingEntityListener.class) // Indica que esta clase es una superclase mapeada y que se deben escuchar los eventos de auditoría, es decir, que se llenen automáticamente los campos de auditoría como fecha de creación, fecha de actualización, creado por y actualizado por.
public abstract class JpaAuditableEntity { // es abstracta para que no se pueda instanciar directamente y solo se use como base para otras entidades

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
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
