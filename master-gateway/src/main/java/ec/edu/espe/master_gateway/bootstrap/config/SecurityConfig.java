package ec.edu.espe.master_gateway.bootstrap.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.fasterxml.jackson.databind.ObjectMapper;
import ec.edu.espe.master_gateway.shared.infrastructure.security.JwtAuthenticationFilter;
import ec.edu.espe.master_gateway.shared.infrastructure.security.ZeroTrustAccessDeniedHandler;
import ec.edu.espe.master_gateway.shared.infrastructure.web.ErrorResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuración principal de seguridad del microservicio.
 *
 * <p>Define las reglas de autenticación y autorización utilizadas por Spring
 * Security para proteger los recursos expuestos por la aplicación. Configura
 * el uso de autenticación basada en JWT, deshabilita el manejo de sesiones
 * tradicionales y establece el comportamiento frente a solicitudes no
 * autenticadas o usuarios sin permisos suficientes.</p>
 *
 * <p>La configuración sigue un modelo de seguridad sin estado (stateless),
 * donde cada solicitud debe incluir un token JWT válido para acceder a los
 * recursos protegidos.</p>
 *
 * <p>Además, establece excepciones para endpoints públicos como autenticación,
 * renovación de tokens, monitoreo de salud y documentación OpenAPI.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Filtro encargado de validar los tokens JWT incluidos en las solicitudes.
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Manejador personalizado para solicitudes rechazadas por falta de permisos.
     */
    private final ZeroTrustAccessDeniedHandler accessDeniedHandler;
    private final ObjectMapper objectMapper;

    /**
     * Inicializa la configuración de seguridad con los componentes necesarios
     * para autenticación y manejo de acceso denegado.
     *
     * @param jwtAuthenticationFilter filtro encargado de procesar y validar
     *                                autenticaciones mediante JWT.
     * @param accessDeniedHandler manejador personalizado para errores de
     *                            autorización.
     * @param objectMapper mapper JSON configurado por JacksonConfig.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          ZeroTrustAccessDeniedHandler accessDeniedHandler,
                          ObjectMapper objectMapper) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.accessDeniedHandler = accessDeniedHandler;
        this.objectMapper = objectMapper;
    }

    /**
     * Configura la cadena de filtros de seguridad utilizada por Spring Security.
     *
     * <p>Define las políticas de protección de endpoints, el manejo de
     * sesiones, la integración del filtro JWT y el tratamiento personalizado
     * de errores de autenticación y autorización.</p>
     *
     * @param http objeto utilizado para configurar la seguridad HTTP.
     * @return cadena de filtros de seguridad configurada.
     * @throws Exception si ocurre un error durante la configuración de
     *                   seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/select-role").permitAll()
                        .requestMatchers("/api/auth/logout").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/refresh-token").permitAll()
                        .requestMatchers("/api/internals/validate-token").permitAll()
                        .requestMatchers("/api/internals/gateway-public-key").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/api-docs/**", "/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            response.setContentType("application/json");
                            response.getOutputStream().write(
                                    objectMapper.writeValueAsBytes(
                                            ErrorResponse.of(HttpStatus.UNAUTHORIZED,
                                                    "Autenticación requerida")
                                    )
                            );
                            response.getOutputStream().flush();
                        })
                );

        return http.build();
    }
}