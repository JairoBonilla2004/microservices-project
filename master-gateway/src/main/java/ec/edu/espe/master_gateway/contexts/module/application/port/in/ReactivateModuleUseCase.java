package ec.edu.espe.master_gateway.contexts.module.application.port.in;

import java.util.UUID;

public interface ReactivateModuleUseCase {
    void execute(UUID id);
}
