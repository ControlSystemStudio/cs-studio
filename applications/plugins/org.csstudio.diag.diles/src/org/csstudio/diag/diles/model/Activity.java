package org.csstudio.diag.diles.model;

import java.util.HashMap;
import java.util.UUID;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

public abstract class Activity extends AbstractChartElement implements
		IPropertySource {

	private String unique_id;

	private int number_id;

	// Information content of the Model class
	private String name;
	private Point location = new Point(0, 0);
	private Dimension size = new Dimension(-1, -1);
	protected HashMap<String, Path> inputs = new HashMap<String, Path>(7);
	protected Vector<Path> targets = new Vector<Path>(2);
	protected Vector<Path> sources = new Vector<Path>(2);

	private int column;
	public int column_width = 80;

	public Activity() {
		unique_id = UUID.randomUUID().toString();
	}

	public void addSourceConnection(Path p) {
		sources.add(p);
		firePropertyChange(SOURCES, null, p);
	}

	public void addTargetConnection(Path p) {
		targets.add(p);
		firePropertyChange(TARGETS, null, p);
		inputs.put(p.getTargetName(), p);
	}

	public int getColumn() {
		return column;
	}

	public Vector<Path> getConnections() {
		Vector<Path> v = getSourceConnections();
		v.addAll(getTargetConnections());
		return v;
	}

	public Object getEditableValue() {
		return null;
	}

	public boolean getInput(String terminal) {
		Path p = inputs.get(terminal);
		return (p == null) ? false : p.getStatus();
	}

	public Point getLocation() {
		return location;
	}

	public String getName() {
		return name;
	}

	public int getNumberId() {
		return number_id;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
		new PropertyDescriptor(COLUMN, "Column") };
	}

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
			return getResult();
		}
		return null;
	}

	abstract public boolean getResult();

	public Dimension getSize() {
		return size;
	}

	public Vector<Path> getSourceConnections() {
		return (Vector<Path>) sources.clone();
	}

	public Vector<Path> getTargetConnections() {
		return (Vector<Path>) targets.clone();
	}

	public String getUniqueId() {
		return unique_id;
	}

	public boolean isPropertySet(Object id) {
		return true;
	}

	public void removeSourceConnection(Path p) {
		sources.remove(p);
		firePropertyChange(SOURCES, null, p);
	}

	public void removeTargetConnection(Path p) {
		targets.remove(p);
		firePropertyChange(TARGETS, null, p);
		inputs.remove(p.getTargetName());
	}

	public void resetPropertyValue(Object id) {
	}

	public void setLocation(Point place) {
		// if location is the same, nothing happens
		if (location.equals(place)) {
			return;
		}

		// puts the figure in the right column
		int custom_x = (int) (((place.x + 1.5 * column_width / 2) / column_width))
				* column_width;
		if (custom_x <= 0) {
			custom_x = column_width;
		}
		custom_x = custom_x - getSize().width / 2;

		// won't let the figure to put too far up
		int custom_y = place.y;
		if (custom_y <= 9) {
			custom_y = 10;
		}

		location = new Point(custom_x, custom_y);
		firePropertyChange(LOC, null, location);

		// +1 so it starts with 1
		column = (custom_x / column_width) + 1;
		firePropertyChange(COLUMN, null, column);
	}

	public void setName(String str) {
		if (str.equals(name))
			return;
		name = str;
		firePropertyChange(NAME, null, str);
	}

	public void setNumberId(int id) {
		number_id = id;
		firePropertyChange(NUMBER_ID, null, id);
	}

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
	}

	abstract public void setResult();

	abstract public void setResultManually(boolean b);

	public void setSize(Dimension dim) {
		if (size.equals(dim))
			return;
		size = dim;
		firePropertyChange(SIZE, null, size);
	}

	public void setUniqueId(String id) {
		unique_id = id;
		firePropertyChange(UNIQUE_ID, null, id);
	}

}