package strickli;

import com.tinkerpop.blueprints.Vertex;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class App {
    public static void main( String[] args ) {
        log.info("Log msg");

        GraphDB g = GraphDB.of();
        g.addVertex(null);
        Vertex v1 = g.addVertex(null);
        g.addVertex(null);
        g.dump();
        log.info("");
        g.removeVertex(v1);
        g.dump();

//        Vertex v1a = g.getVertex(1L);
//        log.info("v1: {}", v1a);
//        g.dump();
//        for (Vertex v: g.getVertices())
//            log.info("vertex {}", v);

        g.addEdge(null, null, null, "sam");
        g.dump();

    }
    // =================================
//    static GraphCache.MyCacheLoader<Foo> f0 = new GraphCache.MyCacheLoader<Foo>() {
//        @Override
//        public Foo load(Long key) throws Exception {
//            log.info("making new Foo {}", key);
//            return Foo.of(key, "aaa"+Long.toString(key));
//        }
//    };
    // =================================
    @Data(staticConstructor="of")
    private static final class Foo {
        private final long i;
        private final String s;
    }
    // =================================
    @ToString
    static class P {
        P(String p_) {
            p = p_;
            c = new C(p_);
        }
        String p;
        C c;
        void doP() {
            log.info("P.p {}", p);
        }

        class C {
            C(String c_) { c = c_; }
            String c;
            void doC() {
                log.info("P.p {} C.c {}", p, c);
            }
        }
    }

}
