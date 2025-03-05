package com.github.everolfe.footballmatches.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;


@Component
public class Cache<K, V> extends LinkedHashMap<K, V> {

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        final int maxSize = 5;
        return size() > maxSize;
    }
}
