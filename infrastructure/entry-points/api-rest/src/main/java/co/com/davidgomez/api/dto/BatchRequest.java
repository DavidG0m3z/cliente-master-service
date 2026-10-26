package co.com.davidgomez.api.dto;

import java.util.List;

public record BatchRequest(String batchId, List<CustomerRecordRequest> records) {
}
