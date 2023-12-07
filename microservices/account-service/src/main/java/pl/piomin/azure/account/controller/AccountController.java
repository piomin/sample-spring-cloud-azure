package pl.piomin.azure.account.controller;

import com.azure.spring.cloud.feature.management.web.FeatureGate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import pl.piomin.azure.account.model.Account;
import pl.piomin.azure.account.repository.AccountRepository;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final static Logger LOG = LoggerFactory.getLogger(AccountController.class);
    private final AccountRepository repository;

    public AccountController(AccountRepository repository) {
        this.repository = repository;
    }

    @PostMapping
    public Account add(@RequestBody Account account) {
        LOG.info("add: {}", account.getNumber());
        return repository.save(account);
    }

    @GetMapping("/{id}")
    public Account findById(@PathVariable String id) {
        LOG.info("findById: {}", id);
        return repository.findById(id).orElseThrow();
    }

    @GetMapping
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        repository.findAll().forEach(accounts::add);
        return accounts;
    }

    @GetMapping("/customer/{customerId}")
    public List<Account> findByCustomerId(@PathVariable String customerId) {
        LOG.info("findByCustomerId: {}", customerId);
        return repository.findByCustomerId(customerId);
    }

    @DeleteMapping("/{id}")
    @FeatureGate(feature = "delete-account")
    public void deleteById(@PathVariable String id) {
        repository.deleteById(id);
    }

}
