package br.com.richardcsantana.starwarsjavaapi;

import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@RunWith(SpringRunner.class)
@ContextConfiguration(initializers = {ApplicationTestsBase.Initializer.class})
public abstract class ApplicationTestsBase {

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:13.2")
            .withDatabaseName("test_star_wars")
            .withUsername("test")
            .withPassword("test");

    public static final DockerImageName MOCKSERVER_IMAGE = DockerImageName
            .parse("mockserver/mockserver:mockserver-5.14.0");

    @ClassRule
    public static MockServerContainer mockServer = new MockServerContainer(MOCKSERVER_IMAGE);
    protected static String serverUrl;

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            postgreSQLContainer.start();
            mockServer.start();
            mockServer.getEndpoint();
            serverUrl = mockServer.getEndpoint();
            TestPropertyValues.of(
                    "swapi.url=" + serverUrl,
                    "spring.r2dbc.url=r2dbc:postgresql://%s:%s/%s?schema=public".formatted(
                            postgreSQLContainer.getHost(),
                            postgreSQLContainer.getFirstMappedPort(),
                            postgreSQLContainer.getDatabaseName()),
                    "spring.r2dbc.username=" + postgreSQLContainer.getUsername(),
                    "spring.r2dbc.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
