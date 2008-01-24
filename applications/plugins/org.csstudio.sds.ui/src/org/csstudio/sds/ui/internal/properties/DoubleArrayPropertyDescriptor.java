package org.csstudio.sds.ui.internal.properties;

import org.csstudio.sds.ui.internal.properties.view.TextPropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Descriptor for a property that has a value which should be edited with a
 * double cell editor.
 * 
 * @author Alexander Will
 * @version $Revision$
 * 
 */
public final class DoubleArrayPropertyDescriptor extends TextPropertyDescriptor {

	/**
	 * Standard constructor.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 * @param category
	 *            the category
	 */
	public DoubleArrayPropertyDescriptor(final Object id, final String displayName, final
			String category) {
		super(id, displayName, category);
		
		this.setLabelProvider(new DoubleArrayLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new DoubleArrayCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	/**
	 * A label provider for a double[] value.
	 * 
	 * @author Kai Meyer
	 * 
	 */
	private final class DoubleArrayLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof double[]) {
				StringBuffer buffer = new StringBuffer();
				buffer.append("(");
				double[] array = (double[]) element;
				if (array.length>0) {
					buffer.append(array[0]);
					for (int i=1;i<array.length;i++) {
						buffer.append("; ");
						buffer.append(array[i]);
					}
				}
				buffer.append(")");
				return buffer.toString();
			} else {
				return element.toString();
			}
		}
	}
}
