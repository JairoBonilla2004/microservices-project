package ec.edu.espe.master_gateway.shared.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import ec.edu.espe.master_gateway.shared.domain.AuthorizationException;
import ec.edu.espe.master_gateway.shared.domain.model.ActivityRecord;
import ec.edu.espe.master_gateway.shared.domain.permission.Permission;
import ec.edu.espe.master_gateway.shared.domain.port.out.AuthorizationPort;
import ec.edu.espe.master_gateway.shared.domain.port.out.RecentActivityPort;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetRecentActivityServiceTest {

    @Mock
    private RecentActivityPort recentActivityPort;
    @Mock
    private AuthorizationPort authorizationPort;

    private GetRecentActivityService service;

    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        service = new GetRecentActivityService(recentActivityPort, authorizationPort);
        now = LocalDateTime.now();
    }

    @Test
    void should_returnCombinedAndSortedActivities() {
        var userActivity = List.of(new ActivityRecord("jdoe", "admin", now, now));
        var roleActivity = List.of(new ActivityRecord("ADMIN", "admin", now.minusHours(1), now.minusHours(1)));

        when(recentActivityPort.findRecentUsers(10)).thenReturn(userActivity);
        when(recentActivityPort.findRecentRoles(10)).thenReturn(roleActivity);
        when(recentActivityPort.findRecentModules(10)).thenReturn(List.of());
        when(recentActivityPort.findRecentMenuItems(10)).thenReturn(List.of());
        when(recentActivityPort.findRecentServices(10)).thenReturn(List.of());

        var result = service.execute(10);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).entityType()).isEqualTo("Usuario");
        assertThat(result.get(1).entityType()).isEqualTo("Rol");
    }

    @Test
    void should_returnEmptyList_when_noActivity() {
        when(recentActivityPort.findRecentUsers(10)).thenReturn(List.of());
        when(recentActivityPort.findRecentRoles(10)).thenReturn(List.of());
        when(recentActivityPort.findRecentModules(10)).thenReturn(List.of());
        when(recentActivityPort.findRecentMenuItems(10)).thenReturn(List.of());
        when(recentActivityPort.findRecentServices(10)).thenReturn(List.of());

        var result = service.execute(10);

        assertThat(result).isEmpty();
    }

    @Test
    void should_limitResults() {
        var activities = List.of(
                new ActivityRecord("a", "admin", now, now),
                new ActivityRecord("b", "admin", now.minusMinutes(1), now.minusMinutes(1)),
                new ActivityRecord("c", "admin", now.minusMinutes(2), now.minusMinutes(2))
        );
        when(recentActivityPort.findRecentUsers(2)).thenReturn(activities.subList(0, 2));
        when(recentActivityPort.findRecentRoles(2)).thenReturn(List.of());
        when(recentActivityPort.findRecentModules(2)).thenReturn(List.of());
        when(recentActivityPort.findRecentMenuItems(2)).thenReturn(List.of());
        when(recentActivityPort.findRecentServices(2)).thenReturn(List.of());

        var result = service.execute(2);

        assertThat(result).hasSize(2);
    }

    @Test
    void should_throw_when_notAuthorized() {
        doThrow(new AuthorizationException("Acceso denegado"))
                .when(authorizationPort).requirePermission(Permission.USERS_READ);

        assertThatThrownBy(() -> service.execute(10))
                .isInstanceOf(AuthorizationException.class);
    }
}
