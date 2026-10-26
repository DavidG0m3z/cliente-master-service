package co.com.davidgomez.api.dto;

import co.com.davidgomez.model.customer.RecordStatus;

import java.util.Map;

public record BatchResponse(
        String batchId,
        int received,
        int duplicatesIgnored,
        Map<RecordStatus, Long> statusCounts
) {
}
