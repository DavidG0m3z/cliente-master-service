package co.com.davidgomez.model.customer;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
public class Customer {

    private final Long id;
    private final IdentityDocument document;

    private final String name;
    private final String email;
    private final String phone;

    private final Source source;
    private final LocalDateTime dateTimesTamp;
}
