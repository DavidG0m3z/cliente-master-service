package co.com.davidgomez.jpa.customer;

import co.com.davidgomez.model.customer.Customer;
import co.com.davidgomez.model.customer.IdentityDocument;
import co.com.davidgomez.model.customer.gateways.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {

    private final CustomerJpaRepository jpaRepository;

    @Override
    public Optional<Customer> findByDocument(IdentityDocument document) {
        return jpaRepository
                .findByDocumentTypeAndDocumentNumber(document.type(), document.number())
                .map(this :: toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = customer.getId() == null
                ? new CustomerEntity()
                : jpaRepository.findById(customer.getId()).orElseGet(CustomerEntity::new);

        entity.setDocumentType(customer.getDocument().type());
        entity.setDocumentNumber(customer.getDocument().number());
        entity.setName(customer.getName());
        entity.setEmail(customer.getEmail());
        entity.setPhone(customer.getPhone());
        entity.setSource(customer.getSource());
        entity.setDataTimestamp(customer.getDataTimestamp());

        return toDomain(jpaRepository.save(entity));

    }


    private Customer toDomain(CustomerEntity e) {
        return Customer.builder()
                .id(e.getId())
                .document(new IdentityDocument(e.getDocumentType(), e.getDocumentNumber()))
                .name(e.getName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .source(e.getSource())
                .dataTimestamp(e.getDataTimestamp())
                .build();
    }
}
