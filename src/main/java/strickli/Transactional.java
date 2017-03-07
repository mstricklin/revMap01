// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli;

import lombok.Data;

public interface Transactional {
    TransactionCloser prepare();
    void commit();
    void rollback();
    void forget();

    @Data(staticConstructor="of")
    class TransactionException extends Exception {
        TransactionException(String msg) {
            super(msg);
        }
    }

    interface TransactionCloser extends AutoCloseable {
        void close() throws RuntimeException;
    }

}
