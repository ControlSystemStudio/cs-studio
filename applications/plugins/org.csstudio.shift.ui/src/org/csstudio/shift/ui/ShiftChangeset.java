package org.csstudio.shift.ui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import org.csstudio.shift.Shift;
import org.csstudio.shift.ShiftBuilder;

public class ShiftChangeset {
	 private ShiftBuilder shiftBuilder;

	    protected final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	    public void addPropertyChangeListener(final PropertyChangeListener listener) {
		    changeSupport.addPropertyChangeListener(listener);
	    }

	    public void removePropertyChangeListener(final PropertyChangeListener listener) {
		    changeSupport.removePropertyChangeListener(listener);
	    }

	    public ShiftChangeset() {
		    shiftBuilder = ShiftBuilder.withType("");
	    }

	    public ShiftChangeset(final Shift shift) throws IOException {
		    shiftBuilder = ShiftBuilder.shift(shift);
	    }

	    public Shift getShift() throws IOException {
	    	return this.shiftBuilder.build();
	    }

	    public void setShiftBuilder(final ShiftBuilder shiftBuilder) {
	    	final ShiftBuilder oldValue = this.shiftBuilder;
		    this.shiftBuilder = shiftBuilder;
		    changeSupport.firePropertyChange("shiftBuilder", oldValue, this.shiftBuilder);
	    }
}
