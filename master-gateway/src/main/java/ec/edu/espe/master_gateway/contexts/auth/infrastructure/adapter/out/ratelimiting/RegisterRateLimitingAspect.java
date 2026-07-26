package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.ratelimiting;

/**
 * Aspecto de limitación de tasa (rate limiting) para el registro de usuarios.
 *
 * <p>Intercepta las invocaciones al servicio de registro de usuarios y limita
 * el número de solicitudes permitidas por dirección IP dentro de una ventana
 * de tiempo configurable. Cuando se supera el límite, lanza una excepción
 * {@link ec.edu.espe.master_gateway.shared.domain.RateLimitExceededException}.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ec.edu.espe.master_gateway.bootstrap.config.RegisterRateLimitProperties;

import ec.edu.espe.master_gateway.shared.domain.RateLimitExceededException;
import java.util.concurrent.atomic.AtomicInteger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class RegisterRateLimitingAspect {

    private final Cache<String, AtomicInteger> attemptCache;
    private final int maxAttempts;
    private final long windowSeconds;

    public RegisterRateLimitingAspect(RegisterRateLimitProperties properties) {
        this.maxAttempts = properties.getMaxAttempts();
        this.windowSeconds = properties.getWindowDuration().toSeconds();
        this.attemptCache = Caffeine.newBuilder()
                .expireAfterWrite(properties.getWindowDuration())
                .maximumSize(100_000)
                .build();
    }

    @Around("execution(* ec.edu.espe.master_gateway.contexts.auth.application.service.RegisterUserService.execute(..))")
    public Object applyRateLimiting(ProceedingJoinPoint joinPoint) throws Throwable {
        String ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr();
        String key = "register:" + (ip != null ? ip : "unknown");
        AtomicInteger counter = attemptCache.get(key, k -> new AtomicInteger(0));
        if (counter.incrementAndGet() > maxAttempts) {
            throw new RateLimitExceededException(
                    "Demasiadas solicitudes de registro. Intente de nuevo en "
                            + windowSeconds + " segundos.",
                    (int) windowSeconds, maxAttempts,
                    Math.max(0, maxAttempts - counter.get()));
        }
        return joinPoint.proceed();
    }
}
