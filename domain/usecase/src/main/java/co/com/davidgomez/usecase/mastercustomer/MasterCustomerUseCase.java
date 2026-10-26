package co.com.davidgomez.usecase.mastercustomer;

import co.com.davidgomez.model.customer.Customer;
import co.com.davidgomez.model.customer.IdentityDocument;
import co.com.davidgomez.model.customer.RawCustomerRecord;
import co.com.davidgomez.model.customer.RecordStatus;
import co.com.davidgomez.model.customer.gateways.CustomerRepository;
import co.com.davidgomez.model.customer.gateways.RawCustomerRecordRepository;
import co.com.davidgomez.model.customer.gateways.TransactionGateway;
import lombok.RequiredArgsConstructor;

import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
public class MasterCustomerUseCase {

    private final CustomerRepository customerRepository;
    private final RawCustomerRecordRepository rawRecordRepository;
    private final TransactionGateway transactionGateway;

    public Optional<RawCustomerRecord> receive(RawCustomerRecord incoming) {
        String hash = calculateHash(incoming);

        if (rawRecordRepository.existsByHash(hash)) {
            return Optional.empty();
        }

        RawCustomerRecord received = incoming.toBuilder()
                .hash(hash)
                .status(RecordStatus.RECEIVED)
                .build();

        return Optional.of(rawRecordRepository.save(received));
    }

    public RawCustomerRecord process(RawCustomerRecord record) {

        IdentityDocument document;
        try {
            document = IdentityDocument.of(record.getRawDocumentType(), record.getRawDocumentNumber());
        } catch (IllegalArgumentException e) {
            return markAsError(record, e.getMessage());
        }

        RawCustomerRecord normalized = rawRecordRepository.save(record.toBuilder()
                .status(RecordStatus.NORMALIZED)
                .errorMessage(null)
                .build());

        try {
            return transactionGateway.executeTransaction(() -> {
                Customer incoming = toCustomer(normalized, document);
                Customer golden = customerRepository.findByDocument(document)
                        .map(existing -> merge(existing, incoming))
                        .orElse(incoming);
                customerRepository.save(golden);
                return rawRecordRepository.save(normalized.toBuilder()
                        .status(RecordStatus.NORMALIZED)
                        .build());
            });
        } catch (RuntimeException e) {
            return markAsError(normalized, "Error tecnico: " + e.getMessage());
        }

    }

    public List<RawCustomerRecord> processBatch(List<RawCustomerRecord> records) {
        return records.stream()
                .map(this::receive)
                .flatMap(Optional::stream)
                .map(this::process)
                .toList();
    }

    public List<RawCustomerRecord> reprocessErrors() {
        return rawRecordRepository.findByStatus(RecordStatus.ERROR).stream()
                .map(this::process)
                .toList();
    }


    private Customer toCustomer(RawCustomerRecord record, IdentityDocument document) {
        return Customer.builder()
                .document(document)
                .name(record.getName())
                .email(record.getEmail())
                .phone(record.getPhone())
                .source(record.getSource())
                .dataTimestamp(record.getDataTimestamp())
                .build();
    }

    private Customer merge(Customer existing, Customer incoming) {
        int incomingPriority = incoming.getSource().getPriority();
        int existingPriority = existing.getSource().getPriority();

        boolean incomingWins = incomingPriority < existingPriority
                || (incomingPriority == existingPriority
                && isNewer(incoming.getDataTimestamp(), existing.getDataTimestamp()));

        Customer winner = incomingWins ? incoming : existing;
        Customer loser = incomingWins ? existing : incoming;

        return winner.toBuilder()
                .id(existing.getId())
                .name(firstNonBlank(winner.getName(), loser.getName()))
                .email(firstNonBlank(winner.getEmail(), loser.getEmail()))
                .phone(firstNonBlank(winner.getPhone(), loser.getPhone()))
                .build();
    }

    private static boolean isNewer(LocalDateTime candidate, LocalDateTime current) {
        return candidate != null && (current == null || candidate.isAfter(current));
    }

    private static String firstNonBlank(String preferred, String fallback) {
        return (preferred != null && !preferred.isBlank()) ? preferred : fallback;
    }


    private static String calculateHash(RawCustomerRecord r) {
        String content = String.join("|",
                String.valueOf(r.getSource()),
                String.valueOf(r.getRawDocumentType()),
                String.valueOf(r.getRawDocumentNumber()),
                String.valueOf(r.getName()),
                String.valueOf(r.getEmail()),
                String.valueOf(r.getPhone()),
                String.valueOf(r.getDataTimestamp()));
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(content.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }

    public List<RawCustomerRecord> reprocessStuck(Duration maxAge) {
        LocalDateTime threshold = LocalDateTime.now().minus(maxAge);
        return rawRecordRepository.findStuck(RecordStatus.NORMALIZED, threshold).stream()
                .map(this::process)
                .toList();
    }

    private RawCustomerRecord markAsError(RawCustomerRecord record, String message) {
        return rawRecordRepository.save(record.toBuilder()
                .status(RecordStatus.ERROR)
                .errorMessage(message)
                .build());
    }
}