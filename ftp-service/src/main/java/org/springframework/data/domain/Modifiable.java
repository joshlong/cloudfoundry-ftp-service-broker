package org.springframework.data.domain;

import java.io.Serializable;

public interface Modifiable<ID extends Serializable> extends Persistable<ID> {
    boolean isPersisted();

    void setPersisted(boolean p);
}
