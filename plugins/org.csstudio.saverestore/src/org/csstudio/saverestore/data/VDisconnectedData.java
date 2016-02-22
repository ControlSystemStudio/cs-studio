package org.csstudio.saverestore.data;

import java.io.Serializable;

import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.VType;

/**
 *
 * <code>VDisconnectedData</code> represents a {@link VType} for a disconnected PV, where the data type is not known.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public final class VDisconnectedData implements VType, Alarm, Serializable {

    private static final long serialVersionUID = -2399970529728581034L;

    /** The singleton instance */
    public static final VDisconnectedData INSTANCE = new VDisconnectedData();

    private static final String TO_STRING = "---";
    public static final String DISCONNECTED = "DISCONNECTED";

    private VDisconnectedData() {
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
