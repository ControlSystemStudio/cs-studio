/*
 * This software is Copyright by the Board of Trustees of Michigan
 * State University (c) Copyright 2016.
 *
 * Contact Information:
 *   Facility for Rare Isotope Beam
 *   Michigan State University
 *   East Lansing, MI 48824-1321
 *   http://frib.msu.edu
 */
package org.csstudio.saverestore;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.csstudio.saverestore.data.SaveSetData;
import org.csstudio.saverestore.data.Threshold;
import org.diirt.vtype.VType;

/**
 * <code>SnapshotContent</code> provides the raw data as they were read from the snapshot file.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 */
public final class SnapshotContent {

    private final List<VType> data;
    private final List<String> names;
    private final List<Boolean> selected;
    private final List<String> readbacks;
    private final List<VType> readbackData;
    private final List<String> deltas;
    private final Date date;

    /**
     * Constructs a new snapshot content.
     *
     * @param date the time when snapshot was taken
     * @param names the list of PV names
     * @param selected the selected/unselected states of the PVs
     * @param data the individual PV data
     * @param readbacks the readback PV names
     * @param readbackData the stored readback values
     * @param deltas the threshold values of functions
     */
    SnapshotContent(Date date, List<String> names, List<Boolean> selected, List<VType> data, List<String> readbacks,
        List<VType> readbackData, List<String> deltas) {
        this.data = Collections.unmodifiableList(data);
        this.readbackData = Collections.unmodifiableList(readbackData);
        this.readbacks = Collections.unmodifiableList(readbacks);
        this.deltas = Collections.unmodifiableList(deltas);
        this.selected = Collections.unmodifiableList(selected);
        this.names = Collections.unmodifiableList(names);
        this.date = date;
    }

    /**
     * Returns the list of all PV values (value, timestamp, alarm stuff). Order of values matches the order in the
     * {@link #getNames()} list.
     *
     * @return the data list
     */
    public List<VType> getData() {
        return data;
    }

    /**
     * Returns the list of all setpoint PV names. The list is ordered as it is stored.
     *
     * @return the names list
     */
    public List<String> getNames() {
        return names;
    }

    /**
     * Returns the list of selected states for all PVs. The list is ordered according to order in the names list.
     *
     * @return the selected states
     */
    public List<Boolean> getSelected() {
        return selected;
    }

    /**
     * Returns the list of the readback PV names associated with the setpoint PV names.
     *
     * @return the readback names
     */
    public List<String> getReadbacks() {
        return readbacks;
    }

    /**
     * Returns the list of all readback PV values as they were at the time when the snapshot was taken.
     *
     * @return the readback PV values
     */
    public List<VType> getReadbackData() {
        return readbackData;
    }

    /**
     * Returns the list of deltas that are used to compare the values of PVs.
     *
     * @see SaveSetData#getDeltaList()
     * @see Threshold
     * @return the deltas list
     */
    public List<String> getDeltas() {
        return deltas;
    }

    /**
     * Returns the full date, when this snapshot was taken.
     *
     * @return the date
     */
    public Date getDate() {
        return date;
    }

}
