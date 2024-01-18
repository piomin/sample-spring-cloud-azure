package pl.piomin.azure.functions.customer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import pl.piomin.azure.functions.customer.model.Account;
import pl.piomin.azure.functions.customer.model.Customer;
import pl.piomin.azure.functions.customer.repository.CustomerRepository;

import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class CustomerInternalFunctions {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerInternalFunctions.class);

    private StreamBridge streamBridge;
    private CustomerRepository repository;

    public CustomerInternalFunctions(StreamBridge streamBridge, CustomerRepository repository) {
        this.streamBridge = streamBridge;
        this.repository = repository;
    }

    @Bean
    public Function<Customer, Customer> addCustomer() {
        return c -> {
            Customer newCustomer = repository.save(c);
            streamBridge.send("customers-out-0", newCustomer);
            LOG.info("New customer added: {}", c);
            return newCustomer;
        };
    }

    @Bean
    public Consumer<Account> changeStatus() {
        return account -> {
            Customer customer = repository.findById(account.getCustomerId())
                    .orElseThrow();
            customer.setStatus(Customer.CUSTOMER_STATUS_ACC_ACTIVE);
            repository.save(customer);
            LOG.info("Customer activated: id={}", customer.getId());
        };
    }

}
