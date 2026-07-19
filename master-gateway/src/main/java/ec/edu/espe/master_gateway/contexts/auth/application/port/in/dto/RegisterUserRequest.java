package ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
    @NotBlank @Size(min = 3, max = 50) String username,
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8) @Pattern(regexp = "^(?=.*[A-Z])(?=.*\\d).+$", message = "La contraseña debe contener al menos una mayúscula y un dígito") String password,
    @NotBlank String confirmPassword,
    @NotBlank String nombreCompleto
) {}
