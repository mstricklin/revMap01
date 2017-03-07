// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli;

import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.collect.Lists.newLinkedList;

@Slf4j
public class Cache<V> implements Transactional {

    public abstract static class MyCacheLoader<V> extends CacheLoader<Long, V> {
        public abstract V load(Long key) throws Exception;
    }
    // =================================
    public static <V> Cache<V> of(MyCacheLoader<V> f) {
        return new Cache(f);
    }
    // =================================
    private Cache(CacheLoader f) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(1000)
                .build(f);
    }
    // =================================
    // CRUD
    void create(Long key, V value) {
        begin();
        log.info("put {} {}", key, value);
        revision.get().put(key, value);
        queueActions(Create.of(this, key, value));
        // throw load exception? Perhaps if key already exists...
    }
    // =================================
    V read(Long key) {
        try {
            V v = revision.get().get(key);
            queueActions(Read.of(this, key));
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
    V update(Long key) {
        begin();
        // throw no-such...?
        queueActions(Update.of(this, key));
        return null;
    }
    // =================================
    V delete(Long key) {
        // throw no-such? Consider locking row...
        return null;
    }
    // =================================
    boolean containsKey(Long key) {
        begin();
//        if
        return false;
    }
    // =================================
    public void dump() {
        log.info("========= Main Cache ===========");
        for (Map.Entry<Long, V> e : cache.asMap().entrySet()) {
            log.info("{} => {}", e.getKey(), e.getValue());
        }
        log.info("========= Excursion Cache ===========");
        for (Map.Entry<Long, V> e : revision.get().asMap().entrySet()) {
            log.info("{} => {}", e.getKey(), e.getValue());
        }
        log.info("========= Pending actions ===========");
        for (Action a : actions.get()) {
            log.info("{}", a);
        }
    }
    // =================================
    public TransactionCloser prepare() {// add timeout? Return closable?

        cacheLock.lock();
        // 2nd excursion, or clear current?
        // test apply actions
        return new TransactionCloser() {
            @Override
            public void close() { // synchronize?
                log.info("AutoCloser");
                if (cacheLock.isLocked())
                    cacheLock.unlock();
                // ditch excursion?
                Cache.this.forget();
            }
        };
    }
    // =================================
    public void commit() {
        // copy revision cache to main-line cache
        cache.putAll(revision.get().asMap());
        Map<Long, V> nulls = Maps.filterValues(cache.asMap(), Predicates.<V>isNull());
        cache.invalidateAll(nulls.keySet());
        cacheLock.unlock();
        actions.get().clear();
        revision.get().invalidateAll();
    }
    // =================================
    public void rollback() {
        cacheLock.unlock();
        actions.get().clear();
        revision.get().invalidateAll();
    }
    // =================================
    public void forget() {
        log.info("forget...");
        cacheLock.unlock();
        actions.get().clear();
        revision.get().invalidateAll();
    }
    // =================================
    private void begin() {
        //actions.get().clear();
        // clear excursion...
    }
    // =================================
    private void queueActions(Action a) {
        actions.get().add(a);
    }
    // =================================

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

    private ThreadLocal<Queue<Action>> actions = new ThreadLocal<Queue<Action>>() {
        @Override
        protected Queue<Action> initialValue() {
            return newLinkedList();
        }
    };

    // =================================
    private interface Action {
        void apply();
    }

    // =================================
    @Data(staticConstructor = "of")
    private final static class Create<V> implements Action {
        @Override
        public void apply() {
            cache.create(key, value);
        }
        protected final Cache<V> cache;
        protected final Long key;
        protected final V value;
    }

    // =================================
    @Data(staticConstructor = "of")
    private final static class Read<V> implements Action {
        @Override
        public void apply() {
            cache.update(key);
        }
        protected final Cache<V> cache;
        protected final Long key;
    }

    // =================================
    @Data(staticConstructor = "of")
    private final static class Update<V> implements Action {
        @Override
        public void apply() {
            cache.update(key);
        }
        protected final Cache<V> cache;
        private final Long key;
    }

    // =================================
    @Data(staticConstructor = "of")
    private final static class Delete<V> implements Action {
        @Override
        public void apply() {
            cache.delete(key);
        }
        protected final Cache<V> cache;
        private final Long key;
    }

    // =================================
    @Data(staticConstructor = "of")
    private final static class Contains<V> implements Action {
        @Override
        public void apply() {
            cache.containsKey(key);
        }
        protected final Cache<V> cache;
        private final Long key;
    }
}
