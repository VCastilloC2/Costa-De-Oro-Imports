package com.application.configuration.fastapi;

import com.application.service.implementation.DashboardApiClient;
import com.application.service.implementation.PrediccionApiClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifica que los beans de FastAPI se cablean solos, sin depender de otros auto-configs. */
class FastApiClientConfigContextTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(FastApiClientConfig.class, PrediccionApiClient.class, DashboardApiClient.class);

    @Test
    void arrancaEnPerfilLocal() {
        runner.withPropertyValues("fastapi.base-url=http://localhost:8000", "fastapi.api-key=")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    assertThat(context).hasSingleBean(PrediccionApiClient.class);
                    assertThat(context).hasSingleBean(DashboardApiClient.class);
                    assertThat(context).hasBean("fastApiPredictionGateway");
                });
    }

    @Test
    void cloudSinHttpsNoArranca() {
        runner.withPropertyValues("fastapi.base-url=http://api.example.com", "fastapi.api-key=cdo_pk_x",
                        "fastapi.require-https=true")
                .run(context -> assertThat(context).hasFailed());
    }
}
