package org.csstudio.diag.diles.model;

import java.util.ArrayList;
import java.util.List;

public class Chart extends AbstractChartElement {
	protected List<Activity> children = new ArrayList<Activity>();

	protected boolean active = false;

	public void addChild(Activity child) {
		children.add(child);
		firePropertyChange(CHILD, null, child);
	}

	public void changeActive() {
		firePropertyChange(ACTIVE_COLUMN, null, null);
	}

	public boolean getActive() {
		return this.active;
	}

	public List<Activity> getChildren() {
		return children;
	}

	public void removeChild(Activity child) {
		children.remove(child);
		firePropertyChange(CHILD, null, child);
	}

	public void setActive(boolean b) {
		if (b == this.active)
			return;
		boolean temp = this.active;
		this.active = b;
		firePropertyChange(ACTIVE_COLUMN, null, null);

	}
}