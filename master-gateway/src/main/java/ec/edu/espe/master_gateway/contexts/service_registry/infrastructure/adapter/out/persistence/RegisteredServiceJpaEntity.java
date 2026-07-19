package ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.out.persistence;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.JpaAuditableEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * Entidad JPA que representa un servicio registrado en la base de datos.
 *
 * <p>Mapea la tabla {@code registered_services} y soporta borrado lógico
 * mediante el campo {@code estado}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Entity
@Table(name = "registered_services")
public class RegisteredServiceJpaEntity extends JpaAuditableEntity {

    @Column(name = "service_code", nullable = false, unique = true, length = 50)
    private String serviceCode;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(name = "base_url", nullable = false, length = 500)
    private String baseUrl;

    @Column(name = "modo_validacion", nullable = false, length = 20)
    private String validationMode;

    @Column(name = "clave_publica", columnDefinition = "TEXT")
    private String publicKey;

    public RegisteredServiceJpaEntity() {}

    public String getServiceCode() { return serviceCode; }
    public void setServiceCode(String serviceCode) { this.serviceCode = serviceCode; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getValidationMode() { return validationMode; }
    public void setValidationMode(String validationMode) { this.validationMode = validationMode; }
    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }
}
