package pl.piomin.azure.account;

import com.azure.spring.data.cosmos.repository.config.EnableCosmosRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableCosmosRepositories
public class AzureAccountApplication {

    public static void main(String[] args) {
        SpringApplication.run(AzureAccountApplication.class, args);
    }
}
