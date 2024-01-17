package pl.piomin.azure.functions.account;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import pl.piomin.azure.functions.account.model.Account;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class AccountInternalFunctions {

    private static final Logger LOG = LoggerFactory.getLogger(AccountInternalFunctions.class);

    @Bean
    public Function<String, Account> addAccount() {
        return req -> {
            LOG.info("Incoming request: {}", req);
            return new Account(req, 1L);
        };
    }

    @Bean
    public Consumer<Account> printAccount() {
        return acc -> LOG.info("Incoming event: {}", acc);
    }
}
