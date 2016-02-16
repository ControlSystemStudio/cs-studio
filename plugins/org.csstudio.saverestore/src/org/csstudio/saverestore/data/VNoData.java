package org.csstudio.saverestore.data;

import java.io.Serializable;

import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;

/**
 *
 * <code>VNoData</code> represents a {@link VType} which has no data and or data type.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class VNoData implements VType, Alarm, Serializable {

    private static final long serialVersionUID = -2399970529728581034L;

    /** The singleton instance */
    public static final VNoData INSTANCE = new VNoData();

    private static final String TO_STRING = "---";
    private static final String DISCONNECTED = "DISCONNECTED";

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

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Alarm#getAlarmSeverity()
     */
    @Override
    public AlarmSeverity getAlarmSeverity() {
        return AlarmSeverity.UNDEFINED;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Alarm#getAlarmName()
     */
    @Override
    public String getAlarmName() {
        return DISCONNECTED;
    }
}
