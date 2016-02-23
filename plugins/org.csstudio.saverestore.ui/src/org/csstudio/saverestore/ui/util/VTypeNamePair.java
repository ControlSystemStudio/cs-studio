package org.csstudio.saverestore.ui.util;

import org.csstudio.saverestore.data.VSnapshot;
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
    public final VSnapshot snapshot;
    public final boolean readback;

    /**
     * Construct a new vtype name pair.
     *
     * @param value the pv value
     * @param name the pv name
     * @param snapshot the snapshot to which the values belong
     * @param readback true if this is a readback or false if a setpoint (readback is not editable)
     */
    public VTypeNamePair(VType value, String name, VSnapshot snapshot, boolean readback) {
        this.value = value;
        this.name = name;
        this.snapshot = snapshot;
        this.readback = readback;
    }
}
