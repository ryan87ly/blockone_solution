package ry.an.util;

import java.util.concurrent.atomic.AtomicInteger;

public class PrefixedCounterIdGenerator implements IdGenerator {
    private final String prefix;
    private final AtomicInteger counter;

    private PrefixedCounterIdGenerator(String prefix) {
        this.prefix = prefix;
        this.counter = new AtomicInteger(0);
    }

    public static PrefixedCounterIdGenerator of(String prefix) {
        return new PrefixedCounterIdGenerator(prefix);
    }

    @Override
    public String nextId() {
        int nextCount = counter.incrementAndGet();
        return String.format("%s-%d", prefix, nextCount);
    }
}
