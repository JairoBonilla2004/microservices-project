package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.ratelimiting;

/**
 * Aspecto de limitación de tasa (rate limiting) para el inicio de sesión.
 *
 * <p>Implementa el patrón <b>Decorator</b> (GoF) mediante un {@code @Around}
 * advice de AspectJ que envuelve la ejecución de
 * {@link ec.edu.espe.master_gateway.contexts.auth.application.service.LoginService#execute}
 * para controlar el número de intentos de inicio de sesión por dirección IP
 * y nombre de usuario.</p>
 *
 * <p>Si se excede el límite configurable (por defecto 5 intentos por minuto
 * por combinación IP+usuario), lanza una excepción
 * {@link ec.edu.espe.master_gateway.shared.domain.RateLimitExceededException}
 * que el {@link ec.edu.espe.master_gateway.shared.infrastructure.web.GlobalExceptionHandler}
 * traduce a una respuesta HTTP 429 (Too Many Requests) con el header
 * {@code Retry-After}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.auth.application.port.in.dto.LoginRequest;
import ec.edu.espe.master_gateway.shared.domain.RateLimitExceededException;
import ec.edu.espe.master_gateway.shared.infrastructure.ratelimiting.LoginRateLimitingService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class LoginRateLimitingAspect {

    private final LoginRateLimitingService rateLimitingService;

    public LoginRateLimitingAspect(LoginRateLimitingService rateLimitingService) {
        this.rateLimitingService = rateLimitingService;
    }

    @Around("execution(* ec.edu.espe.master_gateway.contexts.auth.application.service.LoginService.execute(..))")
    public Object applyRateLimiting(ProceedingJoinPoint joinPoint) throws Throwable {
        var request = (LoginRequest) joinPoint.getArgs()[0];
        String ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr();
        if (!rateLimitingService.isLoginAllowed(ip, request.username())) {
            int maxAttempts = rateLimitingService.getMaxAttempts();
            int retryAfter = (int) rateLimitingService.getWindowDuration().toSeconds();
            int remainingAttempts = rateLimitingService.getRemainingAttempts(ip, request.username());
            throw new RateLimitExceededException(
                    "Demasiados intentos de inicio de sesion. Intente de nuevo en "
                            + retryAfter + " segundos.",
                    retryAfter, maxAttempts, remainingAttempts);
        }
        return joinPoint.proceed();
    }
}
