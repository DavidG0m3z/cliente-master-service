package co.com.davidgomez.jpa.customer;

import co.com.davidgomez.model.customer.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RawCustomerRecordJpaRepository extends JpaRepository<RawCustomerRecordEntity, Long> {

    boolean existsByHash(String hash);
    List<RawCustomerRecordEntity> findByStatus(RecordStatus status);
    List<RawCustomerRecordEntity> findByStatusAndUpdatedAtBefore(RecordStatus status, LocalDateTime before);

}
