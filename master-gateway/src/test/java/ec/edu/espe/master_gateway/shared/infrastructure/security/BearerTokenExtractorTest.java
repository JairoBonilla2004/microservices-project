package ec.edu.espe.master_gateway.shared.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class BearerTokenExtractorTest {

    private final BearerTokenExtractor extractor = new BearerTokenExtractor();

    @Test
    void should_returnToken_when_headerHasBearerPrefix() {
        var result = extractor.extract("Bearer my-jwt-token");

        assertThat(result).hasValue("my-jwt-token");
    }

    static Stream<String> invalidHeaders() {
        return Stream.of(null, "Basic credentials", "");
    }

    @ParameterizedTest
    @MethodSource("invalidHeaders")
    void should_returnEmpty_when_headerIsInvalid(String header) {
        var result = extractor.extract(header);

        assertThat(result).isEmpty();
    }
}
