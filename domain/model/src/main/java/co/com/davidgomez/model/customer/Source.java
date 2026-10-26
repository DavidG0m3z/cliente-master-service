package co.com.davidgomez.model.customer;

public enum Source {

    APP(1),
    BRANCH(2);

    private final int priority;

    Source(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
