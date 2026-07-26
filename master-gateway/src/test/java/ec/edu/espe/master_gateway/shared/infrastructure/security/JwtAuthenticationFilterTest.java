package ec.edu.espe.master_gateway.shared.infrastructure.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenClaims;
import ec.edu.espe.master_gateway.contexts.auth.domain.port.out.TokenValidationPort;
import ec.edu.espe.master_gateway.contexts.identity.domain.model.User;
import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRepositoryPort;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private TokenValidationPort tokenValidationPort;
    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    private ObjectMapper objectMapper;
    private BearerTokenExtractor tokenExtractor;
    private JwtAuthenticationFilter filter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        tokenExtractor = new BearerTokenExtractor();
        filter = new JwtAuthenticationFilter(tokenValidationPort, objectMapper, tokenExtractor, userRepositoryPort);
    }

    @Test
    void should_continueChain_when_noAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void should_continueChain_when_headerHasNoBearerPrefix() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Invalid");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void should_returnUnauthorized_when_tokenIsInvalid() throws Exception {
        var token = "invalid-token";
        var outputStream = mock(ServletOutputStream.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenValidationPort.validate(token)).thenThrow(new RuntimeException("Token inválido"));
        when(response.getOutputStream()).thenReturn(outputStream);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void should_returnUnauthorized_when_userNotFound() throws Exception {
        var userId = UUID.randomUUID();
        var token = "valid-token";
        var outputStream = mock(ServletOutputStream.class);
        var claims = new TokenClaims(userId, UUID.randomUUID(), "ADMIN", "ACCESS_TOKEN",
                Instant.now(), Instant.now().plusSeconds(3600), "issuer", Set.of());
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenValidationPort.validate(token)).thenReturn(claims);
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.empty());
        when(response.getOutputStream()).thenReturn(outputStream);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void should_returnUnauthorized_when_userIsInactive() throws Exception {
        var userId = UUID.randomUUID();
        var token = "valid-token";
        var outputStream = mock(ServletOutputStream.class);
        var claims = new TokenClaims(userId, UUID.randomUUID(), "ADMIN", "ACCESS_TOKEN",
                Instant.now(), Instant.now().plusSeconds(3600), "issuer", Set.of());
        var user = mock(User.class);
        when(user.isActive()).thenReturn(false);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenValidationPort.validate(token)).thenReturn(claims);
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));
        when(response.getOutputStream()).thenReturn(outputStream);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void should_authenticateAndContinue_when_tokenIsValid() throws Exception {
        var userId = UUID.randomUUID();
        var roleId = UUID.randomUUID();
        var token = "valid-token";
        var claims = new TokenClaims(userId, roleId, "ADMIN", "ACCESS_TOKEN",
                Instant.now(), Instant.now().plusSeconds(3600), "issuer", Set.of("READ"));
        var user = mock(User.class);
        when(user.isActive()).thenReturn(true);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(tokenValidationPort.validate(token)).thenReturn(claims);
        when(userRepositoryPort.findById(userId)).thenReturn(Optional.of(user));

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
