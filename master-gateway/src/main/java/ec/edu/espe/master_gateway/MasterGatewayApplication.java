package ec.edu.espe.master_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Punto de entrada de la aplicación Master Gateway.
 *
 * <p>Inicializa el contexto de Spring Boot con escaneo automático de
 * propiedades de configuración, actuando como el gateway central para
 * la autenticación, autorización y enrutamiento de microservicios en
 * el sistema.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class MasterGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MasterGatewayApplication.class, args);
    }
}
