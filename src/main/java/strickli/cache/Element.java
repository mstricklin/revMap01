// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

public interface Element {

    interface Keyed {
        long getId();
    }
    // =================================
    abstract class Elements implements Keyed {
        Elements(long id_) {
            id = id_;
        }
        public long getId() { return id; }
        long id;
    }
    // =================================
    class XVertex extends Elements {
        XVertex(long id) {
            super(id);
        }
    }
    // =================================
    class XEdge extends Elements {
        XEdge(long id) {
            super(id);
        }
        public String label;
    }
}
