// CLASSIFICATION NOTICE: This file is UNCLASSIFIED
package strickli.cache;

import lombok.ToString;

@ToString
public abstract class XElement implements Keyed, Copyable<XElement>  {
    XElement(long id_) {
        id = id_;
    }
    public long getKey() {
        return id;
    }

    @Override
    public abstract XElement copy();
    // =================================
    long id;
}
