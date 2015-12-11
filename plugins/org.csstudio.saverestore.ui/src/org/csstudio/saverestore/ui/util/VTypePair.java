package org.csstudio.saverestore.ui.util;

import org.diirt.vtype.VType;

/**
 *
 * <code>VTypePair</code> is an object that combines two VType objects, which can later be compared one to another.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class VTypePair {

    public final VType base;
    public final VType value;

    /**
     * Constructs a new pair.
     *
     * @param base the base value
     * @param value the value that can be compared to base
     */
    public VTypePair(VType base, VType value) {
        this.base = base;
        this.value = value;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return value == null ? null : value.toString();
    }
}
