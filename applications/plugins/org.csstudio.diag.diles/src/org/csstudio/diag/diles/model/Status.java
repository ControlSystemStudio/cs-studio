package org.csstudio.diag.diles.model;

import java.util.List;

import org.csstudio.diag.diles.DilesEditor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class Status extends Activity {

	public static String TERMINAL_IN = "inTerminal";

	public Status() {
		setSize(new Dimension(25, 25));

		int temp_id = -1;
		List<Activity> list = DilesEditor.getChart().children;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof Status) {
				if (list.get(i).getNumberId() > temp_id) {
					temp_id = list.get(i).getNumberId();
				}
			}
		}
		temp_id++;
		setNumberId(temp_id);

		setName("Status Name");
	}

	@Override
	public boolean getResult() {
		return getInput(TERMINAL_IN);
	}

	@Override
	public void setResult() {
	}

	@Override
	public void setResultManually(boolean b) {
		firePropertyChange(ACTIVITY_STATUS, null, b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csstudio.diag.diles.model.Activity#getPropertyDescriptors()
	 */
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(NAME, "Name"),
				new PropertyDescriptor(COLUMN, "Column"),
				new PropertyDescriptor(NUMBER_ID, "ID")};
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

}
