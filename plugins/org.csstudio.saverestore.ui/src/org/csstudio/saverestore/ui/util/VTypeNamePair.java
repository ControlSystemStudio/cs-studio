package org.csstudio.saverestore.ui.util;

import org.diirt.vtype.VType;

/**
 * <code>VTypeNamePair</code> is a wrapper object around the {@link VType}, which in addition to the value provides also
 * the name of the PV that the value belongs to.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class VTypeNamePair {

    public final VType value;
    public final String name;

    /**
     * Construct a new vtype name pair.
     *
     * @param value the pv value
     * @param name the pv name
     */
    public VTypeNamePair(VType value, String name) {
        this.value = value;
        this.name = name;
    }
}
