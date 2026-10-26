package co.com.davidgomez.config;

import co.com.davidgomez.model.customer.gateways.CustomerRepository;
import co.com.davidgomez.model.customer.gateways.RawCustomerRecordRepository;
import co.com.davidgomez.model.customer.gateways.TransactionGateway;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        // Levanta un Spring mínimo: la config de casos de uso + los mocks de abajo.
        try (var context = new AnnotationConfigApplicationContext(TestConfig.class)) {
            boolean useCaseFound = Arrays.stream(context.getBeanDefinitionNames())
                    .anyMatch(name -> name.endsWith("UseCase"));
            assertTrue(useCaseFound, "No beans ending with 'UseCase' were found");
        }
    }

    @Configuration
    @Import(UseCasesConfig.class)
    static class TestConfig {
        // mock() crea una implementación FALSA de la interfaz: no hace nada,
        // pero le permite a Spring construir el caso de uso para verificar que existe.
        @Bean
        CustomerRepository customerRepository() {
            return mock(CustomerRepository.class);
        }

        @Bean
        RawCustomerRecordRepository rawCustomerRecordRepository() {
            return mock(RawCustomerRecordRepository.class);
        }

        @Bean
        TransactionGateway transactionGateway() {
            return mock(TransactionGateway.class);
        }
    }
}