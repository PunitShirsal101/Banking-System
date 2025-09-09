package com.spunit.accounts.web;

import com.spunit.accounts.domain.Account;
import com.spunit.accounts.domain.AccountFactory;
import com.spunit.accounts.domain.AccountRepository;
import com.spunit.accounts.domain.Balance;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Currency;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/accounts", produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountsController {

    private final AccountRepository repository;

    public AccountsController(AccountRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<AccountView>> getById(@PathVariable("id") UUID id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(opt -> opt.map(AccountView::from).map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @GetMapping
    public Flux<AccountView> list(@RequestParam(value = "size", defaultValue = "20") int size) {
        int s = Math.min(Math.max(size, 1), 100);
        Pageable pageable = PageRequest.of(0, s);
        return Mono.fromCallable(() -> repository.findAll(pageable))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapMany(page -> Flux.fromIterable(page.getContent()))
                .map(AccountView::from);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<AccountView>> create(@RequestBody CreateAccountRequest req) {
        return Mono.fromCallable(() -> doCreate(req))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private ResponseEntity<AccountView> doCreate(CreateAccountRequest req) {
        UUID id = req.id != null ? req.id : UUID.randomUUID();
        Currency currency = Currency.getInstance(req.currency);
        BigDecimal initial = req.initialDeposit == null ? BigDecimal.ZERO : req.initialDeposit;
        BigDecimal hold = req.initialHold == null ? BigDecimal.ZERO : req.initialHold;
        Account acc = AccountFactory.createWithInitialDepositAndHold(id, currency, initial, hold);
        Account saved = repository.save(acc);
        AccountView view = AccountView.from(saved);
        return ResponseEntity.created(URI.create("/api/accounts/" + saved.getId())).body(view);
    }

    public static final class CreateAccountRequest {
        public UUID id;
        public String currency;
        public BigDecimal initialDeposit;
        public BigDecimal initialHold;
    }

    public record AccountView(
            UUID id,
            String status,
            String currency,
            BigDecimal balance,
            BigDecimal hold,
            BigDecimal available,
            long version
    ) {
        public static AccountView from(Account a) {
            Balance b = a.getBalance();
            return new AccountView(
                    a.getId(),
                    a.getStatus().name(),
                    b.getCurrency().getCurrencyCode(),
                    b.getAmount(),
                    a.getHoldAmount(),
                    a.getAvailable(),
                    a.getVersion()
            );
        }
    }
}
