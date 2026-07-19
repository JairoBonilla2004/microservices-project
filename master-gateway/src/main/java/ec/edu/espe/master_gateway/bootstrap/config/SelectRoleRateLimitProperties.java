package ec.edu.espe.master_gateway.bootstrap.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.boot.convert.DurationUnit;

@ConfigurationProperties(prefix = "rate-limiting.select-role")
public class SelectRoleRateLimitProperties {

    private final int maxAttempts;
    private final Duration windowDuration;

    public SelectRoleRateLimitProperties(
            @DefaultValue("10") int maxAttempts,
            @DurationUnit(ChronoUnit.MINUTES) @DefaultValue("1") Duration windowDuration) {
        this.maxAttempts = maxAttempts;
        this.windowDuration = windowDuration;
    }

    public int getMaxAttempts() { return maxAttempts; }
    public Duration getWindowDuration() { return windowDuration; }
}
