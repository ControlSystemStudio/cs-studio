package org.csstudio.saverestore.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.csstudio.saverestore.Utilities;
import org.csstudio.saverestore.data.Threshold;
import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.ui.util.SingleListenerBooleanProperty;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * <code>TableEntry</code> represents a single line in the snapshot viewer table. It provides values for all columns in
 * the table, be it a single snapshot table or a multi snapthos table.
 *
 * @author <a href="mailto:jaka.bobnar@cosylab.com">Jaka Bobnar</a>
 *
 */
public class TableEntry {

    private IntegerProperty id = new SimpleIntegerProperty(this, "id");
    private BooleanProperty selected = new SingleListenerBooleanProperty(this, "selected", true);
    private StringProperty pvName = new SimpleStringProperty(this, "pvName");
    private ObjectProperty<Timestamp> timestamp = new SimpleObjectProperty<>(this, "timestamp");
    private StringProperty status = new SimpleStringProperty(this, "status", "OK");
    private ObjectProperty<AlarmSeverity> severity = new SimpleObjectProperty<>(this, "severity", AlarmSeverity.NONE);
    private ObjectProperty<VTypePair> value = new SimpleObjectProperty<>(this, "value",
        new VTypePair(VNoData.INSTANCE, VNoData.INSTANCE, Optional.empty()));
    private ObjectProperty<VType> liveValue = new SimpleObjectProperty<>(this, "liveValue", VNoData.INSTANCE);
    private List<ObjectProperty<VTypePair>> compareValues = new ArrayList<>();
    private ObjectProperty<VTypePair> readback = new SimpleObjectProperty<>(this, "readback",
        new VTypePair(VNoData.INSTANCE, VNoData.INSTANCE, Optional.empty()));
    private StringProperty readbackName = new SimpleStringProperty(this, "readbackName");
    private BooleanProperty liveStoredEqual = new SingleListenerBooleanProperty(this, "liveStoredEqual", true);
    private ObjectProperty<VTypePair> storedReadback = new SimpleObjectProperty<>(this, "storedReadback",
        new VTypePair(VNoData.INSTANCE, VNoData.INSTANCE, Optional.empty()));
    private List<ObjectProperty<VTypePair>> compareStoredReadbacks = new ArrayList<>();
    private Optional<Threshold<?>> threshold = Optional.empty();

    /**
     * Returns the property that describes whether the live and stored values are identical. This property can only have
     * one listener.
     *
     * @return the property describing if live and stored value are identical (in value terms only)
     */
    public ReadOnlyBooleanProperty liveStoredEqualProperty() {
        return liveStoredEqual;
    }

    /**
     * Returns the property that describes whether the property is selected or not. This property can only have one
     * listener.
     *
     * @return the property describing if the entry is selected or not
     */
    public BooleanProperty selectedProperty() {
        return selected;
    }

    /**
     * @return the property providing the pv name
     */
    public StringProperty pvNameProperty() {
        return pvName;
    }

    /**
     * @return the property providing the readback pv name
     */
    public StringProperty readbackNameProperty() {
        return readbackName;
    }

    /**
     * @return the property providing the readback value
     */
    public ObjectProperty<VTypePair> readbackProperty() {
        return readback;
    }

    /**
     * @return the property providing the timestamp of the primary snapshot value
     */
    public ObjectProperty<Timestamp> timestampProperty() {
        return timestamp;
    }

    /**
     * @return the property providing the alarm severity of the primary snapshot value
     */
    public ObjectProperty<AlarmSeverity> severityProperty() {
        return severity;
    }

    /**
     * @return the property providing the alarm status of the primary snapshot value
     */
    public StringProperty statusProperty() {
        return status;
    }

    /**
     * @return the property providing the value of the primary snapshot value
     */
    public ObjectProperty<VTypePair> valueProperty() {
        return value;
    }

    /**
     * @return the property providing the unique (incremental id) used for sorting the entries
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * @return the property providing the live PV value
     */
    public ObjectProperty<VType> liveValueProperty() {
        return liveValue;
    }

    /**
     * @return the property providing the stored readback vs stored setpoint value
     */
    public ObjectProperty<VTypePair> storedReadbackProperty() {
        return storedReadback;
    }

    /**
     * @param index the index of the compared value (starts with 1)
     * @return the property providing the compared value for the given index
     */
    public ObjectProperty<VTypePair> compareValueProperty(int index) {
        if (index == 0) {
            throw new IndexOutOfBoundsException("Index has to be larger than 0.");
        } else {
            return compareValues.get(index - 1);
        }
    }

