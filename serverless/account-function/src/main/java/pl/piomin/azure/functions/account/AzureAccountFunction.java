package pl.piomin.azure.functions.account;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.EventHubTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
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
                             dataType = "string",
                             name = "events",
                             connection = "AzureWebJobsEventHubSender")
            String message,
            ExecutionContext context) {
        context.getLogger().info("Event: " + message);
        printAccount.accept(new Account());
    }
}
