package org.csstudio.diag.diles.model;

import java.util.Enumeration;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public abstract class Timer extends Activity {

	public static String TERMINAL_IN = "inTerminal";
	public static String TERMINAL_OUT = "outTerminal";

	private boolean time_delay = false;

	/**
	 * Number of delay timer has to wait.
	 */
	private int delay = 5;

	public Timer() {
		setSize(new Dimension(57, 36));
	}

	/**
	 * Getter for timer delay (in seconds).
	 * 
	 * @return delay
	 */
	public int getDelay() {
		return delay;
	}

	/**
	 * Setter for timer delay (in seconds).
	 * 
	 * @param seconds
	 */
	public void setDelay(int seconds) {
		delay = seconds;
		firePropertyChange(DELAY, null, seconds);
	}

	public boolean getTimeDelay() {
		return time_delay;
	}

	public void setTimeDelay(boolean td) {
		time_delay = td;
	}

	protected void setOutput(String terminal, boolean val) {
		Enumeration elements = sources.elements();
		Path p;
		while (elements.hasMoreElements()) {
			p = (Path) elements.nextElement();
			// System.out.println(p.getSourceName() + " " + terminal);
			if (p.getSourceName().equals(terminal)
					&& this.equals(p.getSource())) {
				p.setStatus(val);
				// System.out.println(getClass() + " " + getResult());
			}
		}
	}

	@Override
	public void setResult() {
		setOutput(TERMINAL_OUT, getResult());
	}

	@Override
	public void setResultManually(boolean b) {
		setOutput(TERMINAL_OUT, b);
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object propName, Object value) {
		String str = (String) value;
		int comma = str.indexOf(",");
		if (propName.equals(SIZE))
			setSize(new Dimension(Integer.parseInt(str.substring(0, comma)),
					Integer.parseInt(str.substring(comma + 1))));
		else if (propName.equals(LOC))
			setLocation(new Point(Integer.parseInt(str.substring(0, comma)),
					Integer.parseInt(str.substring(comma + 1))));
		else if (propName.equals(NAME))
			setName(str);
		else if (propName.equals(DELAY))
			setDelay(Integer.valueOf(str));
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {/*
										 * new TextPropertyDescriptor(NAME,
										 * "Name"), new PropertyDescriptor(SIZE,
										 * "Size"), new
										 * TextPropertyDescriptor(LOC,
										 * "Location"), new
										 * PropertyDescriptor(ACTIVITY_STATUS,
										 * "Status"),
										 */
		new PropertyDescriptor(COLUMN, "Column"),
				new TextPropertyDescriptor(DELAY, "Time Delay (in seconds)") };
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#getPropertyValue(java.lang.Object)
	 */
	@Override
	public Object getPropertyValue(Object propName) {
		if (propName.equals(SIZE))
			return getSize().width + "," + getSize().height;
		else if (propName.equals(LOC))
			return getLocation().x + "," + getLocation().y;
		else if (propName.equals(NAME))
			return getName();
		else if (propName.equals(COLUMN))
			return getColumn();
		else if (propName.equals(ACTIVITY_STATUS))
			return getResult();
		else if (propName.equals(DELAY))
			return String.valueOf(getDelay());
		return null;
	}
}
