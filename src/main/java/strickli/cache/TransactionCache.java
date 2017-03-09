// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Predicates.in;
import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Maps.newHashMap;
import static com.google.common.collect.Sets.newHashSet;

@Slf4j
public class TransactionCache<T extends Keyed> {
    // =================================
    // TODO: parameterize max size?
    public static <T extends Keyed> TransactionCache<T> of(ElementCache<T> baseline) {
        return new TransactionCache(baseline);
    }
    // =================================
    private TransactionCache(ElementCache<T> baseline_) {
        baseline = baseline_;
    }
    // =================================
    /*
    C.reate
    R.ead (get)
    U.pdate
    D.elete
    list
    clear
    size
    dump
     */
    // =================================
    public void create(T value) {
        // check size?
        log.info("put {} {}", value.getKey(), value);
        revision.put(value.getKey(), value);
    }
    // =================================
    public T get(Long key) { // Optional?
        if (removed.contains(key))
            return null;
        T t = revision.get(key);
        return (null == t) ? baseline.get(key) : null;
    }
    // =================================
    public T update(Long key) {
        if (removed.contains(key))
            return null;
        T t = revision.get(key);
        if (null != t) return t;
        t = baseline.get(key);
        if (t != null) {
            // TODO: copy?
            revision.put(key, t);
        }
        return t;
    }
    // =================================
    public T delete(Long key) {
        log.info("remove {}", key);
        T t = revision.remove(key);
        removed.add(key);
        return t;
    }
    // =================================
    public Iterable<T> list() {
        log.info("list");
        Map<Long, T> revMap = Maps.filterKeys(revision, not(in(removed)));
        return revMap.values();
    }
    // =================================
    // TODO: a clear() operation? Move everything to removed set?
    public void reset() {
        log.info("reset");
        revision.clear();
        removed.clear();
    }
    // =================================
    // Only covers values in this transaction. Could be a negative number, if there's more removed
    // than revised.
    public int size() {
        return revision.size() - removed.size();
    }
    // =================================
    public void dump() {
        log.info("          Revision");
        for (Map.Entry<Long, T> e : revision.entrySet()) {
            log.info("{} => {}", e.getKey(), e.getValue());
        }
        log.info("          Removals");
        for (Long k : removed) {
            log.info("{}", k);
        }
    }
    // =================================
    public void merge() {
        baseline.merge(revision, removed);
        revision.clear();
        removed.clear();
    }
    // =================================

    private final Map<Long, T> revision = newHashMap();
    private Set<Long> removed = newHashSet();
    private final ElementCache<T> baseline;
}
