package pl.piomin.azure.functions.account;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import pl.piomin.azure.functions.account.model.Account;
import pl.piomin.azure.functions.account.model.Customer;
import pl.piomin.azure.functions.account.repository.AccountRepository;

import java.util.function.Function;

@Service
public class AccountInternalFunctions {

    private static final Logger LOG = LoggerFactory.getLogger(AccountInternalFunctions.class);

    private StreamBridge streamBridge;
    private AccountRepository repository;

    public AccountInternalFunctions(StreamBridge streamBridge, AccountRepository repository) {
        this.streamBridge = streamBridge;
        this.repository = repository;
    }

    @Bean
    public Function<Customer, Account> addAccount() {
        return customer -> {
            String n = RandomStringUtils.random(16, false, true);
            Account a = new Account(n, customer.getId(), 0);
            a = repository.save(a);
            boolean b = streamBridge.send("accounts-out-0", a);
            LOG.info("New account added: {}", a);
            return a;
        };
    }

}
