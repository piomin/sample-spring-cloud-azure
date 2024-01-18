package pl.piomin.azure.functions.customer;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import pl.piomin.azure.functions.customer.model.Account;
import pl.piomin.azure.functions.customer.model.Customer;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SpringBootApplication
public class AzureCustomerFunction {

    public static void main(String[] args) {
        SpringApplication.run(AzureCustomerFunction.class, args);
    }

    @Autowired
    private FunctionCatalog functionCatalog;

    @FunctionName("add-customer")
    public Customer addCustomerFunc(
            @HttpTrigger(name = "req",
                         methods = { HttpMethod.POST },
                         authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<Customer>> request,
            ExecutionContext context) {
        Customer c = request.getBody().orElseThrow();
        context.getLogger().info("Request: {}" + c);
        Function<Customer, Customer> function = functionCatalog.lookup("addCustomer");
        return function.apply(c);
    }

    @FunctionName("activate-customer")
    public void activateCustomerEventFunc(
            @EventHubTrigger(eventHubName = "accounts",
                    name = "changeStatusTrigger",
                    connection = "EVENT_HUBS_CONNECTION_STRING",
                    cardinality = Cardinality.MANY)
            Account event,
            ExecutionContext context) {
        context.getLogger().info("Event: {}" + event);
        Consumer<Account> consumer = functionCatalog.lookup("changeStatus");
        consumer.accept(event);
    }
}
