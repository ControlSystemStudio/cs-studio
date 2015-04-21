package org.csstudio.opibuilder.tools.filebrowser;

import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * A custom file path property applying filters on image resource name.
 * 
 * @author SOPRA Group
 */
public class FilePathPropertyWithFilter extends FilePathProperty {

	/**
	 * The resource names which should be accepted.
	 */
	private String[] filters;

	public FilePathPropertyWithFilter(String propertyID, String description,
			WidgetPropertyCategory category, IPath defaultValue,
			String[] filters) {
		super(propertyID, description, category, defaultValue, filters);
		this.filters = filters;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new FilePathPropertyDescriptorWithFilter(prop_id, description,
				widgetModel, filters);
	}

}
