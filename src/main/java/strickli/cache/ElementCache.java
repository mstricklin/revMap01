// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;

@Slf4j
public class ElementCache<T extends Keyed> {
    public abstract static class Loader<V> extends CacheLoader<Long, V> {
        public abstract V load(Long key) throws Exception;
    }
    /*
    R.ead
    list
    size
    dump
    merge(Map to overwrite, Set to delete)

     */
    // =================================
    public static <T extends Keyed> ElementCache<T> of(ElementCache.Loader<T> f) {
        return new ElementCache(f);
    }
    // =================================
    private ElementCache(CacheLoader f) {
        store = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(f);
    }
    // =================================
    public T get(Long key) { // Optional?
        try {
            return store.get(key);
        } catch (ExecutionException e) {
            log.error("Error loading element from SoR", e);
        }
        return null;
    }
    // =================================
    public Iterable<T> list() {
        // this only covers what's actually in the cache
        return store.asMap().values();
    }
    // =================================
    public long size() {
        return store.size();
    }
    // =================================
    public void dump() {
        for (Map.Entry<Long, T> e : store.asMap().entrySet()) {
            log.info("{} => {}", e.getKey(), e.getValue());
        }
    }
    // =================================
    void merge(Map<Long, T> revisions, Set<Long> removed) {
        store.putAll(revisions);
        store.invalidateAll(removed);
    }
    // =================================
    private final LoadingCache<Long, T> store;
    private final ReentrantLock storeLock = new ReentrantLock();
}
