package br.com.richardcsantana.starwarsjavaapi;

import br.com.richardcsantana.starwarsjavaapi.domain.ApiReaderService;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

@SpringBootApplication
@EnableR2dbcRepositories
public class StarWarsJavaApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(StarWarsJavaApiApplication.class, args);
    }

    @Bean
    @Profile("batch")
    CommandLineRunner init(ApiReaderService apiReaderService) {
        return args -> {
            apiReaderService.consumeAPI().block();
        };
    }

    @Bean
    ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
        ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
        return initializer;
    }

}
