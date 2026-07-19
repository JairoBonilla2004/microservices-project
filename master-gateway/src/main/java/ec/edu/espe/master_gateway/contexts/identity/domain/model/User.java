package ec.edu.espe.master_gateway.contexts.identity.domain.model;

import ec.edu.espe.master_gateway.shared.infrastructure.persistence.EstadoRegistro;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Representa un usuario dentro del dominio de identidad.
 *
 * <p>Un usuario constituye la identidad principal del sistema y almacena la
 * información necesaria para los procesos de autenticación y autorización,
 * incluyendo sus credenciales, datos personales, estado de la cuenta y la
 * información de auditoría generada durante su persistencia.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */

public class User {

    private UUID id;
    private final String username;
    private String email;
    private String passwordHash;
    private String nombreCompleto;
    private EstadoRegistro estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
    private String creadoPor;
    private String actualizadoPor;

    public User(String username, String email, String passwordHash, String nombreCompleto) {
        this.username = Objects.requireNonNull(username, "username no puede ser null");
        this.email = Objects.requireNonNull(email, "email no puede ser null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "passwordHash no puede ser null");
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "nombreCompleto no puede ser null");
        this.estado = EstadoRegistro.ACTIVO;
    }

    public void deactivate() {
        if (this.estado == EstadoRegistro.INACTIVO) {
            throw new IllegalStateException("El usuario ya está inactivo");
        }
        this.estado = EstadoRegistro.INACTIVO;
    }

    public void setEstado(EstadoRegistro estado) {
        this.estado = estado;
    }

    public boolean isActive() {
        return estado == EstadoRegistro.ACTIVO;
    }

    public void updatePassword(String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "newPasswordHash no puede ser null");
    }

    public void updateEmail(String newEmail) {
        this.email = Objects.requireNonNull(newEmail, "email no puede ser null");
    }

    public void updateNombreCompleto(String newNombreCompleto) {
        this.nombreCompleto = Objects.requireNonNull(newNombreCompleto, "nombreCompleto no puede ser null");
    }

    public void markAsPersisted(UUID id, LocalDateTime fechaCreacion, LocalDateTime fechaActualizacion,
                                String creadoPor, String actualizadoPor) {
        if (this.id != null) {
            throw new IllegalStateException("El usuario ya fue persistido");
        }
        this.id = Objects.requireNonNull(id);
        this.fechaCreacion = fechaCreacion;
        this.fechaActualizacion = fechaActualizacion;
        this.creadoPor = creadoPor;
        this.actualizadoPor = actualizadoPor;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
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

}
