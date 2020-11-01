package ry.an.util;


import java.util.concurrent.atomic.AtomicInteger;

public class CountingIdGenerator implements IdGenerator {
    private AtomicInteger counter;

    public CountingIdGenerator(int initCounter) {
        counter = new AtomicInteger(initCounter);
    }

    @Override
    public String nextId() {
        return String.valueOf(counter.incrementAndGet());
    }
}
