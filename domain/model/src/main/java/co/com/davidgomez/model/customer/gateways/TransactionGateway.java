package co.com.davidgomez.model.customer.gateways;

import java.util.function.Supplier;

public interface TransactionGateway {

    <T> T executeTransaction(Supplier<T> action);
}
