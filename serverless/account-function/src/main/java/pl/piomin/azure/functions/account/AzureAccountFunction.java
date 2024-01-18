package pl.piomin.azure.functions.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.Cardinality;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import pl.piomin.azure.functions.account.model.Account;
import pl.piomin.azure.functions.account.model.Customer;

import java.util.function.Function;

@SpringBootApplication
public class AzureAccountFunction {

    private static final Logger LOG = LoggerFactory.getLogger(AzureAccountFunction.class);

    public static void main(String[] args) {
        SpringApplication.run(AzureAccountFunction.class, args);
    }

    @Autowired
    private FunctionCatalog functionCatalog;

    @FunctionName("new-customer")
    public void newAccountEventFunc(
            @EventHubTrigger(eventHubName = "customers",
                             name = "newAccountTrigger",
                             connection = "EVENT_HUBS_CONNECTION_STRING",
                             cardinality = Cardinality.ONE)
            Customer event,
            ExecutionContext context) throws JsonProcessingException {
        context.getLogger().info("Event: " + event);
        Function<Customer, Account> function = functionCatalog.lookup("addAccount");
        function.apply(event);
    }
}
