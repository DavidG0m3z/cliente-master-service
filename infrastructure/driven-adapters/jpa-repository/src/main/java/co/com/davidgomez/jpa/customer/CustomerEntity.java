package co.com.davidgomez.jpa.customer;

import co.com.davidgomez.model.customer.Source;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer",
        uniqueConstraints = @UniqueConstraint(name = "uk_customer_document",
                columnNames = {"document_type", "document_number"}))
@Getter
@Setter
@NoArgsConstructor
public class CustomerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "document_type", nullable = false)
    private String documentType;

    @Column(name = "document_number", nullable = false)
    private String documentNumber;

    private String name;
    private String email;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Source source;

    private LocalDateTime dataTimestamp;

    @Version
    private Long version;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}

