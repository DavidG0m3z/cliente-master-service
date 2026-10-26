package co.com.davidgomez.model.customer.gateways;

import co.com.davidgomez.model.customer.RawCustomerRecord;
import co.com.davidgomez.model.customer.RecordStatus;

import java.util.List;

public interface RawCustomerRecordRepository {

    RawCustomerRecord save(RawCustomerRecord record);

    boolean existsByHash(String hash);

    List<RawCustomerRecord> findByStatus(RecordStatus status);
}
