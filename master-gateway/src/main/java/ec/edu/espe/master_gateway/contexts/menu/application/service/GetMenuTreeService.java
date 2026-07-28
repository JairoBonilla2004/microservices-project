package ec.edu.espe.master_gateway.contexts.menu.application.service;

import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.GetMenuTreeUseCase;
import ec.edu.espe.master_gateway.contexts.menu.application.port.in.dto.MenuNodeResponse;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.RoleMenuAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.RoleModuleAssignmentRepositoryPort;

import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
            // Una sola consulta CTE trae los nodos asignados y todo su subárbol.
            var flatNodes = menuRepositoryPort.findSubtreesByNodeIds(assignedNodeIds);
            var response = buildForest(flatNodes, assignedNodeIds);
            log.debug("Menu tree retrieved for roleId={} with {} assigned nodes", roleId, response.size());
            return response;
        }

        var moduleIds = roleModuleAssignmentRepositoryPort.findModuleIdsByRoleId(roleId);
        if (moduleIds.isEmpty()) {
            log.debug("No menu tree for roleId={}: no assigned nodes or modules", roleId);
            return Collections.emptyList();
        }

        // Una sola consulta CTE trae todo el árbol (raíces + descendientes) de los módulos.
        var flatNodes = menuRepositoryPort.findTreeByModuleIds(moduleIds);
        var rootIds = flatNodes.stream()
                .filter(n -> n.getParentId() == null)
                .map(MenuNode::getId)
                .toList();
        var response = buildForest(flatNodes, rootIds);

        log.debug("Menu tree retrieved for roleId={} from {} modules", roleId, moduleIds.size());
        return response;
    }

    /**
     * Construye el bosque de {@link MenuNodeResponse} a partir de una lista plana de
     * nodos (ya recuperada en una única consulta CTE) y los ids de las raíces deseadas.
     *
     * <p>Evita el problema N+1: el árbol se arma en memoria indexando los nodos por su
     * {@code parentId}, sin volver a consultar la base de datos por cada nivel.</p>
     */
    private List<MenuNodeResponse> buildForest(List<MenuNode> flatNodes, List<UUID> rootIds) {
        Map<UUID, List<MenuNode>> childrenByParent = new LinkedHashMap<>();
        Map<UUID, MenuNode> nodesById = new LinkedHashMap<>();
        for (MenuNode node : flatNodes) {
            nodesById.put(node.getId(), node);
            childrenByParent.computeIfAbsent(node.getParentId(), k -> new java.util.ArrayList<>()).add(node);
        }

        var topLevelIds = rootIds.stream()
                .filter(id -> {
                    var node = nodesById.get(id);
                    if (node == null) return false;
                    return node.getParentId() == null || !rootIds.contains(node.getParentId());
                })
                .toList();

        return topLevelIds.stream()
                .map(nodesById::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(MenuNode::getOrden))
                .map(root -> toResponse(root, childrenByParent))
                .toList();
    }

    private MenuNodeResponse toResponse(MenuNode node, Map<UUID, List<MenuNode>> childrenByParent) {
        var children = childrenByParent.getOrDefault(node.getId(), List.of()).stream()
                .sorted(Comparator.comparingInt(MenuNode::getOrden))
                .map(child -> toResponse(child, childrenByParent))
                .toList();
        return new MenuNodeResponse(
            node.getId(),
            node.getNombre(),
            node.getUrl(),
            node.getModuleId(),
            node.getParentId(),
            node.getOrden(),
            children
        );
    }
}
