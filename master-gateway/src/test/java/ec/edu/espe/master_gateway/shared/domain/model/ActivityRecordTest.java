package ec.edu.espe.master_gateway.shared.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ActivityRecordTest {

    @Test
    void should_createActivityRecord() {
        var now = LocalDateTime.now();

        var activity = new ActivityRecord("John Doe", "admin", now, now);

        assertThat(activity.name()).isEqualTo("John Doe");
        assertThat(activity.actor()).isEqualTo("admin");
        assertThat(activity.createdAt()).isEqualTo(now);
        assertThat(activity.updatedAt()).isEqualTo(now);
    }
}
