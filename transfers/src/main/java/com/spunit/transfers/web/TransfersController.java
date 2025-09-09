package com.spunit.transfers.web;

import com.spunit.common.idempotency.IdempotencyKeyStore;
import com.spunit.transfers.domain.Transfer;
import com.spunit.transfers.domain.TransferRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/transfers", produces = MediaType.APPLICATION_JSON_VALUE)
public class TransfersController {

    private final TransferRepository repository;
    private final Optional<IdempotencyKeyStore> idempotency;

    public TransfersController(TransferRepository repository, Optional<IdempotencyKeyStore> idempotency) {
        this.repository = repository;
        this.idempotency = idempotency;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<TransferView>> getById(@PathVariable("id") UUID id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(opt -> opt.map(TransferView::from).map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<TransferView>> create(@RequestBody CreateTransferRequest req,
                                                     @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {
        return Mono.fromCallable(() -> doCreate(req, idemKey))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private ResponseEntity<TransferView> doCreate(CreateTransferRequest req, String idemKey) throws Exception {
        if (idemKey == null || idemKey.isBlank()) {
            // No idempotency key: create fresh transfer
            return persistTransfer(UUID.randomUUID(), req, null);
        }
        // Try distributed guard if available
        String payloadHash = sha256Hex(req.fromAccountId + ":" + req.toAccountId + ":" + req.currency + ":" + req.amount);
        if (idempotency.isPresent()) {
            boolean stored = idempotency.get().putIfAbsent("transfers:" + idemKey, payloadHash, Duration.ofMinutes(10));
            if (!stored) {
                // Return existing by idempotency key if present
                return repository.findByIdempotencyKey(idemKey)
                        .map(existing -> ResponseEntity.ok(TransferView.from(existing)))
                        .orElseGet(() -> persistTransfer(deterministicUuid("transfers:", idemKey), req, idemKey));
            }
        }
        // Deterministic ID to help dedup even without store
        return persistTransfer(deterministicUuid("transfers:", idemKey), req, idemKey);
    }

    private ResponseEntity<TransferView> persistTransfer(UUID id, CreateTransferRequest req, String idemKey) {
        Currency currency = Currency.getInstance(req.currency);
        String key = idemKey != null && !idemKey.isBlank() ? idemKey : id.toString();
        Transfer transfer = new Transfer(id, req.fromAccountId, req.toAccountId, currency, req.amount, key);
        Transfer saved = repository.save(transfer);
        TransferView view = TransferView.from(saved);
        return ResponseEntity.created(URI.create("/api/transfers/" + saved.getId())).body(view);
    }

    private static UUID deterministicUuid(String namespace, String key) {
        return UUID.nameUUIDFromBytes((namespace + key).getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256Hex(String s) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] d = md.digest(s.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : d) sb.append(String.format("%02x", b));
        return sb.toString();
    }

    public static final class CreateTransferRequest {
        public UUID fromAccountId;
        public UUID toAccountId;
        public String currency;
        public BigDecimal amount;
    }

    public record TransferView(
            UUID id,
            String status,
            UUID fromAccountId,
            UUID toAccountId,
            String currency,
            BigDecimal amount,
            String idempotencyKey
    ) {
        public static TransferView from(Transfer t) {
            return new TransferView(
                    t.getId(),
                    t.getStatus().name(),
                    t.getFromAccountId(),
                    t.getToAccountId(),
                    t.getCurrency().getCurrencyCode(),
                    t.getAmount(),
                    t.getIdempotencyKey()
            );
        }
    }
}
