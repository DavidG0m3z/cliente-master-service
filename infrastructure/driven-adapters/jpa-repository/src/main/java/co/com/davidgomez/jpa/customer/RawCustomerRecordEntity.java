package co.com.davidgomez.jpa.customer;

import co.com.davidgomez.model.customer.RecordStatus;
import co.com.davidgomez.model.customer.Source;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "raw_customer_record",
        indexes = @Index(name = "idx_raw_status_updated", columnList = "status, updated_at"))
@Getter
@Setter
@NoArgsConstructor
public class RawCustomerRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String batchId;

    @Enumerated(EnumType.STRING)
    private Source source;

    private String rawDocumentType;
    private String rawDocumentNumber;
    private String name;
    private String email;
    private String phone;
    private LocalDateTime dataTimestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecordStatus status;

    @Column(unique = true, nullable = false, length = 64)
    private String hash;

    @Column(length = 1000)
    private String errorMessage;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
