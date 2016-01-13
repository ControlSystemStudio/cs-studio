package org.csstudio.saverestore.ui.util;

import java.util.Optional;

import org.csstudio.saverestore.data.Threshold;
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
    public final Optional<Threshold<?>> threshold;

    /**
     * Constructs a new pair.
     *
     * @param base the base value
     * @param value the value that can be compared to base
     * @param threshold the threshold values used for comparison
     */
    public VTypePair(VType base, VType value, Optional<Threshold<?>> threshold) {
        this.base = base;
        this.value = value;
        this.threshold = threshold;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
