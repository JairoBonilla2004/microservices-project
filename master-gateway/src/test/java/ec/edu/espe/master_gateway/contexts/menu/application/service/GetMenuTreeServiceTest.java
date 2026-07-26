package ec.edu.espe.master_gateway.contexts.menu.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.contexts.identity.domain.port.out.UserRoleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.menu.domain.model.MenuNode;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.MenuRepositoryPort;
import ec.edu.espe.master_gateway.contexts.menu.domain.port.out.RoleMenuAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.contexts.module.domain.port.out.RoleModuleAssignmentRepositoryPort;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetMenuTreeServiceTest {

    @Mock
    private MenuRepositoryPort menuRepositoryPort;
    @Mock
    private RoleMenuAssignmentRepositoryPort roleMenuAssignmentRepositoryPort;
    @Mock
    private RoleModuleAssignmentRepositoryPort roleModuleAssignmentRepositoryPort;
    @Mock
    private AuthorizationPort authorizationPort;
    @Mock
    private UserRoleAssignmentRepositoryPort userRoleAssignmentRepositoryPort;

    private GetMenuTreeService service;
    private UUID roleId;
    private UUID userId;

    @BeforeEach
    void setUp() {
        service = new GetMenuTreeService(menuRepositoryPort, roleMenuAssignmentRepositoryPort,
                roleModuleAssignmentRepositoryPort, authorizationPort, userRoleAssignmentRepositoryPort);
        roleId = UUID.randomUUID();
        userId = UUID.randomUUID();
    }

    @Test
    void should_returnMenuTree_when_userHasRole() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.of(mock()));
        var nodeId = UUID.randomUUID();
        when(roleMenuAssignmentRepositoryPort.findMenuNodeIdsByRoleId(roleId))
                .thenReturn(List.of(nodeId));
        var node = new MenuNode("Dashboard", UUID.randomUUID(), null, 1);
        node.setId(nodeId);
        when(menuRepositoryPort.findSubtreesByNodeIds(List.of(nodeId)))
                .thenReturn(List.of(node));

        var result = service.execute(roleId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Dashboard");
    }

    @Test
    void should_returnEmpty_when_noAssignedNodesOrModules() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.empty());
        when(roleMenuAssignmentRepositoryPort.findMenuNodeIdsByRoleId(roleId))
                .thenReturn(List.of());
        when(roleModuleAssignmentRepositoryPort.findModuleIdsByRoleId(roleId))
                .thenReturn(List.of());

        var result = service.execute(roleId);

        assertThat(result).isEmpty();
    }

    @Test
    void should_returnMenuTreeFromModules_when_noDirectMenuAssignments() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.empty());
        when(roleMenuAssignmentRepositoryPort.findMenuNodeIdsByRoleId(roleId))
                .thenReturn(List.of());
        var moduleId = UUID.randomUUID();
        when(roleModuleAssignmentRepositoryPort.findModuleIdsByRoleId(roleId))
                .thenReturn(List.of(moduleId));
        var rootId = UUID.randomUUID();
        var rootNode = new MenuNode("Module Root", moduleId, null, 1);
        rootNode.setId(rootId);
        when(menuRepositoryPort.findTreeByModuleIds(List.of(moduleId)))
                .thenReturn(List.of(rootNode));

        var result = service.execute(roleId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Module Root");
    }

    @Test
    void should_buildForest_withChildren() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.of(mock()));
        var parentId = UUID.randomUUID();
        var childId = UUID.randomUUID();
        when(roleMenuAssignmentRepositoryPort.findMenuNodeIdsByRoleId(roleId))
                .thenReturn(List.of(parentId));
        var parent = new MenuNode("Parent", UUID.randomUUID(), null, 1);
        parent.setId(parentId);
        var child = new MenuNode("Child", UUID.randomUUID(), parentId, 2);
        child.setId(childId);
        when(menuRepositoryPort.findSubtreesByNodeIds(List.of(parentId)))
                .thenReturn(List.of(parent, child));

        var result = service.execute(roleId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).children()).hasSize(1);
        assertThat(result.get(0).children().get(0).nombre()).isEqualTo("Child");
    }
}
