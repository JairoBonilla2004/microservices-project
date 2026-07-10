package ec.edu.espe.master_gateway.shared.domain;

public class AuthenticationException extends DomainException {

    public AuthenticationException(String mensaje) {
        super(mensaje, "AUTH_FAILED");
    }
}
