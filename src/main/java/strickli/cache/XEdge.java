// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
@ToString(callSuper=true)
public class XEdge extends XElement implements Edge {
    public static XEdge of(long id, String label) {
        return new XEdge(id, label);
    }
    // =================================
    private XEdge(long id, String label_) {
        super(id);
        this.label = label_;
    }
    // =================================
    @Override
    public XElement copy() {
        return XEdge.of(id, label);
    }


    @Override
    public Vertex getVertex(Direction direction) throws IllegalArgumentException {
        return null;
    }
    @Override
    public String getLabel() {
        return label;
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
    // =================================
    private String label;
}
