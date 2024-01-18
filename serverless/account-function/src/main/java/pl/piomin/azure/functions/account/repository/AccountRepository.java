package pl.piomin.azure.functions.account.repository;

import org.springframework.data.repository.ListCrudRepository;
import pl.piomin.azure.functions.account.model.Account;

public interface AccountRepository extends ListCrudRepository<Account, Long> {
}
