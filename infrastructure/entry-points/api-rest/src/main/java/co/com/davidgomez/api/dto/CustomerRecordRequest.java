package co.com.davidgomez.api.dto;

import java.time.LocalDateTime;

public record CustomerRecordRequest(
        String source,
        String documentType,
        String documentNumber,
        String name,
        String email,
        String phone,
        LocalDateTime dataTimestamp
) {}
