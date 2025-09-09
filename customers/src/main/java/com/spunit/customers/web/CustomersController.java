package com.spunit.customers.web;

import com.spunit.customers.domain.ContactInfo;
import com.spunit.customers.domain.Customer;
import com.spunit.customers.domain.CustomerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/customers", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomersController {

    private final CustomerRepository repository;

    public CustomersController(CustomerRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Customer>> getById(@PathVariable("id") UUID id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Customer>> create(@RequestBody CreateCustomerRequest req) {
        UUID id = req.id != null ? req.id : UUID.randomUUID();
        Customer customer = new Customer(id, req.firstName, req.lastName, new ContactInfo(req.email, req.phone));
        return repository.save(customer)
                .map(saved -> ResponseEntity.created(URI.create("/api/customers/" + saved.getId())).body(saved));
    }

    public static final class CreateCustomerRequest {
        public UUID id;
        public String firstName;
        public String lastName;
        public String email;
        public String phone;
    }
}
