package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.visualparts.PointListCellEditor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Descriptor for a property that has a value which should be edited with a pointlist
 * cell editor.
 * @author Xihui Chen
 */
public class PointlistPropertyDescriptor extends TextPropertyDescriptor {
	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 * @param category
	 *            the category
	 */
	public PointlistPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);	
		setLabelProvider(new PointlistLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new PointListCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	/**
	 * A label provider for multiple line Strings.
	 * 
	 * @author Kai Meyer
	 */
	private final class PointlistLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof PointList) {
				PointList list = (PointList) element;
				StringBuffer buffer = new StringBuffer();
				if (list.size()>0) {
					this.addPointText(buffer, list.getPoint(0));
					for (int i=1;i<list.size();i++) {
						buffer.append("; ");
						this.addPointText(buffer, list.getPoint(i));	
					}
				}
				return buffer.toString();
			} else {
				return element.toString();
			}
		}
		
		/**
		 * Adds the text of the given Point to the StringBuffer.
		 * @param buffer
		 * 			The StringBuffer
		 * @param point
		 * 			The Point
		 */
		private void addPointText(final StringBuffer buffer, final Point point) {
			buffer.append("(");
			buffer.append(point.x);
			buffer.append(",");
			buffer.append(point.y);
			buffer.append(")");
		}
	}
}
