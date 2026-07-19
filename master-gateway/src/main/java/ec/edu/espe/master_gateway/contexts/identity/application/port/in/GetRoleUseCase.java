package ec.edu.espe.master_gateway.contexts.identity.application.port.in;

import ec.edu.espe.master_gateway.contexts.identity.application.port.in.dto.RoleResponse;
import java.util.UUID;

public interface GetRoleUseCase {
    RoleResponse execute(UUID id);
}
