package co.com.davidgomez.model.customer;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class RawCustomerRecord {

    private final Long id;

    private final String batchId;

    private final Source source;
    private final String rawDocumentType;
    private final String rawDocumentNumber;

    private final String name;
    private final String email;
    private final String phone;
    private final LocalDateTime dataTimesTamp;

    private final RecordStatus status;

    private final String hash;

    private final String error;

}
