package org.csstudio.opibuilder.visualparts;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.platform.ui.util.CustomMediaFactory;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * Descriptor for a property that is a boolean value which should be edited with
 * a boolean cell editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * IPropertyDescriptor pd = new BooleanPropertyDescriptor(&quot;fg&quot;, &quot;boolean&quot;);
 * </pre>
 * 
 * </p>
 * 
 * @author Xihui Chen
 */
public final class BooleanPropertyDescriptor extends PropertyDescriptor {

	/**
	 * Creates an property descriptor with the given id and display name.
	 * 
	 * @param id
	 *            the id of the property
	 * @param displayName
	 *            the name to display for the property
	 */
	public BooleanPropertyDescriptor(final Object id, final String displayName) {
		super(id, displayName);

		setLabelProvider(new BooleanLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new CheckboxCellEditor(parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

	/**
	 * A label provider for boolean value, which displays a checked or unchecked box image.
	 * 
	 * @author Xihui Chen
	 * 
	 */
	private final class BooleanLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Image getImage(final Object element) {
			if (element instanceof Boolean) {
				if (((Boolean)element).booleanValue()) {
					return CustomMediaFactory.getInstance().getImageFromPlugin(OPIBuilderPlugin.PLUGIN_ID, "icons/checked.gif");
				} else {
					return CustomMediaFactory.getInstance().getImageFromPlugin(OPIBuilderPlugin.PLUGIN_ID, "icons/unchecked.gif");
				}
			} else {
				return null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof Boolean) {
				if (((Boolean)element).booleanValue()) {
					return "yes";
				} else {
					return "no";
				}
			} else {
				return element.toString();
			}
		}
	}
}
