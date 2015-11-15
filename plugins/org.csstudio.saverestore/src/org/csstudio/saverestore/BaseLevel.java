package org.csstudio.saverestore;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * <code>BaseLevel</code> represents the base object that can be selected. These can be for example the top level
 * folders if the data comes from the file system. When the base level is created it is uniquely defined by its
 * storage name. This is also the only property that is required by the {@link DataProvider} to do further loading
 * of data.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public abstract class BaseLevel implements Serializable {
    private static final long serialVersionUID = 7503287144725281421L;

    /**
     * @return the name used for presentation of this base level
     */
    public abstract String getPresentationName();

    /**
     * @return the name used for storage of this base level; has to be unique and can only contain ASCII characters
     */
    public abstract String getStorageName();

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BaseLevel other = (BaseLevel) obj;
        return getStorageName().equals(other.getStorageName());
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(BaseLevel.class,getStorageName());
    }

}
