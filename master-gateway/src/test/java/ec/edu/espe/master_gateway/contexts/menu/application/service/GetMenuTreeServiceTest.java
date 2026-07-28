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

    @Test
    void should_filterOrphanNodes_when_parentIsInRootIds() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.empty());
        var moduleId = UUID.randomUUID();
        when(roleModuleAssignmentRepositoryPort.findModuleIdsByRoleId(roleId))
                .thenReturn(List.of(moduleId));
        var parentId = UUID.randomUUID();
        var childOfB = UUID.randomUUID();
        var parent = new MenuNode("Parent", moduleId, null, 2);
        parent.setId(parentId);
        var child = new MenuNode("Child", moduleId, parentId, 3);
        child.setId(childOfB);
        when(menuRepositoryPort.findTreeByModuleIds(List.of(moduleId)))
                .thenReturn(List.of(parent, child));

        var result = service.execute(roleId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Parent");
        assertThat(result.get(0).children()).hasSize(1);
        assertThat(result.get(0).children().get(0).nombre()).isEqualTo("Child");
    }

    @Test
    void should_sortRootsByOrden() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.empty());
        var moduleId = UUID.randomUUID();
        when(roleModuleAssignmentRepositoryPort.findModuleIdsByRoleId(roleId))
                .thenReturn(List.of(moduleId));
        var firstId = UUID.randomUUID();
        var secondId = UUID.randomUUID();
        var thirdId = UUID.randomUUID();
        var first = new MenuNode("Z Last", moduleId, null, 3);
        first.setId(firstId);
        var second = new MenuNode("A First", moduleId, null, 1);
        second.setId(secondId);
        var third = new MenuNode("M Middle", moduleId, null, 2);
        third.setId(thirdId);
        when(menuRepositoryPort.findTreeByModuleIds(List.of(moduleId)))
                .thenReturn(List.of(first, second, third));

        var result = service.execute(roleId);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).nombre()).isEqualTo("A First");
        assertThat(result.get(0).orden()).isEqualTo(1);
        assertThat(result.get(1).nombre()).isEqualTo("M Middle");
        assertThat(result.get(1).orden()).isEqualTo(2);
        assertThat(result.get(2).nombre()).isEqualTo("Z Last");
        assertThat(result.get(2).orden()).isEqualTo(3);
    }

    @Test
    void should_sortChildrenByOrden() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.of(mock()));
        var parentId = UUID.randomUUID();
        var firstChildId = UUID.randomUUID();
        var secondChildId = UUID.randomUUID();
        var thirdChildId = UUID.randomUUID();
        when(roleMenuAssignmentRepositoryPort.findMenuNodeIdsByRoleId(roleId))
                .thenReturn(List.of(parentId));
        var parent = new MenuNode("Parent", UUID.randomUUID(), null, 1);
        parent.setId(parentId);
        var first = new MenuNode("C Last", UUID.randomUUID(), parentId, 3);
        first.setId(firstChildId);
        var second = new MenuNode("A First", UUID.randomUUID(), parentId, 1);
        second.setId(secondChildId);
        var third = new MenuNode("B Middle", UUID.randomUUID(), parentId, 2);
        third.setId(thirdChildId);
        when(menuRepositoryPort.findSubtreesByNodeIds(List.of(parentId)))
                .thenReturn(List.of(parent, first, second, third));

        var result = service.execute(roleId);

        assertThat(result).hasSize(1);
        var children = result.get(0).children();
        assertThat(children).hasSize(3);
        assertThat(children.get(0).nombre()).isEqualTo("A First");
        assertThat(children.get(0).orden()).isEqualTo(1);
        assertThat(children.get(1).nombre()).isEqualTo("B Middle");
        assertThat(children.get(1).orden()).isEqualTo(2);
        assertThat(children.get(2).nombre()).isEqualTo("C Last");
        assertThat(children.get(2).orden()).isEqualTo(3);
    }

    @Test
    void should_skipMissingNode_when_rootIdNotFoundInNodes() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.empty());
        var moduleId = UUID.randomUUID();
        when(roleModuleAssignmentRepositoryPort.findModuleIdsByRoleId(roleId))
                .thenReturn(List.of(moduleId));
        var validId = UUID.randomUUID();
        var missingId = UUID.randomUUID();
        var validNode = new MenuNode("Valid", moduleId, null, 1);
        validNode.setId(validId);
        when(menuRepositoryPort.findTreeByModuleIds(List.of(moduleId)))
                .thenReturn(List.of(validNode));

        var result = service.execute(roleId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("Valid");
    }

    @Test
    void should_treatDirectAssignedNodeAsRoot_when_parentNotInRootIds() {
        when(authorizationPort.getCurrentUserId()).thenReturn(userId);
        when(userRoleAssignmentRepositoryPort.findByUserIdAndRoleId(userId, roleId))
                .thenReturn(Optional.of(mock()));
        var parentId = UUID.randomUUID();
        var childId = UUID.randomUUID();
        var parentOutsideId = UUID.randomUUID();
        when(roleMenuAssignmentRepositoryPort.findMenuNodeIdsByRoleId(roleId))
                .thenReturn(List.of(childId));
        var parentOutside = new MenuNode("ParentOutside", UUID.randomUUID(), null, 1);
        parentOutside.setId(parentOutsideId);
        var child = new MenuNode("DirectChild", UUID.randomUUID(), parentOutsideId, 2);
        child.setId(childId);
        when(menuRepositoryPort.findSubtreesByNodeIds(List.of(childId)))
                .thenReturn(List.of(parentOutside, child));

        var result = service.execute(roleId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).nombre()).isEqualTo("DirectChild");
    }
}
