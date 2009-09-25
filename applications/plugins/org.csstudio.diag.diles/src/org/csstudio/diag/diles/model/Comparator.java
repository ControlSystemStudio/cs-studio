package org.csstudio.diag.diles.model;

import java.util.Enumeration;

import org.eclipse.draw2d.geometry.Dimension;

public class Comparator extends Activity {

	public static String TERMINAL_A = "inTerminal_1";
	public static String TERMINAL_B = "inTerminal_2";
	public static String TERMINAL_OUT = "outTerminal";

	public Comparator() {
		setSize(new Dimension(42, 38));
	}

	public double getDoubleInput(String terminal) {
		Path p = inputs.get(terminal);
		return (p == null) ? null : p.getDoubleStatus();
	}

	@Override
	public boolean getInput(String terminal) {
		return false;
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
		else if (propName.equals(ACTIVITY_STATUS)) {
			// System.out.println("propResult " + getResult());
			return getResult();
		}
		return null;
	}

	@Override
	public boolean getResult() {
		// System.out.println(getDoubleInput(TERMINAL_A)+" "+getDoubleInput(TERMINAL_B));
		try {
			if (getDoubleInput(TERMINAL_A) >= getDoubleInput(TERMINAL_B)) {
				return true;
			} else {
				return false;
			}
			/*
			 * Handles NullPointerException which appears in case there is no
			 * input in Comparator.
			 */
		} catch (NullPointerException e) {
			return false;
		}
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

	/**
	 * Sets output depending on the input.
	 */
	@Override
	public void setResult() {
		setOutput(TERMINAL_OUT, getResult());
	}

	/**
	 * Sets output manually.
	 * 
	 * @param b
	 *            manual output
	 */
	@Override
	public void setResultManually(boolean b) {
		setOutput(TERMINAL_OUT, b);
	}

}
