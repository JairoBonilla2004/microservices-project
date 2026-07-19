package ec.edu.espe.master_gateway.contexts.service_registry.infrastructure.adapter.in.web;

/**
 * Controlador REST para el registro y gestión de microservicios.
 *
 * <p>Proporciona endpoints para que los microservicios hijos se registren
 * en el gateway, permitiendo la validación asimétrica de tokens mediante
 * el modo de validación configurado. Incluye operaciones CRUD sobre los
 * servicios registrados y sus configuraciones de seguridad asociadas.</p>
 *
 * @author Jairo Bonilla
 * @author Reishel Tipan
 * @author Julio Viche
 */
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.DeactivateServiceUseCase;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.ListRegisteredServicesUseCase;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.RegisterServiceUseCase;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.UpdateServiceUseCase;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.RegisterServiceRequest;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.RegisterServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.ServiceResponse;
import ec.edu.espe.master_gateway.contexts.service_registry.application.port.in.dto.UpdateServiceRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/service-registry")
public class ServiceRegistryController {

    private final RegisterServiceUseCase registerServiceUseCase;
    private final ListRegisteredServicesUseCase listRegisteredServicesUseCase;
    private final UpdateServiceUseCase updateServiceUseCase;
    private final DeactivateServiceUseCase deactivateServiceUseCase;

    public ServiceRegistryController(RegisterServiceUseCase registerServiceUseCase,
                                     ListRegisteredServicesUseCase listRegisteredServicesUseCase,
                                     UpdateServiceUseCase updateServiceUseCase,
                                     DeactivateServiceUseCase deactivateServiceUseCase) {
        this.registerServiceUseCase = registerServiceUseCase;
        this.listRegisteredServicesUseCase = listRegisteredServicesUseCase;
        this.updateServiceUseCase = updateServiceUseCase;
        this.deactivateServiceUseCase = deactivateServiceUseCase;
    }

    @PostMapping
    public ResponseEntity<RegisterServiceResponse> registerService(@RequestBody @Valid RegisterServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(registerServiceUseCase.execute(request));
    }

    @GetMapping
    public ResponseEntity<List<ServiceResponse>> listServices() {
        return ResponseEntity.ok(listRegisteredServicesUseCase.execute());
    }

    @PutMapping("/{code}")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable String code,
                                                         @RequestBody @Valid UpdateServiceRequest request) {
        return ResponseEntity.ok(updateServiceUseCase.execute(code, request));
    }

    @DeleteMapping("/{code}")
    public ResponseEntity<Void> deactivateService(@PathVariable String code) {
        deactivateServiceUseCase.execute(code);
        return ResponseEntity.noContent().build();
    }
}
