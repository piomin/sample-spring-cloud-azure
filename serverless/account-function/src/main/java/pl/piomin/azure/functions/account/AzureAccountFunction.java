package pl.piomin.azure.functions.account;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.OutputBinding;
import com.microsoft.azure.functions.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.function.context.FunctionCatalog;
import org.springframework.context.annotation.Bean;
import pl.piomin.azure.functions.account.model.Account;
import pl.piomin.azure.functions.account.model.Customer;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@SpringBootApplication
public class AzureAccountFunction {

    private static final Logger LOG = LoggerFactory.getLogger(AzureAccountFunction.class);

    public static void main(String[] args) {
        SpringApplication.run(AzureAccountFunction.class, args);
    }

    @Autowired
    private FunctionCatalog functionCatalog;

//    @FunctionName("addAccount")
//    public Account addAccountFunc(
//            @HttpTrigger(name = "req",
//                         methods = { HttpMethod.POST },
//                         authLevel = AuthorizationLevel.ANONYMOUS)
//            HttpRequestMessage<Optional<String>> request,
//            ExecutionContext context) {
////        Function func = functionCatalog.lookup("addAccount");
//        String body = request.getBody().orElseThrow();
//        context.getLogger().info("Msg: " + body);
//        return addAccount.apply(body);
//    }

    @FunctionName("new-customer")
    public void newAccountEventFunc(
            @EventHubTrigger(eventHubName = "customers",
                             name = "newAccountTrigger",
                             connection = "EVENT_HUBS_CONNECTION_STRING",
                             cardinality = Cardinality.MANY)
            Customer event,
            ExecutionContext context) {
        context.getLogger().info("Event: {}" + event);
        Function<Customer, Account> function = functionCatalog.lookup("addAccount");
        function.apply(event);
    }
}
