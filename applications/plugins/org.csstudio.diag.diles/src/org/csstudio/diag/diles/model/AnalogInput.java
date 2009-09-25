package org.csstudio.diag.diles.model;

import java.util.Enumeration;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class AnalogInput extends Activity implements IPropertySource {

	public static String TERMINAL_OUT = "outTerminal";

	private double doubleOutput;

	public AnalogInput() {
		doubleOutput = 0.1d;
		setSize(new Dimension(37, 28));
	}

	public double getDoubleResult() {
		return this.doubleOutput;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#getEditableValue()
	 */
	@Override
	public Object getEditableValue() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {

		String[] combo = new String[] { "true", "false" };

		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(NAME, "Name"),
				new TextPropertyDescriptor(ACTIVITY_STATUS, "Status"),
				new PropertyDescriptor(COLUMN, "Column") };
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
		else if (propName.equals(ACTIVITY_STATUS)) {
			return String.valueOf(getDoubleResult());
		} else if (propName.equals(COLUMN))
			return getColumn();
		return null;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#getResult()
	 */
	@Override
	public boolean getResult() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#isPropertySet(java.lang.Object)
	 */
	@Override
	public boolean isPropertySet(Object id) {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#resetPropertyValue(java.lang.Object)
	 */
	@Override
	public void resetPropertyValue(Object id) {
	}

	public void setDoubleResult() {
		setOutput(TERMINAL_OUT, getDoubleResult());
	}

	public void setDoubleResult(double tf) {
		double oldDoubleOutput = this.doubleOutput;
		this.doubleOutput = tf;
		firePropertyChange(ACTIVITY_STATUS, oldDoubleOutput, this.doubleOutput);
	}

	public void setDoubleResultManually(double b) {
		setOutput(TERMINAL_OUT, b);
	}

	protected void setOutput(String terminal, double val) {
		Enumeration elements = sources.elements();
		Path p;
		while (elements.hasMoreElements()) {
			p = (Path) elements.nextElement();
			// System.out.println(p.getSourceName() + " " + terminal);
			if (p.getSourceName().equals(terminal)
					&& this.equals(p.getSource())) {
				p.setDoubleStatus(val);
				// System.out.println(getClass() + " " + getDoubleResult());

			}
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object propName, Object value) {
		String str = "test";

		str = (String) value;

		int comma = str.indexOf(",");
		if (propName.equals(SIZE))
			setSize(new Dimension(Integer.parseInt(str.substring(0, comma)),
					Integer.parseInt(str.substring(comma + 1))));
		else if (propName.equals(LOC))
			setLocation(new Point(Integer.parseInt(str.substring(0, comma)),
					Integer.parseInt(str.substring(comma + 1))));
		else if (propName.equals(NAME))
			setName(str);
		else if (propName.equals(ACTIVITY_STATUS)) {
			setDoubleResult(Double.valueOf(str));
		}
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#setResult()
	 */
	@Override
	public void setResult() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#setResultManually(boolean)
	 */
	@Override
	public void setResultManually(boolean b) {
		// TODO Auto-generated method stub

	}
}
