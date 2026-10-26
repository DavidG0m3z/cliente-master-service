package co.com.davidgomez.jpa.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByDocumentTypeAndDocumentNumber(String documentType, String documentNumber);

}
