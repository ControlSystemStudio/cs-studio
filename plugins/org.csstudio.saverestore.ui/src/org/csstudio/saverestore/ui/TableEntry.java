package org.csstudio.saverestore.ui;

import java.util.ArrayList;
import java.util.List;

import org.csstudio.saverestore.data.VNoData;
import org.csstudio.saverestore.ui.util.VTypePair;
import org.diirt.util.time.Timestamp;
import org.diirt.vtype.Alarm;
import org.diirt.vtype.AlarmSeverity;
import org.diirt.vtype.Time;
import org.diirt.vtype.VType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    private BooleanProperty selected = new SimpleBooleanProperty(this, "selected", true);
    private StringProperty pvName = new SimpleStringProperty(this, "pvName");
    private ObjectProperty<Timestamp> timestamp = new SimpleObjectProperty<>(this, "timestamp");
    private StringProperty status = new SimpleStringProperty(this, "status", "OK");
    private ObjectProperty<AlarmSeverity> severity = new SimpleObjectProperty<>(this, "severity", AlarmSeverity.NONE);
    private ObjectProperty<VTypePair> value = new SimpleObjectProperty<>(this, "value",
        new VTypePair(VNoData.INSTANCE, VNoData.INSTANCE));
    private ObjectProperty<VType> liveValue = new SimpleObjectProperty<>(this, "liveValue");
    private List<ObjectProperty<VTypePair>> compareValues = new ArrayList<>();
    private ObjectProperty<VTypePair> readback = new SimpleObjectProperty<>(this, "readback",
        new VTypePair(VNoData.INSTANCE, VNoData.INSTANCE));
    private StringProperty readbackName = new SimpleStringProperty(this, "readbackName");

    /**
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
            valueProperty().set(new VTypePair(liveValue.get(), val));
            for (ObjectProperty<VTypePair> o : compareValues) {
                o.set(new VTypePair(val, o.get().value));
            }
        } else {
            for (int i = compareValues.size(); i < index; i++) {
                compareValues.add(new SimpleObjectProperty<>(this, "CompareValue" + i));
            }
            compareValues.get(index - 1).set(new VTypePair(valueProperty().get().value, val));
        }
    }

    /**
     * Set the readback value of this entry.
     *
     * @param val the value
     */
    public void setReadbackValue(VType val) {
        if (readback.get().value != val) {
            readback.set(new VTypePair(liveValueProperty().get(), val));
        }
    }

    /**
     * Set the live value of this entry and updates the readback value as well.
     *
     * @param val the new value
     */
    public void setLiveValue(VType val) {
        liveValue.set(val);
        readback.set(new VTypePair(val, readback.get().value));
        value.set(new VTypePair(val, value.get().value));
    }
}
