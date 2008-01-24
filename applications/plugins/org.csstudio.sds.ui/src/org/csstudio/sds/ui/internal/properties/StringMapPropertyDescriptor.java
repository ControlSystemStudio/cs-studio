package org.csstudio.sds.ui.internal.properties;

import java.util.Map;

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
public class StringMapPropertyDescriptor extends PropertyDescriptor {
	
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
	public StringMapPropertyDescriptor(final Object id, final String displayName,
			final String category) {
		super(id, displayName);
		_name = displayName;
		assert category != null;
		setCategory(category);
		
		this.setLabelProvider(new MapLabelProvider());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CellEditor createPropertyEditor(final Composite parent) {
		CellEditor editor = new StringMapCellEditor(parent, _name);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}
	
	/**
	 * A label provider for a Map of Strings.
	 * 
	 * @author Kai Meyer
	 */
	private final class MapLabelProvider extends LabelProvider {
		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("unchecked")
		@Override
		public String getText(final Object element) {
			if (element instanceof Map) {
				Map<String, String> map = (Map<String, String>) element;
				StringBuffer buffer = new StringBuffer("[");
				if (!map.isEmpty()) {
					String[] strings = map.keySet().toArray(new String[map.keySet().size()]);
					this.addAliasText(buffer, strings[0], map.get(strings[0]));
					for (int i=1;i<map.size();i++) {
						buffer.append(", ");
						this.addAliasText(buffer, strings[0], map.get(strings[0]));
					}
				}
				buffer.append("]");
				return buffer.toString();
			} else {
				return element.toString();
			}
		}
		
		/**
		 * Adds the text of the given AliasDescriptor to the StringBuffer.
		 * @param buffer
		 * 			The StringBuffer
		 * @param key
		 * 			The key
		 * @param value
		 * 			The value
		 */
		private void addAliasText(final StringBuffer buffer, final String key, final String value) {
			buffer.append(key);
			buffer.append(": ");
			buffer.append(value);
		}
	}
}
