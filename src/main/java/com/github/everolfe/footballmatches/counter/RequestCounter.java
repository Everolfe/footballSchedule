package com.github.everolfe.footballmatches.counter;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Component;

@Component
public class RequestCounter {
    private final ConcurrentHashMap<String, AtomicInteger> counterMap = new ConcurrentHashMap<>();

    public int incrementAndGet(String key) {
        return counterMap.computeIfAbsent(key, k -> new AtomicInteger(0))
                .incrementAndGet();
    }

    public int getCount(String key) {
        return counterMap.getOrDefault(key, new AtomicInteger(0)).get();
    }

    public ConcurrentHashMap<String, Integer> getAllCounts() {
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        counterMap.forEach((key, value) -> result.put(key, value.get()));
        return result;
    }

    public void resetCounter(String key) {
        counterMap.put(key, new AtomicInteger(0));
    }

    public void resetAll() {
        counterMap.clear();
    }
}