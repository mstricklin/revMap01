// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;


@Slf4j
public class GraphCache implements Transactional {
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
//    public static <V> GraphCache<V> of(MyCacheLoader<V> f) {
//        return new GraphCache(f);
//    }
//    // =================================
//    private GraphCache(CacheLoader f) {
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
//        log.info("========= Main GraphCache ===========");
//        for (Map.Entry<Long, V> e : cache.asMap().entrySet()) {
//            log.info("{} => {}", e.getKey(), e.getValue());
//        }
//        log.info("========= Excursion GraphCache ===========");
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
//                GraphCache.this.forget();
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
    interface ElementDao<T> {
        void create(T t);
        T read(long id);
        void update(T t);
        void delete(long id);
        Iterable<T> list();
        Iterable<T> list(String key, Object value);
        void clear();
    }
    // =================================
    public class CacheDao<T extends Keyed> implements ElementDao<T> {
        CacheDao(GraphCache store_, RevMap<T> cache_) {
            store = store_; cache = cache_;
        }
        @Override
        public void create(T t) {
            cache.create(t);
            // TODO: queue action
        }
        @Override
        public T read(long id) {
            log.info("CacheDao read {}", id);
            return cache.read(id);
        }
        @Override
        public void update(T t) {
            cache.update(t.getKey());
            // TODO: queue action
        }
        @Override
        public void delete(long id) {
            cache.delete(id);
            // TODO: queue action
        }
        @Override
        public Iterable<T> list() {
            return cache.list();
        }
        @Override
        public Iterable<T> list(String key, Object value) {
            // TODO: implement
            return Collections.emptyList();
        }
        @Override
        public void clear() {
            cache.clear();
            // TODO: queue action
        }
        // =================================
        GraphCache store;
        RevMap<T> cache;
    }
    // =================================
    public void dump() {
        log.info("========= Vertex GraphCache ===========");
        vertexCache.dump();
        log.info("========= Edge GraphCache =============");
        edgeCache.dump();
        log.info("==================================");
    }
    // =================================
    RevMap<XVertex> vertexCache = RevMap.of(vertexLoader);
    RevMap<XVertex> vertexPropertyCache;
    RevMap<XEdge> edgeCache = RevMap.of(edgeLoader);
    RevMap<XEdge> edgePropertyCache;

    static RevMap.Loader<XVertex> vertexLoader = new RevMap.Loader<XVertex>() {
        @Override
        public XVertex load(Long key) throws Exception {
            log.info("making new Vertex {}", key);
            return XVertex.of(key);
        }
    };
    static RevMap.Loader<XEdge> edgeLoader = new RevMap.Loader<XEdge>() {
        @Override
        public XEdge load(Long key) throws Exception {
            log.info("making new Edge {}", key);
            return XEdge.of(key, "edge-"+key);
        }
    };

    public final CacheDao<XVertex> vertexDao = new CacheDao<>(this, vertexCache);
    public final CacheDao<XEdge>   edgeDao   = new CacheDao<>(this, edgeCache);

}
