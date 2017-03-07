// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import lombok.Data;
import lombok.EqualsAndHashCode;

public interface Transactional {
    TransactionCloser prepare();
    void commit();
    void rollback();
    void forget();

    @Data(staticConstructor="of")
    @EqualsAndHashCode(callSuper=false)
    class TransactionException extends Exception {
        TransactionException(String msg) {
            super(msg);
        }
    }

    interface TransactionCloser extends AutoCloseable {
        void close() throws RuntimeException;
    }

}
