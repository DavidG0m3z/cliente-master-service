package co.com.davidgomez.jpa.transaction;

import co.com.davidgomez.model.customer.gateways.TransactionGateway;
import org.springframework.stereotype.Component;

import co.com.davidgomez.model.customer.gateways.TransactionGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import java.util.function.Supplier;

@Component
public class SpringTransactionGateway implements TransactionGateway {

    private final TransactionTemplate transactionTemplate;

    public SpringTransactionGateway(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Override
    public <T> T executeTransaction(Supplier<T> action) {
        return transactionTemplate.execute(status -> action.get());
    }
}
