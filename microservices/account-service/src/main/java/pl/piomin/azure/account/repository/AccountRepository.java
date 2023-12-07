package pl.piomin.azure.account.repository;

import com.azure.spring.data.cosmos.repository.CosmosRepository;
import pl.piomin.azure.account.model.Account;

import java.util.List;

public interface AccountRepository extends CosmosRepository<Account, String> {
    List<Account> findByCustomerId(String customerId);
}
