package org.csstudio.saverestore;

import java.util.Date;
import java.util.List;

import org.diirt.vtype.VType;

/**
 * <code>SnapshotContent</code> provides the raw data as they were read from the snapshot file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 * @param <T>
 */
public class SnapshotContent {

    /** The list of all PV data (value, timestamp, alarm stuff). Order matches the {@link #names} */
    public final List<VType> data;
    /** The list of all PV names */
    public final List<String> names;
    /** The list of selected states. Order matches the {@link #names} */
    public final List<Boolean> selected;
    /** The date of the snapshot */
    public final Date date;

    /**
     * Constructs a new snapshot content.
     *
     * @param date the time when snapshot was taken
     * @param names the list of pv names
     * @param selected the selected/unselected states of the pvs
     * @param data the individual pv data
     */
    SnapshotContent(Date date, List<String> names, List<Boolean> selected, List<VType> data) {
        this.data = data;
        this.selected = selected;
        this.names = names;
        this.date = date;
    }
}
