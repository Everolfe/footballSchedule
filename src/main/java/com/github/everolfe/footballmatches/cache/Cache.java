package com.github.everolfe.footballmatches.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
public class Cache<K, V> extends LinkedHashMap<K, V> {
    private static final Logger logger = LoggerFactory.getLogger(Cache.class);
    private static final int MAX_SIZE = 5;

    @Override
    public V put(K key, V value) {
        logger.info("Add to cache: Key = {}, Value = {}", key, value);
        return super.put(key, value);
    }

    @Override
    public V get(Object key) {
        V value = super.get(key);
        if (value != null) {
            logger.info("Get from cache: Key = {}, Value = {}", key, value);
        } else {
            logger.info("Key not found in cache: Key = {}", key);
        }
        return value;
    }

    @Override
    protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
        boolean shouldRemove = size() > MAX_SIZE;
        if (shouldRemove) {
            logger.info("Delete eldest entry: Key = {}, Value = {}",
                    eldest.getKey(), eldest.getValue());
        }
        return shouldRemove;
    }
}