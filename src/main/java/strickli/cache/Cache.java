// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import com.google.common.base.Predicates;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.locks.ReentrantLock;

import static com.google.common.collect.Lists.newLinkedList;

@Slf4j
public class Cache implements Transactional {
    @Override
    public TransactionCloser prepare() {
        return null;
    }
    @Override
    public void commit() {

    }
    @Override
    public void rollback() {

    }
    @Override
    public void forget() {

    }

//    public abstract static class MyCacheLoader<V> extends CacheLoader<Long, V> {
//        public abstract V load(Long key) throws Exception;
//    }
//    // =================================
//    public static <V> Cache<V> of(MyCacheLoader<V> f) {
//        return new Cache(f);
//    }
//    // =================================
//    private Cache(CacheLoader f) {
//        cache = CacheBuilder.newBuilder()
//                .maximumSize(1000)
//                .build(f);
//    }
//    // =================================
//    // CRUD
//    public void create(Long key, V value) {
//        begin();
//        log.info("put {} {}", key, value);
//        revision.get().put(key, value);
////        queueActions(Create.of(this, key, value));
//        // throw load exception? Perhaps if key already exists...
//    }
//    // =================================
//    public V read(Long key) {
//        try {
//            V v = revision.get().get(key);
////            queueActions(Read.of(this, key));
//            return v;
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//            log.error("Error msg here...should it be in the generator?");
//            log.error("Msg", e);
//            // throw no-such...?
//        }
//        return null;
//    }
//    // =================================
//    public V update(Long key) {
//        begin();
//        // throw no-such...?
////        queueActions(Update.of(this, key));
//        return null;
//    }
//    // =================================
//    public V delete(Long key) {
//        // throw no-such? Consider locking row...
//        return null;
//    }
//    // =================================
//    public boolean containsKey(Long key) {
//        begin();
////        if
//        return false;
//    }
//    // =================================
//    public void dump() {
//        log.info("========= Main Cache ===========");
//        for (Map.Entry<Long, V> e : cache.asMap().entrySet()) {
//            log.info("{} => {}", e.getKey(), e.getValue());
//        }
//        log.info("========= Excursion Cache ===========");
//        for (Map.Entry<Long, V> e : revision.get().asMap().entrySet()) {
//            log.info("{} => {}", e.getKey(), e.getValue());
//        }
//        log.info("========= Pending actions ===========");
////        for (Action a : actions.get()) {
////            log.info("{}", a);
////        }
//    }
//    // =================================
//    public TransactionCloser prepare() {// add timeout? Return closable?
//
//        cacheLock.lock();
//        // 2nd excursion, or clear current?
//        // test apply actions
//        return new TransactionCloser() {
//            @Override
//            public void close() { // synchronize?
//                log.info("AutoCloser");
//                Cache.this.forget();
//            }
//        };
//    }
//    // =================================
//    public void commit() {
//        // copy revision cache to main-line cache
//        cache.putAll(revision.get().asMap());
//        Map<Long, V> nulls = Maps.filterValues(cache.asMap(), Predicates.<V>isNull());
//        cache.invalidateAll(nulls.keySet());
//        if (cacheLock.isLocked())
//            cacheLock.unlock();
////        actions.get().clear();
//        revision.get().invalidateAll();
//    }
//    // =================================
//    public void rollback() {
//        if (cacheLock.isLocked())
//            cacheLock.unlock();
////        actions.get().clear();
//        revision.get().invalidateAll();
//    }
//    // =================================
//    public void forget() {
//        if (cacheLock.isLocked())
//            cacheLock.unlock();
////        actions.get().clear();
//        revision.get().invalidateAll();
//    }
//    // =================================
//    private void begin() {
//        //actions.get().clear();
//        // clear excursion...
//    }
//    // =================================
//
//    final LoadingCache<Long, V> cache;
//    private final ReentrantLock cacheLock = new ReentrantLock();
//    private ThreadLocal<LoadingCache<Long, V>> revision = new ThreadLocal() {
//        @Override
//        protected LoadingCache<Long, V> initialValue() {
//            return CacheBuilder.newBuilder()
//                    .maximumSize(1000)
//                    .build(new CacheLoader<Long, V>() {
//                        public V load(Long key) throws ExecutionException {
//                            return cache.get(key);// copy?
//                        }
//                    });
//        }
//    };

    // =================================
    interface Dao<T> {
        void add(T t);
        Element.XVertex get(long id);
        void remove(long id);
        Iterable<T> list();
        Iterable<T> list(String key, Object value);
        void clear();
    }
    // =================================
    class CacheDao<T extends Element.Keyed> implements Dao<T> {
        CacheDao(RevMap<T> cache_) {
            cache = cache_;
        }
        @Override
        public void add(T v) {

        }
        @Override
        public Element.XVertex get(long id) {
            return null;
        }
        @Override
        public void remove(long id) {

        }
        @Override
        public Iterable<T> list() {
            return Collections.emptyList();
        }
        @Override
        public Iterable<T> list(String key, Object value) {
            return Collections.emptyList();
        }
        @Override
        public void clear() {

        }
        RevMap<T> cache;

    }
    // =================================
    CacheDao<Element.XVertex> getV() {
        return new CacheDao<>(vertexCache);
    }
    // =================================
    public void addVertex(Element.XVertex v) {
        vertexCache.add(v);
        // queue action, if success
    }
    public Element.XVertex getVertex(long id) {
        Element.XVertex v = vertexCache.read(id);
        // queue action...? Not necessary on read
        return v;
    }
    public void remove(long id) {
        vertexCache.delete(id);
        // queue action
    }


    //    RdbmsVertex add();
//    RdbmsVertex get(long id);
//    void remove(long id);
//    Iterable<RdbmsVertex> list();
//    Iterable<RdbmsVertex> list(String key, Object value);
//    void clear();
    // =================================
    RevMap<Element.XVertex> vertexCache;
    RevMap<Element.XVertex> vertexPropertyCache;
    RevMap<Element.XEdge> edgeCache;
    RevMap<Element.XEdge> edgePropertyCache;

}
