package org.csstudio.diag.diles.model;

import java.util.List;

import org.csstudio.diag.diles.DilesEditor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.ComboBoxPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class HardwareTrueFalse extends Logic implements IPropertySource {

	public static final String HARDWARE_TRUE_FALSE = "hardware_true_false";

	private boolean true_false;

	public HardwareTrueFalse() {
		true_false = true;
		setSize(new Dimension(34, 27));

		int temp_id = -1;
		List<Activity> list = DilesEditor.getChart().children;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof HardwareTrueFalse) {
				if (list.get(i).getNumberId() > temp_id) {
					temp_id = list.get(i).getNumberId();
				}
			}
		}
		temp_id++;
		setNumberId(temp_id);

		if (getName() == null) {
			setName("Hardware Input Name");
		}
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
				new TextPropertyDescriptor(NAME, "Name"),/*
														 * new
														 * TextPropertyDescriptor
														 * (SIZE, "Size"), new
														 * TextPropertyDescriptor
														 * (LOC, "Location"),
														 */
				new ComboBoxPropertyDescriptor(ACTIVITY_STATUS, "Status", combo),
				new PropertyDescriptor(COLUMN, "Column"),
				new PropertyDescriptor(NUMBER_ID, "ID") };
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
			if (getResult() == true) {
				return 0;
			} else {
				return 1;
			}
		} else if (propName.equals(COLUMN))
			return getColumn();
		else if (propName.equals(NUMBER_ID)) {
			return getNumberId();
		}
		return null;
	}

	@Override
	public boolean getResult() {
		return this.true_false;
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#isPropertySet(java.lang.Object)
	 */
	@Override
	public boolean isPropertySet(Object id) {
		return true;
	}

	@Override
	public void resetPropertyValue(Object id) {
	}

	/* (non-Javadoc)
	 * @see org.csstudio.diag.diles.model.Activity#setPropertyValue(java.lang.Object, java.lang.Object)
	 */
	@Override
	public void setPropertyValue(Object propName, Object value) {
		String str = "test";

		if (!propName.equals(ACTIVITY_STATUS)) {
			str = (String) value;
		}

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
			if ((Integer) value == 0) {
				setResult(true);
			} else if ((Integer) value == 1) {
				setResult(false);
			}
		}
	}

	@Override
	public void setResult() {
		setOutput(TERMINAL_OUT, getResult());
	}

	public void setResult(boolean tf) {
		boolean oldTrueFalse = this.true_false;
		this.true_false = tf;
		firePropertyChange(ACTIVITY_STATUS, oldTrueFalse, this.true_false);
	}

	@Override
	public void setResultManually(boolean b) {
		setOutput(TERMINAL_OUT, b);
	}

}
