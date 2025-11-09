package io.flow.health;

import io.flow.config.FlowProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class JWTSetupChecker implements ApplicationRunner {

    private final FlowProperties flowProperties;

    public JWTSetupChecker(FlowProperties flowProperties) {
        this.flowProperties = flowProperties;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (StringUtils.isEmpty(
                flowProperties
                        .getSecurity()
                        .getAuthentication()
                        .getJwt()
                        .getBase64Secret())) {
            throw new IllegalArgumentException("JWT secret is missing");
        } else {
            log.info("JWT secret found and ready to use");
        }
    }
}
