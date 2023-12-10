package org.backup.tools.repositories.gdrive;

import org.backup.tools.test.IntegratedTest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


class ConfigurationTest {

    @IntegratedTest
    @Test
    void shouldLoadConfigurationFromHome() {
        final Configuration configuration = Configuration.load();

        assertThat(configuration.application().name()).isNotBlank();
        assertThat(configuration.application().scopes()).isNotEmpty();
        assertThat(configuration.application().credentials()).isNotNull();
        assertThat(configuration.options().tokensPath()).isNotBlank();
    }

    @Test
    void shouldLoadConfigurationFromClasspath() {
        final Configuration configuration = Configuration.load("classpath:/configs/test-config.yaml");

        assertThat(configuration.application().name()).isEqualTo("test-app");
        assertThat(configuration.application().scopes()).containsExactlyInAnyOrder("read_all", "write_none");
        assertThat(configuration.application().credentials()).isNotNull();
        assertThat(configuration.options().tokensPath()).isEqualTo("tokens_and_tokens");
    }
}
