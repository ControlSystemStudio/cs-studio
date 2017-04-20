package org.csstudio.saverestore.ui;

import org.csstudio.saverestore.data.SaveSetEntry;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * An observable built around a {@link SaveSetEntry} to simplify the use of javafx controls
 *
 * @author Kunal Shroff
 *
 */
public class ObservableSaveSetEntry {

    private final SimpleStringProperty pvname;
    private final SimpleStringProperty readback;
    private final SimpleStringProperty delta;
    final SimpleBooleanProperty readonly;

    ObservableSaveSetEntry(SaveSetEntry saveSetEntry) {
        this.pvname = new SimpleStringProperty(saveSetEntry.getPVName());
        this.readback = new SimpleStringProperty(saveSetEntry.getReadback());
        this.delta = new SimpleStringProperty(saveSetEntry.getDelta());
        this.readonly = new SimpleBooleanProperty(saveSetEntry.isReadOnly());
    }

    public String getPvname() {
        return pvname.get();
    }

    public void setPvname(String pvName) {
        this.pvname.set(pvName);
    }

    public String getReadback() {
        return readback.get();
    }

    public void setReadback(String readback) {
        this.readback.set(readback);
    }

    public String getDelta() {
        return delta.get();
    }

    public void setDelta(String delta) {
        this.delta.set(delta);
    }

    public boolean isReadonly() {
        return readonly.get();
    }

    public void setReadonly(boolean readonly) {
        this.readonly.set(readonly);
    }

    public SaveSetEntry getEntry(){
        return new SaveSetEntry(getPvname(), getReadback(), getDelta(), isReadonly());
    }

    /**
     * Returns the string with which this save set is represented in the file.
     *
     * @return the string representing this save set
     */
    public String getSaveString() {
        StringBuilder sb = new StringBuilder(200);
        sb.append(getPvname()).append(',').append(getReadback()).append(',');
        if (getDelta().indexOf(',') > -1) {
            sb.append('"').append(getDelta()).append('"');
        } else {
            sb.append(getDelta());
        }
        sb.append(',').append(isReadonly());
        return sb.toString();
    }

    @Override
    public String toString() {
        return getSaveString();
    }
}