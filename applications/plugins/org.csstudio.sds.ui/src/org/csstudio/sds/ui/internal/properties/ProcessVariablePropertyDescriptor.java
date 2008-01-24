package org.csstudio.sds.ui.internal.properties;

import org.csstudio.platform.model.pvs.IProcessVariableAddress;
import org.csstudio.sds.ui.internal.properties.view.PropertyDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Descriptor for a property that has a value which should be edited with a map
 * cell editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * IPropertyDescriptor pd = new MapPropertyDescriptor(&quot;surname&quot;, &quot;Last Name&quot;);
 * </pre>
 * 
 * </p>
 * 
 * @author Kai Meyer
 */
public class ProcessVariablePropertyDescriptor extends PropertyDescriptor {
	
	/**
	 * The name of the property.
	 */
	private final String _name;
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
	public ProcessVariablePropertyDescriptor(final Object id, final String displayName,
			final String category) {
		super(id, displayName);
		_name = displayName;
		assert category != null;
		setCategory(category);
		
		this.setLabelProvider(new ProcessVariableLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new ProcessVariableCellEditor(parent, _name);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	/**
	 * A label provider for a {@link IProcessVariableAddress}.
	 * 
	 * @author Kai Meyer
	 */
	private final class ProcessVariableLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof IProcessVariableAddress) {
				return ((IProcessVariableAddress)element).getFullName();
			} else {
				return element.toString();
			}
		}
		
	}
}
