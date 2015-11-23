package org.csstudio.saverestore.data;

import org.diirt.vtype.VType;

/**
 *
 * <code>VNoData</code> represents a {@link VType} which has no data and or data type.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class VNoData implements VType {

    /** The singleton instance */
    public static final VNoData INSTANCE = new VNoData();

    private static final String TO_STRING = "---";

    private VNoData() {
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return TO_STRING;
    }
}
