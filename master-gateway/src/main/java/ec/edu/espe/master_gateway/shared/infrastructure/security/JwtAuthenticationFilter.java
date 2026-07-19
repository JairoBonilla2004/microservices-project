package ec.edu.espe.master_gateway.shared.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import ec.edu.espe.master_gateway.shared.infrastructure.web.ErrorResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION_HEADER = "Authorization";

    private final TokenValidationPort tokenValidationPort;
    private final ObjectMapper objectMapper;
    private final BearerTokenExtractor tokenExtractor;
    private final UserRepositoryPort userRepositoryPort;

    public JwtAuthenticationFilter(TokenValidationPort tokenValidationPort,
                                   ObjectMapper objectMapper,
                                   BearerTokenExtractor tokenExtractor,
                                   UserRepositoryPort userRepositoryPort) {
        this.tokenValidationPort = tokenValidationPort;
        this.objectMapper = objectMapper;
        this.tokenExtractor = tokenExtractor;
        this.userRepositoryPort = userRepositoryPort;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(AUTHORIZATION_HEADER);
        var tokenOpt = tokenExtractor.extract(header);

        if (tokenOpt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = tokenOpt.get();
        TokenClaims claims;
        try {
            claims = tokenValidationPort.validate(token);
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            response.resetBuffer();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getOutputStream().write(
                    objectMapper.writeValueAsBytes(
                            ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Token inválido o expirado")
                    )
            );
            response.getOutputStream().flush();
            return;
        }

        UUID userId = claims.getUserId();
        var userOpt = userRepositoryPort.findById(userId);
        if (userOpt.isEmpty() || !userOpt.get().isActive()) {
            SecurityContextHolder.clearContext();
            response.resetBuffer();
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.getOutputStream().write(
                    objectMapper.writeValueAsBytes(
                            ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Usuario inactivo o eliminado")
                    )
            );
            response.getOutputStream().flush();
            return;
        }

        String principal = userId.toString();

        var authorities = new java.util.ArrayList<org.springframework.security.core.GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.getRoleId()));
        for (String permission : claims.getPermissions()) {
            authorities.add(new SimpleGrantedAuthority(
                    SpringSecurityAuthorizationAdapter.PERMISSION_PREFIX + permission
            ));
        }

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        principal, null,
                        authorities
                );
        authentication.setDetails(claims.getPermissions());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
