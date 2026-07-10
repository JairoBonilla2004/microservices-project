package ec.edu.espe.master_gateway.shared.infrastructure.ratelimiting;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ec.edu.espe.master_gateway.bootstrap.config.LoginRateLimitProperties;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

/**
 * Servicio de limitación de tasa para intentos de inicio de sesión.
 *
 * <p>Implementa un contador de intentos por dirección IP y nombre de
 * usuario utilizando una caché temporal con expiración. Cuando se supera
 * el número máximo de intentos permitidos en la ventana de tiempo
 * configurada, el inicio de sesión es bloqueado temporalmente.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@Service
public class LoginRateLimitingService {

    private final Cache<String, AtomicInteger> attemptCache;
    private final int maxAttempts;
    private final Duration windowDuration;

    public LoginRateLimitingService(LoginRateLimitProperties properties) {
        this.maxAttempts = properties.getMaxAttempts();
        this.windowDuration = properties.getWindowDuration();
        this.attemptCache = Caffeine.newBuilder()
                .expireAfterWrite(windowDuration)
                .maximumSize(100_000)
                .build();
    }

    public boolean isLoginAllowed(String ip, String username) {
        String key = buildKey(ip, username);
        AtomicInteger counter = attemptCache.get(key, k -> new AtomicInteger(0));
        return counter.incrementAndGet() <= maxAttempts;
    }

    public int getRemainingAttempts(String ip, String username) {
        String key = buildKey(ip, username);
        AtomicInteger counter = attemptCache.getIfPresent(key);
        if (counter == null) {
            return maxAttempts;
        }
        return Math.max(0, maxAttempts - counter.get());
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public Duration getWindowDuration() {
        return windowDuration;
    }

    private String buildKey(String ip, String username) {
        return ip + ":" + (username != null ? username.toLowerCase() : "");
    }
}
