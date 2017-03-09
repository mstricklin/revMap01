// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import com.google.common.base.Optional;
import com.google.common.base.Predicates;
import com.google.common.cache.*;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class RevMap<T extends Keyed> {
    public abstract static class Loader<V> extends CacheLoader<Long, V> {
        public abstract V load(Long key) throws Exception;
    }
    // =================================
    public static <V extends Keyed> RevMap<V> of(Loader<V> f) {
        return new RevMap(f);
    }
    // =================================
    private RevMap(CacheLoader f) {
        baseline = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(f);
    }
    /*
    C.reate
    R.ead
    U.pdate
    D.elete
    list
    clear
    size
    dump
     */
    // =================================
    public void create(T value) {
        log.info("put {} {}", value.getKey(), value);
        revision.get().put(value.getKey(), value);
        // throw load exception? Perhaps if key already exists...
    }
    // =================================
    public T read(Long key) { // Optional?
        if (removed.get().contains(key))
            return null;
        T t = revision.get().getIfPresent(key);
        if (null == t) {
            try {
                t = baseline.get(key);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        return t;
    }
    // =================================
    public T update(Long key) {
        // TODO: throw no-such...? Copy before moving?
        return null;
    }
    // =================================
    public T delete(Long key) {
        log.info("remove {}", key);
        revision.get().invalidate(key);
        removed.get().add(key);
        return null;
    }
    // =================================
    public boolean containsKey(Long key) {
        return false;
    }
    // =================================
    public Iterable<T> list() {
        // this only covers what's actually in the cache
        Map<Long, T> baseMap = Maps.filterKeys(revision.get().asMap(), not(in(removed.get())));
        Map<Long, T> revMap = Maps.filterKeys(baseline.asMap(), not(in(removed.get())));
        return Iterables.concat(baseMap.values(), revMap.values());
    }
    // =================================
    public void clear() {
        removed.get().addAll( revision.get().asMap().keySet() );
        revision.get().invalidateAll();
        removed.get().addAll( baseline.asMap().keySet() );
    }
    // =================================
    public int size() {
        // this only covers what's actually in the cache
        Set<Long> keys = newHashSet( baseline.asMap().keySet() );
        keys.addAll( revision.get().asMap().keySet() );
        keys.removeAll( removed.get() );
        return keys.size();
    }
    // =================================
    public void dump() {
        log.info("          Main");
        for (Map.Entry<Long, T> e : baseline.asMap().entrySet()) {
            log.info("{} => {}", e.getKey(), e.getValue());
        }
        log.info("          Revision");
        for (Map.Entry<Long, T> e : revision.get().asMap().entrySet()) {
            log.info("{} => {}", e.getKey(), e.getValue());
        }
        log.info("          Removals");
        for (Long k : removed.get()) {
            log.info("{}", k);
        }
    }
    // =================================
    private final LoadingCache<Long, T> baseline;

    private final ReentrantLock storeLock = new ReentrantLock();

    // need an eviction listener for the revision, mark the transaction as overflowed if anything is evicted
    private ThreadLocal<LoadingCache<Long, T>> revision = new ThreadLocal() {
        @Override
        protected LoadingCache<Long, T> initialValue() {
            return CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .build(new CacheLoader<Long, T>() {
                        public T load(Long key) throws ExecutionException {
                            log.info("revision load from canonical");
                            return baseline.get(key);// copy?
                        }
                    });
        }
    };

    private ThreadLocal<Set<Long>> removed = new ThreadLocal() {
        @Override
        protected Set<Long> initialValue() {
            return newHashSet();
        }
    };
}
