package pl.piomin.azure.functions.customer.repository;

import org.springframework.data.repository.ListCrudRepository;
import pl.piomin.azure.functions.customer.model.Customer;

public interface CustomerRepository extends ListCrudRepository<Customer, Long> {
}
