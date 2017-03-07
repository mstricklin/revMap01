// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class RevMap<V extends Element.Keyed> {
    public abstract static class Loader<V> extends CacheLoader<Long, V> {
        public abstract V load(Long key) throws Exception;
    }
    // =================================
    public static <V extends Element.Keyed> RevMap<V> of(Loader<V> f) {
        return new RevMap(f);
    }
    // =================================
    private RevMap(CacheLoader f) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(f);
    }
    // =================================
    // CRUD
    public void add(V value) {
        log.info("put {} {}", value.getId(), value);
        revision.get().put(value.getId(), value);
        // throw load exception? Perhaps if key already exists...
    }
    // =================================
    public V read(Long key) { // Optional?
        try {
            V v = revision.get().get(key);
            return v;
        } catch (ExecutionException e) {
            e.printStackTrace();
            log.error("Error msg here...should it be in the generator?");
            log.error("Msg", e);
            // throw no-such...?
        }
        return null;
    }
    // =================================
    public V update(Long key) {
        // throw no-such...?
        return null;
    }
    // =================================
    public V delete(Long key) {
        // throw no-such? Consider locking row...
        return null;
    }
    // =================================
    public boolean containsKey(Long key) {
        return false;
    }
    final LoadingCache<Long, V> cache;
    private final ReentrantLock cacheLock = new ReentrantLock();
    private ThreadLocal<LoadingCache<Long, V>> revision = new ThreadLocal() {
        @Override
        protected LoadingCache<Long, V> initialValue() {
            return CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .build(new CacheLoader<Long, V>() {
                        public V load(Long key) throws ExecutionException {
                            return cache.get(key);// copy?
                        }
                    });
        }
    };


}
