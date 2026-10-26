package co.com.davidgomez.jpa.customer;

import co.com.davidgomez.model.customer.RawCustomerRecord;
import co.com.davidgomez.model.customer.RecordStatus;
import co.com.davidgomez.model.customer.gateways.RawCustomerRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class RawCustomerRecordRepositoryAdapter implements RawCustomerRecordRepository {

    private final RawCustomerRecordJpaRepository jpaRepository;


    @Override
    public RawCustomerRecord save(RawCustomerRecord record) {
        RawCustomerRecordEntity entity = record.getId() == null
                ? new RawCustomerRecordEntity()
                : jpaRepository.findById(record.getId()).orElseGet(RawCustomerRecordEntity::new);

        entity.setBatchId(record.getBatchId());
        entity.setSource(record.getSource());
        entity.setRawDocumentType(record.getRawDocumentType());
        entity.setRawDocumentNumber(record.getRawDocumentNumber());
        entity.setName(record.getName());
        entity.setEmail(record.getEmail());
        entity.setPhone(record.getPhone());
        entity.setDataTimestamp(record.getDataTimestamp());
        entity.setStatus(record.getStatus());
        entity.setHash(record.getHash());
        entity.setErrorMessage(record.getErrorMessage());

        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public boolean existsByHash(String hash) {
        return jpaRepository.existsByHash(hash);
    }

    @Override
    public List<RawCustomerRecord> findByStatus(RecordStatus status) {
        return jpaRepository.findByStatus(status).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<RawCustomerRecord> findStuck(RecordStatus status, LocalDateTime updateBefore) {
        return jpaRepository.findByStatusAndUpdatedAtBefore(status, updateBefore).stream()
                .map(this::toDomain)
                .toList();
    }

    private RawCustomerRecord toDomain(RawCustomerRecordEntity e) {
        return RawCustomerRecord.builder()
                .id(e.getId())
                .batchId(e.getBatchId())
                .source(e.getSource())
                .rawDocumentType(e.getRawDocumentType())
                .rawDocumentNumber(e.getRawDocumentNumber())
                .name(e.getName())
                .email(e.getEmail())
                .phone(e.getPhone())
                .dataTimestamp(e.getDataTimestamp())
                .status(e.getStatus())
                .hash(e.getHash())
                .errorMessage(e.getErrorMessage())
                .build();
    }
}
