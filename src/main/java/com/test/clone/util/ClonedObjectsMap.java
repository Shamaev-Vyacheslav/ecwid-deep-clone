package com.test.clone.util;

import java.util.HashMap;
import java.util.Optional;

public class ClonedObjectsMap<K, V> extends HashMap<K, V> {
    @Override
    public V put(K key, V value) {
        Optional<?> actualKey = keySet().stream().filter(k -> k == key).findFirst();
        if (actualKey.isPresent()) {
            return super.get(actualKey.get());
        } else {
            return super.put(key, value);
        }
    }

    @Override
    public V get(Object key) {
        Optional<?> actualKey = keySet().stream().filter(k -> k == key).findFirst();
        if (actualKey.isPresent()) {
            return super.get(actualKey.get());
        } else {
            return null;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return keySet().stream().anyMatch(k -> k == key);
    }
}
