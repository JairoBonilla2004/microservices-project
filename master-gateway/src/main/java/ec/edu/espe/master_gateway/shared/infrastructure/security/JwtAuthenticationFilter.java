package ec.edu.espe.master_gateway.shared.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.shared.infrastructure.web.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filtro de autenticación JWT que procesa cada solicitud HTTP.
 *
 * <p>Extrae el token JWT del encabezado {@code Authorization}, lo valida
 * mediante {@link TokenValidationPort} y establece el contexto de seguridad
 * de Spring Security con los datos del usuario autenticado. Si el token es
 * inválido o ha expirado, responde con un error 401 sin interrumpir la
 * cadena de filtros.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final TokenValidationPort tokenValidationPort;

    public JwtAuthenticationFilter(TokenValidationPort tokenValidationPort) {
        this.tokenValidationPort = tokenValidationPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(BEARER_PREFIX.length());
            TokenClaims claims = tokenValidationPort.validate(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            claims.getUserId().toString(), null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + claims.getRoleId()))
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    new ObjectMapper().writeValueAsString(
                            ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Token inválido o expirado")
                    )
            );
            return;
        }

        filterChain.doFilter(request, response);
    }
}
