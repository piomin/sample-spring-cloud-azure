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
    public Function<String, Account> addAccount;
    @Autowired
    public Consumer<Account> printAccount;
    @Autowired
    private FunctionCatalog functionCatalog;

    @FunctionName("addAccount")
    public Account addAccountFunc(
            @HttpTrigger(name = "req",
                         methods = { HttpMethod.POST },
                         authLevel = AuthorizationLevel.ANONYMOUS)
            HttpRequestMessage<Optional<String>> request,
            ExecutionContext context) {
//        Function func = functionCatalog.lookup("addAccount");
        String body = request.getBody().orElseThrow();
        context.getLogger().info("Msg: " + body);
        return addAccount.apply(body);
    }

    @FunctionName("newAccountEvent")
    public void newAccountEventFunc(
            @EventHubTrigger(eventHubName = "accounts",
                             name = "events",
                             connection = "AzureWebJobsEventHubSender",
                             cardinality = Cardinality.MANY)
            String message) {
        LOG.info("Msg-Event: {}", message);
//        context.getLogger().info("Event: " + message);
//        output.setValue(message);
        printAccount.accept(new Account());
    }
}
