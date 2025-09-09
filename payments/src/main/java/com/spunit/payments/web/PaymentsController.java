package com.spunit.payments.web;

import com.spunit.common.idempotency.IdempotencyKeyStore;
import com.spunit.payments.domain.Payment;
import com.spunit.payments.domain.PaymentFactory;
import com.spunit.payments.domain.PaymentRepository;
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
@RequestMapping(path = "/api/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaymentsController {

    private final PaymentRepository repository;
    private final Optional<IdempotencyKeyStore> idempotency;

    public PaymentsController(PaymentRepository repository, Optional<IdempotencyKeyStore> idempotency) {
        this.repository = repository;
        this.idempotency = idempotency;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<PaymentView>> getById(@PathVariable("id") UUID id) {
        return Mono.fromCallable(() -> repository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .map(opt -> opt.map(PaymentView::from).map(ResponseEntity::ok)
                        .orElseGet(() -> ResponseEntity.notFound().build()));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<PaymentView>> create(@RequestBody CreatePaymentRequest req,
                                                    @RequestHeader(value = "Idempotency-Key", required = false) String idemKey) {
        return Mono.fromCallable(() -> doCreate(req, idemKey))
                .subscribeOn(Schedulers.boundedElastic());
    }

    private ResponseEntity<PaymentView> doCreate(CreatePaymentRequest req, String idemKey) throws Exception {
        // Idempotency: if key present, derive deterministic id; else random id
        UUID id = (idemKey != null && !idemKey.isBlank()) ? deterministicUuid("payments:", idemKey) : UUID.randomUUID();

        // Guard via distributed idempotency store when available
        if (idemKey != null && !idemKey.isBlank() && idempotency.isPresent()) {
            String payloadHash = sha256Hex(req.payerAccountId + ":" + req.payeeId + ":" + req.currency + ":" + req.amount);
            boolean stored = idempotency.get().putIfAbsent("payments:" + idemKey, payloadHash, Duration.ofMinutes(10));
            if (!stored) {
                // Return existing resource if present; otherwise proceed (eventual consistency)
                return repository.findById(id)
                        .map(existing -> ResponseEntity.ok(PaymentView.from(existing)))
                        .orElseGet(() -> proceedCreate(id, req));
            }
        }
        return proceedCreate(id, req);
    }

    private ResponseEntity<PaymentView> proceedCreate(UUID id, CreatePaymentRequest req) {
        Currency currency = Currency.getInstance(req.currency);
        Payment payment = PaymentFactory.create(id, req.payerAccountId, req.payeeId, currency, req.amount);
        Payment saved = repository.save(payment);
        PaymentView view = PaymentView.from(saved);
        return ResponseEntity.created(URI.create("/api/payments/" + saved.getId())).body(view);
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

    public static final class CreatePaymentRequest {
        public UUID payerAccountId;
        public UUID payeeId;
        public String currency;
        public BigDecimal amount;
    }

    public record PaymentView(
            UUID id,
            String status,
            UUID payerAccountId,
            UUID payeeId,
            String currency,
            BigDecimal amount
    ) {
        public static PaymentView from(Payment p) {
            return new PaymentView(
                    p.getId(),
                    p.getStatus().name(),
                    p.getPayerAccountId(),
                    p.getPayeeId(),
                    p.getCurrency().getCurrencyCode(),
                    p.getAmount()
            );
        }
    }
}
