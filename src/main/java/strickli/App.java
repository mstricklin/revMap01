package strickli;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import strickli.cache.Cache;
import strickli.cache.Transactional;

import static com.google.common.collect.Maps.newHashMap;

@Slf4j
public class App {
    public static void main( String[] args ) {
        log.info("Log msg");

        Cache<Foo> c = Cache.of(f0);
        c.create(17l, Foo.of(17l, "aaa 17"));
        c.create(18l, Foo.of(18l, "aaa 18"));
        Foo f = c.read(17l);
        c.dump();
        try (Transactional.TransactionCloser xit = c.prepare()) {
            log.info("");
            c.dump();
//            c.rollback();
        }
        log.info("");
        c.dump();

        P p = new P("ppp");
        p.doP();
        log.info("P: {}", p);

    }
    // =================================
    static Cache.MyCacheLoader<Foo> f0 = new Cache.MyCacheLoader<Foo>() {
        @Override
        public Foo load(Long key) throws Exception {
            log.info("making new Foo {}", key);
            return Foo.of(key, "aaa"+Long.toString(key));
        }
    };
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
