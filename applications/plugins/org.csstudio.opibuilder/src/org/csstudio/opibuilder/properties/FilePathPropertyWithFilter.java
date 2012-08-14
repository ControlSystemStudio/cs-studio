package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.PropertySSHelper;
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
		if(PropertySSHelper.getIMPL() == null)
			return null;
		return PropertySSHelper.getIMPL().FilePathPropertyDescriptorWithFilter(
				prop_id, description, widgetModel, filters);	
//		return new FilePathPropertyDescriptorWithFilter(prop_id, description,
//				widgetModel, filters);
	}

}
