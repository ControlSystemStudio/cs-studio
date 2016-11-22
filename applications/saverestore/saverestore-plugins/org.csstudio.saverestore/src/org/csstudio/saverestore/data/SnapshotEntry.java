package org.csstudio.saverestore.data;

import java.io.Serializable;
import java.util.Objects;

import org.csstudio.saverestore.Utilities;
import org.diirt.vtype.VType;

/**
 * <code>SnapshotEntry</code> represents a single entry in the snapshot. It contains fields for all parameters that are
 * store in the snapshot file. All parameters except <code>value</code> and <code>selected</code> are fixed and cannot
 * be changed.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class SnapshotEntry implements Serializable {

    private static final long serialVersionUID = 5181175467248870613L;
    private final String name;
    private transient VType value;
    private boolean selected;
    private final String readbackName;
    private final transient VType readbackValue;
    private final String delta;
    private final boolean readOnly;

    /**
     * Constructs a new entry with the given name and value. The entry is selected and writable. Other parameters are
     * null or equivalent.
     *
     * @param name the name of the PV
     * @param value the PV value
     */
    public SnapshotEntry(String name, VType value) {
        this(name, value, true);
    }

    /**
     * Constructs a new entry from the given parameters. The entry is writable. Other parameters are null or equivalent.
     *
     * @param name the name of the PV
     * @param value the PV value
     * @param selected true if selected, false if not
     */
    public SnapshotEntry(String name, VType value, boolean selected) {
        this(name, value, selected, null, null, null, false);
    }

    /**
     * Constructs a new entry from pieces.
     *
     * @param name the name of the PV
     * @param value stored PV value
     * @param selected true if PV is selected or false otherwise
     * @param readbackName the name of the readback PV
     * @param readbackValue the stored readback PV value
     * @param delta string representing the value comparison function
     * @param readOnly true if PV is read only or false if writable
     */
    public SnapshotEntry(String name, VType value, boolean selected, String readbackName, VType readbackValue,
        String delta, boolean readOnly) {
        this.value = value == null ? VNoData.INSTANCE : value;
        this.name = name;
        this.selected = selected;
        this.readbackName = readbackName == null ? "" : readbackName;
        this.readbackValue = readbackValue == null ? VNoData.INSTANCE : readbackValue;
        this.delta = delta == null ? "" : delta;
        this.readOnly = readOnly;
    }

    /**
     * Returns PV value (value, timestamp, alarm stuff).
     *
     * @return the stored pv value
     */
    public VType getValue() {
        return value;
    }

    /**
     * Returns the name of the PV.
     *
     * @return the PV name
     */
    public String getPVName() {
        return name;
    }

    /**
     * Returns the delta used for validating the value of this PV.
     *
     * @see Threshold
     * @see SaveSetContent.SaveSetEntry#getDelta()
     * @return the delta
     */
    public String getDelta() {
        return delta;
    }

    /**
     * Returns the name of the readback PV associated with the setpoint represented by this entry.
     *
     * @return the readback name
     */
    public String getReadbackName() {
        return readbackName;
    }

    /**
     * Returns the readback PV value as it was at the time when the snapshot was taken.
     *
     * @return the readback pv value
     */
    public VType getReadbackValue() {
        return readbackValue;
    }

    /**
     * Returns true if this entry represents a read only PV or false if the PV can be read and written to. Read only PVs
     * cannot be restored.
     *
     * @return true if read only or false if not
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Returns true if this entry was selected for restoring in the GUI or false otherwise.
     *
     * @return true if selected or false if not selected
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Sets the value and the selected state of this entry. If the value is null {@link VNoData#INSTANCE} is set.
     *
     * @param value the value to set
     * @param selected true if selected or false otherwise
     */
    void set(VType value, boolean selected) {
        this.value = value == null ? VNoData.INSTANCE : value;
        this.selected = selected;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash(SnapshotEntry.class, name, selected, readOnly, readbackName, readbackValue, delta, value,
            readbackValue);
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
        } else if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        SnapshotEntry other = (SnapshotEntry) obj;
        if (!(Objects.equals(name, other.name) && selected == other.selected && readOnly == other.readOnly
            && Objects.equals(readbackName, other.readbackName) && Objects.equals(delta, other.delta))) {
            return false;
        }
        if (!Utilities.areVTypesIdentical(value, other.value, true)) {
            return false;
        }
        if (!Utilities.areVTypesIdentical(readbackValue, other.readbackValue, false)) {
            return false;
        }
        return true;
    }

}
