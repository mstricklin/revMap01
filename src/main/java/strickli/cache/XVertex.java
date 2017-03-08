// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import lombok.ToString;

import java.util.Set;

@ToString(callSuper=true)
public class XVertex extends XElement implements Vertex {
    public static XVertex of(long id) {
        return new XVertex(id);
    }
    private XVertex(long id) {
        super(id);
    }

    @Override
    public XElement copy() {
        return XVertex.of(id);
    }
    @Override
    public Iterable<Edge> getEdges(Direction direction, String... labels) {
        return null;
    }
    @Override
    public Iterable<Vertex> getVertices(Direction direction, String... labels) {
        return null;
    }
    @Override
    public VertexQuery query() {
        return null;
    }
    @Override
    public Edge addEdge(String label, Vertex inVertex) {
        return null;
    }
    @Override
    public <T> T getProperty(String key) {
        return null;
    }
    @Override
    public Set<String> getPropertyKeys() {
        return null;
    }
    @Override
    public void setProperty(String key, Object value) {

    }
    @Override
    public <T> T removeProperty(String key) {
        return null;
    }
    @Override
    public void remove() {

    }
    @Override
    public Object getId() {
        return id;
    }
}
