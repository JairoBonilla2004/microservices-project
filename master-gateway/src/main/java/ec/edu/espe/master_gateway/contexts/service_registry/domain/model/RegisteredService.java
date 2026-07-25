package ec.edu.espe.master_gateway.contexts.service_registry.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Servicio registrado en el sistema de registro de microservicios.
 *
 * <p>Representa un microservicio registrado que puede ser autenticado
 * y autorizado por el gateway. Cada servicio tiene un código único,
 * una URL base, un modo de validación (directa o asimétrica) y una
 * clave pública opcional para verificación de tokens.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
public class RegisteredService {

    public enum ValidationMode {
        NONE,
        DELEGATE,
        LOCAL
    }

    private UUID id;
    private final String serviceCode;
    private String nombre;
    private String baseUrl;
    private final ValidationMode validationMode;
    private EstadoRegistro estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;

    public RegisteredService(String serviceCode, String nombre, String baseUrl,
                             ValidationMode validationMode) {
        this.serviceCode = Objects.requireNonNull(serviceCode, "serviceCode no puede ser null");
        this.nombre = Objects.requireNonNull(nombre, "nombre no puede ser null");
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl no puede ser null");
        this.validationMode = Objects.requireNonNull(validationMode, "validationMode no puede ser null");
        this.estado = EstadoRegistro.ACTIVO;
    }

    public void deactivate() {
        if (this.estado == EstadoRegistro.INACTIVO) {
            throw new IllegalStateException("El servicio ya está inactivo");
        }
        this.estado = EstadoRegistro.INACTIVO;
    }

    public void updateBaseUrl(String newBaseUrl) {
        this.baseUrl = Objects.requireNonNull(newBaseUrl, "newBaseUrl no puede ser null");
    }

    public void updateNombre(String newNombre) {
        this.nombre = Objects.requireNonNull(newNombre, "nombre no puede ser null");
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public void setActualizadoPor(String actualizadoPor) {
        this.actualizadoPor = actualizadoPor;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public String getNombre() {
        return nombre;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public ValidationMode getValidationMode() {
        return validationMode;
    }

    public EstadoRegistro getEstado() {
        return estado;
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
