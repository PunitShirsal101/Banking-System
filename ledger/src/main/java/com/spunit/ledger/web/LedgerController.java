package com.spunit.ledger.web;

import com.spunit.ledger.domain.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Currency;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/api/ledger/postings", produces = MediaType.APPLICATION_JSON_VALUE)
public class LedgerController {

    private final LedgerPostingRepository repository;

    public LedgerController(LedgerPostingRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<LedgerPosting>> getById(@PathVariable("id") UUID id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(opt -> opt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<LedgerPosting>> create(@RequestBody CreatePostingRequest req) {
        return Mono.fromCallable(() -> doCreate(req)).subscribeOn(Schedulers.boundedElastic());
    }

    private ResponseEntity<LedgerPosting> doCreate(CreatePostingRequest req) {
        List<LedgerEntry> entries = req.entries.stream()
                .map(e -> new LedgerEntry(e.accountId,
                        EntryType.valueOf(e.type.toUpperCase()),
                        Currency.getInstance(e.currency),
                        e.amount))
                .collect(Collectors.toList());
        LedgerPosting posting = new LedgerPosting(UUID.randomUUID(), entries);
        LedgerPosting saved = repository.save(posting);
        return ResponseEntity.created(URI.create("/api/ledger/postings/" + saved.getId())).body(saved);
    }

    public static final class CreatePostingRequest {
        public List<CreateEntry> entries;
    }

    public static final class CreateEntry {
        public UUID accountId;
        public String type; // DEBIT or CREDIT
        public String currency;
        public BigDecimal amount;
    }
}
