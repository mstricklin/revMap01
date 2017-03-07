package strickli;

import com.google.common.base.Function;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.util.Map;

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

}
