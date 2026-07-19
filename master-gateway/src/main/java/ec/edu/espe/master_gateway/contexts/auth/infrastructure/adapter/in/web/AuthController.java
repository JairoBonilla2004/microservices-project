package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.in.web;

/**
 * Controlador REST para la autenticación de usuarios.
 *
 * <p>Expone los endpoints públicos del flujo de autenticación incluyendo
 * inicio de sesión, selección de rol, renovación de tokens y cierre de
 * sesión. Todos los endpoints son accesibles sin autenticación previa
 * según la configuración de seguridad definida en {@code SecurityConfig}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.LoginUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.LogoutUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.RefreshTokenUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.RegisterUserUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.SelectRoleUseCase;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginResponse;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RefreshTokenRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RefreshTokenResponse;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RegisterUserRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.RegisterUserResponse;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.SelectRoleRequest;
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.SelectRoleResponse;
import ec.edu.espe.master_gateway.shared.infrastructure.security.BearerTokenExtractor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final LoginUseCase loginUseCase;
    private final SelectRoleUseCase selectRoleUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final LogoutUseCase logoutUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final BearerTokenExtractor tokenExtractor;

    public AuthController(LoginUseCase loginUseCase,
                          SelectRoleUseCase selectRoleUseCase,
                          RefreshTokenUseCase refreshTokenUseCase,
                          LogoutUseCase logoutUseCase,
                          RegisterUserUseCase registerUserUseCase,
                          BearerTokenExtractor tokenExtractor) {
        this.loginUseCase = loginUseCase;
        this.selectRoleUseCase = selectRoleUseCase;
        this.refreshTokenUseCase = refreshTokenUseCase;
        this.logoutUseCase = logoutUseCase;
        this.registerUserUseCase = registerUserUseCase;
        this.tokenExtractor = tokenExtractor;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(loginUseCase.execute(request));
    }

    @PostMapping("/select-role")
    public ResponseEntity<SelectRoleResponse> selectRole(@RequestBody @Valid SelectRoleRequest request) {
        return ResponseEntity.ok(selectRoleUseCase.execute(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenUseCase.execute(request));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody @Valid RegisterUserRequest request) {
        RegisterUserResponse response = registerUserUseCase.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = true) String authHeader,
                                       @RequestBody @Valid RefreshTokenRequest request) {
        String accessToken = tokenExtractor.extract(authHeader).orElse(null);
        logoutUseCase.execute(request.refreshToken(), accessToken);
        return ResponseEntity.noContent().build();
    }
}
