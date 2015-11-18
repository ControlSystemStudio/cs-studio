package org.csstudio.saverestore.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.Utilities;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ListInt;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Array;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;

/**
 *
 * <code>VSnapshot</code> describes the snapshot data. It contains the list of pv names together with
 * their values at a specific time.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class VSnapshot implements VType, Time, Array, Serializable {

    private static final long serialVersionUID = 2676226155070688049L;

    private final List<String> names;
    private final List<VType> values;
    private final Timestamp snapshotTime;
    private final BeamlineSet beamlineSet;
    private final Optional<Snapshot> snapshot;

    /**
     * Constructs a new data object.
     *
     * @param snapshot the descriptor
     * @param names the names of pvs
     * @param values the values of the pvs
     * @param snapshotTime the time when the snapshot was taken (this is not identical to the time when
     *           the snapshot was stored)
     */
    public VSnapshot(Snapshot snapshot, List<String> names, List<VType> values, Timestamp snapshotTime) {
        if (names.size() != values.size()) {
            throw new IllegalArgumentException("The number of PV names does not match the number of values");
        }
        this.names = Collections.unmodifiableList(names);
        this.values = Collections.unmodifiableList(values);
        this.snapshotTime = snapshotTime;
        this.beamlineSet = snapshot.getBeamlineSet();
        this.snapshot = Optional.of(snapshot);
    }

    /**
     * Constructs an empty snapshot object.
     *
     * @param set the beamline set for which the snapshot is for
     * @param names the names of pvs
     */
    public VSnapshot(BeamlineSet set, List<String> names) {
        this.names = Collections.unmodifiableList(names);
        List<VType> list = new ArrayList<>();
        this.names.forEach(e -> list.add(VNoData.INSTANCE));
        this.values = Collections.unmodifiableList(list);
        this.snapshotTime = null;
        this.beamlineSet = set;
        this.snapshot = Optional.empty();
    }

    /**
     * @return the beamline set which this snapshot is for
     */
    public BeamlineSet getBeamlineSet() {
        return beamlineSet;
    }

    /**
     * @return the snapshot descriptor if it exists, or an empty object, if this is a no data snapshot
     *          representation
     */
    public Optional<Snapshot> getSnapshot() {
        return snapshot;
    }

    /**
     * @return the list of pv names
     */
    public List<String> getNames() {
        return names;
    }

    /**
     * @return the list of pv values
     */
    public List<VType> getValues() {
        return values;
    }

    /*
     * (non-Javadoc)
     * @see org.diirt.vtype.Array#getData()
     */
    @Override
    public Object getData() {
        return Collections.unmodifiableList(Arrays.asList(names,values));
    }

    /*
     * (non-Javadoc)
     * @see org.diirt.vtype.Array#getSizes()
     */
    @Override
    public ListInt getSizes() {
        return new ArrayInt(2,names.size());
    }

    /*
     * (non-Javadoc)
     * @see org.diirt.vtype.Time#getTimestamp()
     */
    @Override
    public Timestamp getTimestamp() {
        return snapshotTime;
    }

    /*
     * (non-Javadoc)
     * @see org.diirt.vtype.Time#getTimeUserTag()
     */
    @Override
    public Integer getTimeUserTag() {
        return (int)snapshotTime.getSec();
    }

    /*
     * (non-Javadoc)
     * @see org.diirt.vtype.Time#isTimeValid()
     */
    @Override
    public boolean isTimeValid() {
        return true;
    }

    /**
     * @return true if this snapshot has been saved or false if only taken and not saved
     */
    public boolean isSaved() {
        return snapshot.isPresent() ? snapshot.get().getComment() != null : false;
    }

    /**
     * @return true if this snapshot can be saved or false if already saved or has no data
     */
    public boolean isSaveable() {
        return snapshotTime != null && !isSaved();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (isSaved()) {
            return Utilities.timestampToBigEndianString(snapshot.get().getDate(),true);
        } else {
            if (snapshotTime == null) {
                return beamlineSet.getName();
            }
            return Utilities.timestampToBigEndianString(snapshotTime.toDate(),true);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((beamlineSet == null) ? 0 : beamlineSet.hashCode());
        result = prime * result + ((names == null) ? 0 : names.hashCode());
        result = prime * result + ((snapshotTime == null) ? 0 : snapshotTime.hashCode());
        result = prime * result + ((values == null) ? 0 : values.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VSnapshot other = (VSnapshot) obj;
        if (beamlineSet == null) {
            if (other.beamlineSet != null)
                return false;
        } else if (!beamlineSet.equals(other.beamlineSet))
            return false;
        if (names == null) {
            if (other.names != null)
                return false;
        } else if (!names.equals(other.names))
            return false;
        if (snapshotTime == null) {
            if (other.snapshotTime != null)
                return false;
        } else if (!snapshotTime.equals(other.snapshotTime))
            return false;
        if (values == null) {
            if (other.values != null)
                return false;
        } else if (!values.equals(other.values))
            return false;
        return true;
    }



}
