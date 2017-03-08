// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

public interface Copyable<C extends Copyable<C>> {
    // intention is shallow...
    C copy();
}
