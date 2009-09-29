package org.csstudio.opibuilder.properties.support;

import org.csstudio.opibuilder.visualparts.MultiLineTextCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**The property descriptor for multiline text editing.
 * @author Xihui Chen
 *
 */
public class MultiLineTextPropertyDescriptor extends TextPropertyDescriptor {

	public MultiLineTextPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
		setLabelProvider(new MultiLineLabelProvider());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
	    final String title = NLS.bind("Edit {0}", getDisplayName());
		CellEditor editor = new MultiLineTextCellEditor(parent, title);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	class MultiLineLabelProvider extends LabelProvider{
		@Override
		public String getText(Object element) {
			return element == null ? "" : element.toString().replaceAll("\n", " / ");//$NON-NLS-1$
		}
	}
}
