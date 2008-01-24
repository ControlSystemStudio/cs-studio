package org.csstudio.sds.ui.internal.properties.view;

import org.csstudio.sds.ui.internal.properties.MultipleLineTextCellEditor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;

/**
 * Descriptor for a property that has a value which should be edited with a text
 * cell editor.
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * <p>
 * Example:
 * 
 * <pre>
 * IPropertyDescriptor pd = new TextPropertyDescriptor(&quot;surname&quot;, &quot;Last Name&quot;);
 * </pre>
 * 
 * </p>
 * 
 * @author Sven Wende
 */
public class TextPropertyDescriptor extends PropertyDescriptor {
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
	public TextPropertyDescriptor(final Object id, final String displayName,
			final String category) {
		super(id, displayName);
		assert category != null;
		setCategory(category);
		
		this.setLabelProvider(new MultipleLineTextLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new MultipleLineTextCellEditor(parent);
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
	private final class MultipleLineTextLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getText(final Object element) {
			if (element instanceof String) {
				String text = (String) element;
				if (text.contains("\n")) {
					String[] lines = text.split("\n");
					StringBuffer buffer = new StringBuffer();
					if (lines.length>0) {
						buffer.append(lines[0].trim());
						for (int i=1;i<lines.length;i++) {
							if (lines[i].trim().length()>0) {
								buffer.append(" / ");
								buffer.append(lines[i].trim());	
							}
						}
					}
					text = buffer.toString();
				}
				return text;
			} else {
				return element.toString();
			}
		}
	}
}
