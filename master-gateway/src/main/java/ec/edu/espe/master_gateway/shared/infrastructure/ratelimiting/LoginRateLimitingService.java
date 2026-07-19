package ec.edu.espe.master_gateway.shared.infrastructure.ratelimiting;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import ec.edu.espe.master_gateway.bootstrap.config.LoginRateLimitProperties;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(LoginRateLimitingService.class);

    private final Cache<String, AtomicInteger> attemptCache;
    private final int maxAttempts;
    private final Duration windowDuration;
    private final String keyType;

    public LoginRateLimitingService(LoginRateLimitProperties properties) {
        this.maxAttempts = properties.getMaxAttempts();
        this.windowDuration = properties.getWindowDuration();
        this.keyType = properties.getKeyType();
        this.attemptCache = Caffeine.newBuilder()
                .expireAfterWrite(windowDuration)
                .maximumSize(100_000)
                .build();
    }

    public boolean isLoginAllowed(String ip, String username) {
        String key = buildKey(ip, username);
        AtomicInteger counter = attemptCache.get(key, k -> new AtomicInteger(0));
        int attempts = counter.incrementAndGet();
        if (attempts > maxAttempts) {
            log.warn("Rate limit hit for key={}, attempts={}/{}", key, attempts, maxAttempts);
        }
        return attempts <= maxAttempts;
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
        String safeIp = ip != null ? ip : "unknown";
        if ("ip_only".equals(keyType)) {
            return "login:" + safeIp;
        }
        String safeUser = username != null ? username : "anonymous";
        return "login:" + safeIp + ":" + safeUser;
    }
}
