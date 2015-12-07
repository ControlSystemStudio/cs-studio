package org.csstudio.saverestore.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.FileUtilities;
import org.csstudio.saverestore.Utilities;
import org.diirt.util.array.ArrayInt;
import org.diirt.util.array.ListInt;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Array;
import org.diirt.vtype.Time;
import org.diirt.vtype.VTable;
import org.diirt.vtype.VType;
import org.diirt.vtype.ValueFactory;

/**
 *
 * <code>VSnapshot</code> describes the snapshot data. It contains the list of pv names together with their values at a
 * specific time.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class VSnapshot implements VType, Time, Array, Serializable {

    private static final long serialVersionUID = 2676226155070688049L;

    private final List<String> names;
    private final List<VType> values;
    private final List<Boolean> selected;
    private final Timestamp snapshotTime;
    private final BeamlineSet beamlineSet;
    private final Optional<Snapshot> snapshot;

    /**
     * Constructs a new data object from the {@link VTable}. The table is expected to have 3 columns: pv names, selected
     * state and data of types String, Boolean and VType respectively.
     *
     * @param snapshot the descriptor
     * @param table the table providing the data
     * @param snapshotTime the time when the snapshot was taken (this is not identical to the time when the snapshot was
     *            stored)
     */
    @SuppressWarnings("unchecked")
    public VSnapshot(Snapshot snapshot, VTable table, Timestamp snapshotTime) {
        if (table.getColumnCount() != 3) {
            throw new IllegalArgumentException("The table parameter has incorrect number of columns. Should be 3.");
        }
        List<String> n = null;
        List<Boolean> s = null;
        List<VType> d = null;
        for (int i = 0; i < 3; i++) {
            String name = table.getColumnName(i);
            if (FileUtilities.H_PV_NAME.equals(name)) {
                n = (List<String>) table.getColumnData(i);
            } else if (FileUtilities.H_SELECTED.equals(name)) {
                s = (List<Boolean>) table.getColumnData(i);
            } else if (FileUtilities.H_VALUE.equals(name)) {
                d = (List<VType>) table.getColumnData(i);
            }
        }
        if (n == null) {
            throw new IllegalArgumentException(
                    "The table does not contain the column " + FileUtilities.H_PV_NAME + ".");
        } else if (s == null) {
            throw new IllegalArgumentException(
                    "The table does not contain the column " + FileUtilities.H_SELECTED + ".");
        } else if (d == null) {
            throw new IllegalArgumentException("The table does not contain the column " + FileUtilities.H_VALUE + ".");
        }

        this.names = Collections.unmodifiableList(n);
        this.selected = Collections.unmodifiableList(s);
        this.values = Collections.unmodifiableList(d);
        this.snapshotTime = snapshotTime;
        this.beamlineSet = snapshot.getBeamlineSet();
        this.snapshot = Optional.of(snapshot);
    }

    /**
     * Constructs a new data object.
     *
     * @param snapshot the descriptor
     * @param names the names of pvs
     * @param selected flags indicating if the corresponding PVs are selected in the snapshot or not (only selected ones
     *            are restored)
     * @param values the values of the pvs
     * @param snapshotTime the time when the snapshot was taken (this is not identical to the time when the snapshot was
     *            stored)
     */
    public VSnapshot(Snapshot snapshot, List<String> names, List<Boolean> selected, List<VType> values,
            Timestamp snapshotTime) {
        if (names.size() != values.size()) {
            throw new IllegalArgumentException("The number of PV names does not match the number of values");
        }
        this.names = Collections.unmodifiableList(names);
        this.selected = Collections.unmodifiableList(selected);
        this.values = Collections.unmodifiableList(values);
        this.snapshotTime = snapshotTime;
        this.beamlineSet = snapshot.getBeamlineSet();
        this.snapshot = Optional.of(snapshot);
    }

    /**
     * Constructs a new data object.
     *
     * @param snapshot the descriptor
     * @param names the names of pvs
     * @param values the values of the pvs
     * @param snapshotTime the time when the snapshot was taken (this is not identical to the time when the snapshot was
     *            stored)
     */
    public VSnapshot(Snapshot snapshot, List<String> names, List<VType> values, Timestamp snapshotTime) {
        if (names.size() != values.size()) {
            throw new IllegalArgumentException("The number of PV names does not match the number of values");
        }
        this.names = Collections.unmodifiableList(names);
        final List<Boolean> selList = new ArrayList<>(names.size());
        this.names.forEach(e -> selList.add(Boolean.TRUE));
        this.selected = Collections.unmodifiableList(selList);
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
        final List<VType> list = new ArrayList<>(names.size());
        final List<Boolean> selList = new ArrayList<>(names.size());
        this.names.forEach(e -> {
            list.add(VNoData.INSTANCE);
            selList.add(Boolean.TRUE);
        });
        this.values = Collections.unmodifiableList(list);
        this.selected = Collections.unmodifiableList(selList);
        this.snapshotTime = null;
        this.beamlineSet = set;
        this.snapshot = Optional.empty();
    }

    /**
     * Returns the beamline set which this snapshot is for. If {@link #getSnapshot()} exists, this is always the same as
     * {@link #getSnapshot()#getBeamlineSet()}.
     *
     * @return the beamline set which this snapshot is for
     */
    public BeamlineSet getBeamlineSet() {
        return beamlineSet;
    }

    /**
     * Returns the snapshot descriptor if it exists, or an empty object, if this snapshot does not have a descriptor.
     * Snapshot does not have a descriptor if it has not been stored yet.
     *
     * @return the snapshot descriptor
     */
    public Optional<Snapshot> getSnapshot() {
        return snapshot;
    }

    /**
     * Returns the list of all pv names in this snapshot.
     *
     * @return the list of pv names
     */
    public List<String> getNames() {
        return names;
    }

    /**
     * Returns the list of all pv values in this snapshot. The order matches {@link #getNames()}.
     *
     * @return the list of pv values
     */
    public List<VType> getValues() {
        return values;
    }

    /**
     * Returns the list of selection states of the pvs. The order matches {@link #getNames()}. For PVs that are marked
     * as selected, the value is true, for unselected the value is false.
     *
     * @return the list of selection values
     */
    public List<Boolean> getSelected() {
        return selected;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Array#getData()
     */
    @Override
    public Object getData() {
        return Collections.unmodifiableList(Arrays.asList(names, values));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Array#getSizes()
     */
    @Override
    public ListInt getSizes() {
        return new ArrayInt(2, names.size());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Time#getTimestamp()
     */
    @Override
    public Timestamp getTimestamp() {
        return snapshotTime;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Time#getTimeUserTag()
     */
    @Override
    public Integer getTimeUserTag() {
        return (int) snapshotTime.getSec();
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Time#isTimeValid()
     */
    @Override
    public boolean isTimeValid() {
        return true;
    }

    /**
     * Returns true if this snapshot has been saved or false if only taken and not yet saved.
     *
     * @return true if this snapshot is saved or false otherwise
     */
    public boolean isSaved() {
        return snapshot.isPresent() ? snapshot.get().getComment() != null : false;
    }

    /**
     * Returns true if this snapshot can be saved or false if already saved. Snapshot can only be saved if it is a new
     * snapshot that has never been saved before. If the same snapshot has to be saved again a new instance of this
     * object has to be constructed.
     *
     * @return true if this snapshot can be saved or false if already saved or has no data
     */
    public boolean isSaveable() {
        return snapshotTime != null && !isSaved();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        if (isSaved()) {
            return Utilities.timestampToBigEndianString(snapshot.get().getDate(), true);
        } else {
            if (snapshotTime == null) {
                return beamlineSet.getName();
            }
            return Utilities.timestampToBigEndianString(snapshotTime.toDate(), true);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
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

    /**
     * Transforms this snapshot to a {@link VTable} with three columns: names (String), selected state (Boolean),
     * and data ({@link VType}).
     *
     * @return  {@link VTable} instance which contains the data
     */
    public VTable toVTable() {
        List<Class<?>> classes = Arrays.asList(String.class, Boolean.class, VType.class);
        List<Object> values = Arrays.asList(getNames(), getSelected(), getValues());
        List<String> columns = Arrays.asList(FileUtilities.H_PV_NAME, FileUtilities.H_SELECTED, FileUtilities.H_VALUE);
        return ValueFactory.newVTable(classes, columns, values);
    }
}
