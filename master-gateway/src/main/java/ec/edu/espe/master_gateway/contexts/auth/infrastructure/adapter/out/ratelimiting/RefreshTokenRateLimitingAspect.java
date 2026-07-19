package ec.edu.espe.master_gateway.contexts.auth.infrastructure.adapter.out.ratelimiting;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ec.edu.espe.master_gateway.bootstrap.config.RefreshTokenRateLimitProperties;
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
public class RefreshTokenRateLimitingAspect {

    private final Cache<String, AtomicInteger> attemptCache;
    private final int maxAttempts;
    private final long windowSeconds;

    public RefreshTokenRateLimitingAspect(RefreshTokenRateLimitProperties properties) {
        this.maxAttempts = properties.getMaxAttempts();
        this.windowSeconds = properties.getWindowDuration().toSeconds();
        this.attemptCache = Caffeine.newBuilder()
                .expireAfterWrite(properties.getWindowDuration())
                .maximumSize(100_000)
                .build();
    }

    @Around("execution(* ec.edu.espe.master_gateway.contexts.auth.application.service.RefreshTokenService.execute(..))")
    public Object applyRateLimiting(ProceedingJoinPoint joinPoint) throws Throwable {
        String ip = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest().getRemoteAddr();
        String key = "refresh-token:" + (ip != null ? ip : "unknown");
        AtomicInteger counter = attemptCache.get(key, k -> new AtomicInteger(0));
        if (counter.incrementAndGet() > maxAttempts) {
            throw new RateLimitExceededException(
                    "Demasiadas solicitudes de renovacion de token. Intente de nuevo en "
                            + windowSeconds + " segundos.",
                    (int) windowSeconds, maxAttempts,
                    Math.max(0, maxAttempts - counter.get()));
        }
        return joinPoint.proceed();
    }
}
