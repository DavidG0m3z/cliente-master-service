package co.com.davidgomez.api;

import co.com.davidgomez.api.dto.BatchRequest;
import co.com.davidgomez.api.dto.BatchResponse;
import co.com.davidgomez.api.dto.CustomerRecordRequest;
import co.com.davidgomez.model.customer.RawCustomerRecord;
import co.com.davidgomez.model.customer.RecordStatus;
import co.com.davidgomez.model.customer.Source;
import co.com.davidgomez.usecase.mastercustomer.MasterCustomerUseCase;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
@RequiredArgsConstructor
public class ApiRest {

    private static final Logger log = LoggerFactory.getLogger(ApiRest.class);


    private final MasterCustomerUseCase masterCustomerUseCase;

    @PostMapping("/batches")
    public BatchResponse processBatch(@RequestBody BatchRequest request) {
        log.info("Processing batch {} with {} records", request.batchId(), request.records().size());

        List<RawCustomerRecord> records = request.records().stream()
                .map(r -> toDomain(r, request.batchId()))
                .toList();

        List<RawCustomerRecord> processed = masterCustomerUseCase.processBatch(records);

        BatchResponse response = new BatchResponse(
                request.batchId(),
                records.size(),
                records.size() - processed.size(),
                countByStatus(processed));

        log.info("Batch {} finished: {}", request.batchId(), response.statusCounts());
        return response;
    }

    @PostMapping("/records/reprocess-errors")
    public Map<RecordStatus, Long> reprocessErrors() {
        return countByStatus(masterCustomerUseCase.reprocessErrors());
    }


    @PostMapping("/records/reprocess-stuck")
    public Map<RecordStatus, Long> reprocessStuck(@RequestParam(defaultValue = "30") long minutes) {
        return countByStatus(masterCustomerUseCase.reprocessStuck(Duration.ofMinutes(minutes)));
    }

    private RawCustomerRecord toDomain(CustomerRecordRequest r, String batchId) {
        return RawCustomerRecord.builder()
                .batchId(batchId)
                .source(Source.valueOf(r.source()))
                .rawDocumentType(r.documentType())
                .rawDocumentNumber(r.documentNumber())
                .name(r.name())
                .email(r.email())
                .phone(r.phone())
                .dataTimestamp(r.dataTimestamp())
                .build();
    }

    private Map<RecordStatus, Long> countByStatus(List<RawCustomerRecord> records) {
        return records.stream()
                .collect(Collectors.groupingBy(RawCustomerRecord::getStatus, Collectors.counting()));
    }
}