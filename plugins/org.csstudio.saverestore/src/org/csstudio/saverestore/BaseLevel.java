package org.csstudio.saverestore;

import java.io.Serializable;

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
public interface BaseLevel extends Serializable {

    /**
     * @return the name used for presentation of this base level
     */
    String getPresentationName();

    /**
     * @return the name used for storage of this base level; has to be unique and can only contain ASCII characters
     */
    String getStorageName();

}
