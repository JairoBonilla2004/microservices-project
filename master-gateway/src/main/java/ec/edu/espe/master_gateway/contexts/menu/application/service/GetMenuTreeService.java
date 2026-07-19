package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.GetMenuTreeUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuNodeResponse;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.RoleMenuAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.RoleModuleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class GetMenuTreeService implements GetMenuTreeUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetMenuTreeService.class);

    private final MenuRepositoryPort menuRepositoryPort;
    private final RoleMenuAssignmentRepositoryPort roleMenuAssignmentRepositoryPort;
    private final RoleModuleAssignmentRepositoryPort roleModuleAssignmentRepositoryPort;
    private final AuthorizationPort authorizationPort;
    private final UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort;

    public GetMenuTreeService(MenuRepositoryPort menuRepositoryPort,
                                RoleMenuAssignmentRepositoryPort roleMenuAssignmentRepositoryPort,
                                RoleModuleAssignmentRepositoryPort roleModuleAssignmentRepositoryPort,
                                AuthorizationPort authorizationPort,
                                UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort) {
        this.menuRepositoryPort = Objects.requireNonNull(menuRepositoryPort);
        this.roleMenuAssignmentRepositoryPort = Objects.requireNonNull(roleMenuAssignmentRepositoryPort);
        this.roleModuleAssignmentRepositoryPort = Objects.requireNonNull(roleModuleAssignmentRepositoryPort);
        this.authorizationPort = Objects.requireNonNull(authorizationPort);
        this.userRoleAssignmentRepositoryPort = Objects.requireNonNull(userRoleAssignmentRepositoryPort);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MenuNodeResponse> execute(UUID roleId) {
        var currentUserId = authorizationPort.getCurrentUserId();
        var hasRole = userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(currentUserId, roleId).isPresent();
        if (!hasRole) {
            authorizationPort.requirePermission(Permission.MENUS_READ);
        }
        var assignedNodeIds = roleMenuAssignmentRepositoryPort.findMenuNodeIdsByRoleId(roleId);

        if (!assignedNodeIds.isEmpty()) {
            List<MenuNodeResponse> result = new ArrayList<>();
            for (UUID nodeId : assignedNodeIds) {
                menuRepositoryPort.findById(nodeId)
                        .ifPresent(node -> result.add(buildNodeWithChildren(node)));
            }
            log.debug("Menu tree retrieved for roleId={} with {} assigned nodes", roleId, result.size());
            return result;
        }

        var moduleIds = roleModuleAssignmentRepositoryPort.findModuleIdsByRoleId(roleId);
        if (moduleIds.isEmpty()) {
            log.debug("No menu tree for roleId={}: no assigned nodes or modules", roleId);
            return Collections.emptyList();
        }

        var rootNodes = menuRepositoryPort.findRootNodesByModuleIds(moduleIds);
        var response = rootNodes.stream()
                .map(this::buildNodeWithChildren)
                .toList();

        log.debug("Menu tree retrieved for roleId={} from {} modules", roleId, moduleIds.size());
        return response;
    }

    /**
     * Construye recursivamente un {@link MenuNodeResponse} con todos sus hijos.
     *
     * <p>Para el nodo dado, obtiene sus hijos directos del repositorio y
     * los transforma invocando este mismo método, generando así la
     * estructura jerárquica completa.</p>
     *
     * @param node nodo de dominio a transformar.
     * @return respuesta del nodo con su subárbol de hijos.
     */
    private MenuNodeResponse buildNodeWithChildren(MenuNode node) {
        var children = menuRepositoryPort.findChildrenByParentId(node.getId());
        var childResponses = children.stream()
                .map(this::buildNodeWithChildren)
                .toList();
        return new MenuNodeResponse(
            node.getId(),
            node.getNombre(),
            node.getUrl(),
            node.getModuleId(),
            node.getParentId(),
            node.getOrden(),
            childResponses
        );
    }
}
