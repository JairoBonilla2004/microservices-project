package ec.edu.espe.master_gateway.shared.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class PageResultTest {

    @Test
    void should_createPageResult() {
        var content = List.of("a", "b", "c");

        var page = new PageResult<>(content, 10L, 0, 3);

        assertThat(page.content()).isEqualTo(content);
        assertThat(page.totalElements()).isEqualTo(10L);
        assertThat(page.page()).isZero();
        assertThat(page.size()).isEqualTo(3);
    }

    @Test
    void should_returnCorrectPagingInfo() {
        var page = new PageResult<>(List.of(1, 2), 25L, 2, 10);

        assertThat(page.totalPages()).isEqualTo(3);
    }
}
