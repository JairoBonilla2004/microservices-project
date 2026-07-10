package ec.edu.espe.master_gateway.shared.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.master_gateway.shared.infrastructure.web.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * Manejador personalizado para respuestas de acceso denegado.
 *
 * <p>Implementa el principio de confianza cero retornando una respuesta
 * JSON estructurada con código HTTP 403 cuando un usuario autenticado
 * intenta acceder a un recurso para el que no tiene permisos.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class ZeroTrustAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.getWriter().write(
                new ObjectMapper().writeValueAsString(
                        ErrorResponse.of(HttpStatus.FORBIDDEN,
                                "Acceso denegado: no tienes permisos para este recurso")
                )
        );
    }
}
