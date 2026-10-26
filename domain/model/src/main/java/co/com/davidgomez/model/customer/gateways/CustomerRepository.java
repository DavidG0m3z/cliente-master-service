package co.com.davidgomez.model.customer.gateways;

import co.com.davidgomez.model.customer.Customer;
import co.com.davidgomez.model.customer.IdentityDocument;

import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findByDocument(IdentityDocument document);

    Customer save(Customer customer);
}
