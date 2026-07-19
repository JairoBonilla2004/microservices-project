package ec.edu.espe.master_gateway.bootstrap.config;

/**
 * Configuración de Jackson para la serialización JSON.
 *
 * <p>Registra el módulo {@code JavaTimeModule} para soportar tipos de fecha/hora
 * de Java 8 y deshabilita la serialización de fechas como marcas de tiempo numéricas,
 * garantizando un formato ISO-8601 legible en las respuestas JSON.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