    /**
     * @param index the index of the compared value (starts with 1)
     * @return the property providing the compares stored readback value for the given index
     */
    public ObjectProperty<VTypePair> compareStoredReadbackProperty(int index) {
        if (index == 0) {
            throw new IndexOutOfBoundsException("Index has to be larger than 0.");
        } else {
            return compareStoredReadbacks.get(index - 1);
        }
    }

    /**
     * Updates the snapshot value for the primary snapshot (index = 0) or for the snapshot compared to the primary
     * (index > 0).
     *
     * @param val the value to set
     * @param index the index of the snapshot to which the value belongs
     */
    public void setSnapshotValue(VType val, int index) {
        if (index == 0) {
            if (val instanceof Alarm) {
                severity.set(((Alarm) val).getAlarmSeverity());
                status.set(((Alarm) val).getAlarmName());
            } else {
                severity.set(AlarmSeverity.NONE);
                status.set("OK");
            }
            if (val instanceof Time) {
                timestamp.set(((Time) val).getTimestamp());
            } else {
                timestamp.set(Timestamp.now());
            }
            value.set(new VTypePair(liveValue.get(), val, threshold));
            compareValues.forEach(o -> o.set(new VTypePair(val, o.get().value, threshold)));
            liveStoredEqual.set(Utilities.areValuesEqual(liveValue.get(), val, threshold));
            storedReadback.set(new VTypePair(val, storedReadback.get().value, threshold));
        } else {
            for (int i = compareValues.size(); i < index; i++) {
                compareValues.add(new SimpleObjectProperty<>(this, "CompareValue" + i,
                    new VTypePair(VNoData.INSTANCE, VNoData.INSTANCE, threshold)));
                compareStoredReadbacks.add(new SimpleObjectProperty<>(this, "CompareStoredReadback" + i,
                    new VTypePair(VNoData.INSTANCE, VNoData.INSTANCE, threshold)));
            }
            compareValues.get(index - 1).set(new VTypePair(valueProperty().get().value, val, threshold));
            compareStoredReadbacks.get(index - 1)
                .set(new VTypePair(val, compareStoredReadbacks.get(index - 1).get().value, threshold));
        }
    }

    /**
     * Set the stored readback value for the primary snapshot of for the snapshots compared to the primary one.
     *
     * @param val the value to set
     * @param index the index of the snapshot
     */
    public void setStoredReadbackValue(VType val, int index) {
        if (index == 0) {
            storedReadback.set(new VTypePair(storedReadback.get().base, val, threshold));
        } else {
            for (int i = compareValues.size(); i < index; i++) {
                compareStoredReadbacks.add(new SimpleObjectProperty<>(this, "CompareStoredReadback" + i,
                    new VTypePair(VNoData.INSTANCE, VNoData.INSTANCE, threshold)));
            }
            compareStoredReadbacks.get(index - 1)
                .set(new VTypePair(compareStoredReadbacks.get(index - 1).get().base, val, threshold));
        }
    }

    /**
     * Set the readback value of this entry.
     *
     * @param val the value
     */
    public void setReadbackValue(VType val) {
        if (readback.get().value != val) {
            readback.set(new VTypePair(liveValueProperty().get(), val, threshold));
        }
    }

    /**
     * Set the live value of this entry and updates the readback value as well.
     *
     * @param val the new value
     */
    public void setLiveValue(VType val) {
        liveValue.set(val);
        readback.set(new VTypePair(val, readback.get().value, threshold));
        VType stored = value.get().value;
        value.set(new VTypePair(val, stored, threshold));
        liveStoredEqual.set(Utilities.areValuesEqual(val, stored, threshold));
    }

    /**
     * Set the threshold value for this entry. All value comparisons related to this entry are calculated using the
     * threshold (if it exists). Once the threshold is set, it cannot be unset.
     *
     * @param threshold the threshold
     */
    public void setThreshold(Optional<Threshold<?>> threshold) {
        if (threshold.isPresent()) {
            this.threshold = threshold;
            VType val = this.value.get().value;
            this.value.set(new VTypePair(this.value.get().base, val, threshold));
            this.liveStoredEqual.set(Utilities.areValuesEqual(liveValue.get(), val, threshold));
            this.compareValues.forEach(e -> e.set(new VTypePair(val, e.get().value, threshold)));
            this.readback.set(new VTypePair(this.readback.get().base, this.readback.get().value, threshold));
            this.storedReadback
                .set(new VTypePair(this.storedReadback.get().base, this.storedReadback.get().value, threshold));
            this.compareStoredReadbacks.forEach(e -> e.set(new VTypePair(e.get().base, e.get().value, threshold)));
        }
    }
}
