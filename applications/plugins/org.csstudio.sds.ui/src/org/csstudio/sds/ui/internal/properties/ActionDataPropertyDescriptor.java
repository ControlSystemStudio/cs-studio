package org.csstudio.sds.ui.internal.properties;

import org.csstudio.sds.model.properties.ActionData;
import org.csstudio.sds.ui.internal.properties.view.TextPropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Descriptor for a property that has a value which should be edited with a
 * {@link ActionData} cell editor.
 * 
 * @author Kai Meyer
 * @version $Revision$
 * 
 */
public final class ActionDataPropertyDescriptor extends TextPropertyDescriptor {
	
	/**
	 * The name of the property.
	 */
	private final String _name;

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
	public ActionDataPropertyDescriptor(final Object id, final String displayName, final
			String category) {
		super(id, displayName, category);
		_name = displayName;
		this.setLabelProvider(new ActionDataLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new ActionDataCellEditor(parent, _name);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	/**
	 * A label provider for a {@link ActionData} value.
	 * 
	 * @author Kai Meyer
	 * 
	 */
	private final class ActionDataLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof ActionData) {
				return ((ActionData)element).toString();
			} else {
				return element.toString();
			}
		}
	}
}
