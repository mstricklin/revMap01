// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import com.google.common.base.Function;

public interface Keyed {
    long getKey();
    final Function<Keyed, Long> extractKey = new Function<Keyed, Long>() {
        @Override
        public Long apply(Keyed v) {
            return v.getKey();
        }
    };
}
