package io.flow;

import static io.flow.db.DbConstants.DEFAULT_TENANT;
import static io.flow.db.DbConstants.TENANT_CHANGESET;

import io.flow.config.ApplicationProperties;
import io.flow.config.FlowInquiryProfiles;
import io.flow.config.FlowInquiryProperties;
import io.flow.db.service.LiquibaseService;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@EnableConfigurationProperties({
    LiquibaseProperties.class,
    ApplicationProperties.class,
    FlowInquiryProperties.class
})
@EntityScan("io.flow")
@EnableAspectJAutoProxy
@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "PT30S")
public class FlowApp {

    private static final Logger LOG = LoggerFactory.getLogger(FlowApp.class);

    private final LiquibaseService liquibaseService;

    private final Environment env;

    public FlowApp(Environment env, LiquibaseService liquibaseService) {
        this.env = env;
        this.liquibaseService = liquibaseService;
    }


    @PostConstruct
    public void initApplication() {
        Collection<String> activeProfiles = Arrays.asList(env.getActiveProfiles());
        if (activeProfiles.contains(FlowInquiryProfiles.SPRING_PROFILE_DEVELOPMENT)
                && activeProfiles.contains(FlowInquiryProfiles.SPRING_PROFILE_PRODUCTION)) {
            LOG.error(
                    "You have misconfigured your application! It should not run "
                            + "with both the 'dev' and 'prod' profiles at the same time.");
        }
        migrateDatabases(activeProfiles);
    }


    public static void main(String[] args) {
        loadEnvVariablesFromEnvFile(".");
        loadEnvVariablesFromEnvFile("..");

        SpringApplication app = new SpringApplication(FlowApp.class);

        app.setDefaultProperties(
                Map.of("spring.profiles.default", FlowInquiryProfiles.SPRING_PROFILE_DEVELOPMENT));
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void loadEnvVariablesFromEnvFile(String path) {
        Dotenv dotEnv =
                Dotenv.configure()
                        .directory(path)
                        .filename(".env.local")
                        .systemProperties()
                        .ignoreIfMissing()
                        .load();
        if (!dotEnv.entries().isEmpty()) {
            LOG.info("Loaded env variables from {}", path + "/.env.local");
        }
    }

    private static void logApplicationStartup(Environment env) {
        String protocol =
                Optional.ofNullable(env.getProperty("server.ssl.key-store"))
                        .map(key -> "https")
                        .orElse("http");
        String applicationName = env.getProperty("spring.application.name");
        String serverPort = env.getProperty("server.port");
        String contextPath =
                Optional.ofNullable(env.getProperty("server.servlet.context-path"))
                        .filter(StringUtils::isNotBlank)
                        .orElse("/");
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            LOG.warn("The host name could not be determined, using `localhost` as fallback");
        }
        LOG.info("----------------------------------------------------------");
        LOG.info("\tApplication '{}' is running! Access URLs:", applicationName);
        LOG.info("\tLocal: \t\t{}://localhost:{}{}", protocol, serverPort, contextPath);
        LOG.info("\tExternal: \t{}://{}:{}{}", protocol, hostAddress, serverPort, contextPath);
        LOG.info(
                "\tProfile(s): \t{}",
                env.getActiveProfiles().length == 0
                        ? env.getDefaultProfiles()
                        : env.getActiveProfiles());
    }

    @Transactional
    void migrateDatabases(Collection<String> activeProfiles) {
        liquibaseService.updateLiquibaseSchema(TENANT_CHANGESET, DEFAULT_TENANT, activeProfiles);
    }
}
