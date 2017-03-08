// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli;

import com.google.common.cache.CacheLoader;
import com.tinkerpop.blueprints.*;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import lombok.extern.slf4j.Slf4j;
import strickli.cache.CovariantIterable;
import strickli.cache.GraphCache;
import strickli.cache.XEdge;
import strickli.cache.XVertex;

import java.util.concurrent.ExecutionException;

import static com.google.common.base.Preconditions.checkNotNull;

@Slf4j
public class GraphDB implements Graph {
    private static long VERTEX_ID = 1;
    private static long EDGE_ID = 1;
    // =================================
    static GraphDB of() {
        return new GraphDB();
    }
    GraphDB() {
        graphCache = new GraphCache();
    }
    // =================================
    public Features getFeatures() {
        return null;
    }
    // =================================
    public Vertex addVertex(Object id) {
        // TODO: get a unique ID, ignoring requested one
        XVertex v = XVertex.of(VERTEX_ID++);
        graphCache.vertexDao.create(v);
        return v;
    }
    // =================================
    public Vertex getVertex(Object id) {
        log.trace("getVertex {}", id);
        if (null == id)
            throw ExceptionFactory.vertexIdCanNotBeNull();
        try {
            final Long longID = (id instanceof Long) ? (Long)id : Long.valueOf(id.toString());
            return graphCache.vertexDao.read(longID);
        } catch (NumberFormatException | ClassCastException e) {
            log.error("could not find vertex id {}", id);
        }
        return null;
    }
    // =================================
    public void removeVertex(Vertex vertex) {
        checkNotNull(vertex);
//        for (Edge e : vertex.getEdges(Direction.BOTH))
//            removeEdge(e);
        Long longId = (Long) vertex.getId();
        graphCache.vertexDao.delete(longId);
    }
    // =================================
    public Iterable<Vertex> getVertices() {
        return new CovariantIterable<Vertex>(graphCache.vertexDao.list());
    }
    // =================================
    public Iterable<Vertex> getVertices(String key, Object value) {
        return null;
    }
    public Edge addEdge(Object id, Vertex outVertex, Vertex inVertex, String label) {
        // TODO: get a unique ID, ignoring requested one
        XEdge e = XEdge.of(EDGE_ID++, label);
        graphCache.edgeDao.create(e);
        return e;
    }
    public Edge getEdge(Object id) {
        return null;
    }
    public void removeEdge(Edge edge) {

    }
    public Iterable<Edge> getEdges() {
        return null;
    }
    public Iterable<Edge> getEdges(String key, Object value) {
        return null;
    }
    public GraphQuery query() {
        return null;
    }
    public void shutdown() {

    }
    public void dump() {
        graphCache.dump();
    }

    private final GraphCache graphCache;
}
