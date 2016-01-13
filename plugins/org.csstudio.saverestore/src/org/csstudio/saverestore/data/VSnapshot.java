package org.csstudio.saverestore.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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

    //these could all be final, but can't be because they are set during serialization, which is manual, due
    //to Timestamp not being serializable
    private List<String> names;
    private List<VType> values;
    private List<Boolean> selected;
    private Timestamp snapshotTime;
    private BeamlineSet beamlineSet;
    private Snapshot snapshot;

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

        this.names = new ArrayList<>(n);
        this.selected = new ArrayList<>(s);
        this.values = new ArrayList<>(d);
        this.snapshotTime = snapshotTime;
        this.beamlineSet = snapshot.getBeamlineSet();
        this.snapshot = snapshot;
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
        this.names = new ArrayList<>(names);
        this.selected = new ArrayList<>(selected);
        this.values = new ArrayList<>(values);
        this.snapshotTime = snapshotTime;
        this.beamlineSet = snapshot.getBeamlineSet();
        this.snapshot = snapshot;
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
        this.names = new ArrayList<>(names);
        final List<Boolean> selList = new ArrayList<>(names.size());
        this.names.forEach(e -> selList.add(Boolean.TRUE));
        this.selected = new ArrayList<>(selList);
        this.values = new ArrayList<>(values);
        this.snapshotTime = snapshotTime;
        this.beamlineSet = snapshot.getBeamlineSet();
        this.snapshot = snapshot;
    }

    /**
     * Constructs an empty snapshot object.
     *
     * @param set the beamline set for which the snapshot is for
     * @param names the names of pvs
     */
    public VSnapshot(BeamlineSet set, List<String> names) {
        this.names = new ArrayList<>(names);
        final List<VType> list = new ArrayList<>(names.size());
        final List<Boolean> selList = new ArrayList<>(names.size());
        this.names.forEach(e -> {
            list.add(VNoData.INSTANCE);
            selList.add(Boolean.TRUE);
        });
        this.values = new ArrayList<>(list);
        this.selected = new ArrayList<>(selList);
        this.snapshotTime = null;
        this.beamlineSet = set;
        this.snapshot = null;
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
        return Optional.ofNullable(snapshot);
    }

    /**
     * Returns the list of all pv names in this snapshot.
     *
     * @return the list of pv names
     */
    public List<String> getNames() {
        return Collections.unmodifiableList(names);
    }

    /**
     * Returns the list of all pv values in this snapshot. The order matches {@link #getNames()}.
     *
     * @return the list of pv values
     */
    public List<VType> getValues() {
        return Collections.unmodifiableList(values);
    }

    /**
     * Returns the list of selection states of the pvs. The order matches {@link #getNames()}. For PVs that are marked
     * as selected, the value is true, for unselected the value is false.
     *
     * @return the list of selection values
     */
    public List<Boolean> getSelected() {
        return Collections.unmodifiableList(selected);
    }

    /**
     * Add a PV to this snapshot.
     *
     * @param name the name of the pv to add
     * @param selected the selected flag
     * @param value the pv value
     */
    public void addPV(String name, boolean selected, VType value) {
        this.names.add(name);
        this.selected.add(selected);
        this.values.add(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Array#getData()
     */
    @Override
    public Object getData() {
        return Collections.unmodifiableList(Arrays.asList(names, selected, values));
    }

    /*
     * (non-Javadoc)
     *
     * @see org.diirt.vtype.Array#getSizes()
     */
    @Override
    public ListInt getSizes() {
        return new ArrayInt(3, names.size());
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
        return snapshot == null ? false : snapshot.getComment() != null;
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
            return Utilities.timestampToBigEndianString(snapshot.getDate(), true);
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
        return Objects.hash(beamlineSet, names, selected, snapshotTime, values);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (getClass() != obj.getClass()) {
            return false;
        }
        VSnapshot other = (VSnapshot) obj;
        return Objects.equals(beamlineSet, other.beamlineSet) && Objects.equals(names, other.names)
            && Objects.equals(snapshotTime, other.snapshotTime) && Objects.equals(values, other.values)
            && Objects.equals(selected, other.selected);
    }

    /**
     * Transforms this snapshot to a {@link VTable} with three columns: names (String), selected state (Boolean), and
     * data ({@link VType}).
     *
     * @return {@link VTable} instance which contains the data
     */
    public VTable toVTable() {
        List<Class<?>> classes = Arrays.asList(String.class, Boolean.class, VType.class);
        List<Object> values = Arrays.asList(getNames(), getSelected(), getValues());
        List<String> columns = Arrays.asList(FileUtilities.H_PV_NAME, FileUtilities.H_SELECTED, FileUtilities.H_VALUE);
        return ValueFactory.newVTable(classes, columns, values);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (snapshot == null) {
            out.writeObject("No Snapshot");
        } else {
            out.writeObject(snapshot);
        }
        if (snapshotTime == null) {
            out.writeLong(Long.valueOf(-1));
            out.writeInt(Integer.valueOf(-1));
        } else {
            out.writeLong(snapshotTime.getSec());
            out.writeInt(snapshotTime.getNanoSec());
        }
        out.writeObject(beamlineSet);
        out.writeObject(names);
        out.writeObject(values);
        out.writeObject(selected);
    }

    @SuppressWarnings("unchecked")
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object obj = in.readObject();
        if (obj instanceof String && "No Snapshot".equals((String)obj)) {
            snapshot = null;
        }
        long secs = in.readLong();
        int nano = in.readInt();
        if (secs == -1 && nano == -1) {
            snapshotTime = null;
        } else {
            snapshotTime = Timestamp.of(secs, nano);
        }
        beamlineSet = (BeamlineSet)in.readObject();
        names = (List<String>) in.readObject();
        values = (List<VType>) in.readObject();
        selected = (List<Boolean>) in.readObject();
    }
}
