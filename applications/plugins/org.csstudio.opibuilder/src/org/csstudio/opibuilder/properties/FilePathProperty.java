package org.csstudio.opibuilder.properties;

import org.csstudio.opibuilder.properties.support.FilePathPropertyDescriptor;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.jdom.Element;



/**
 * The property for file path, which is represented as an {@link IPath}.
 * @author Xihui Chen
 *
 */
public class FilePathProperty extends AbstractWidgetProperty {

	/**
	 * The file extension, which should be accepted.
	 */
	private String[] fileExtensions;
	
	public FilePathProperty(String prop_id, String description,
			WidgetPropertyCategory category, boolean visibleInPropSheet,
			IPath defaultValue, String[] fileExtensions) {
		super(prop_id, description, category, visibleInPropSheet, defaultValue);
		this.fileExtensions = fileExtensions;
	}

	@Override
	public Object checkValue(Object value) {
		if(value == null)
			return null;
		Object acceptedValue = null;
		
		if (value instanceof IPath) {
			IPath path = (IPath) value;
			if (fileExtensions!=null && fileExtensions.length>0) {
				for (String extension : fileExtensions) {
					if (extension.equalsIgnoreCase(path.getFileExtension())) {
						acceptedValue = value; 
					}
				}
			} else {
				acceptedValue = value;
			}
			if (path.isEmpty()) {
				acceptedValue = value;
			}
		}
		
		return acceptedValue;
	}

	@Override
	protected PropertyDescriptor createPropertyDescriptor() {
		return new FilePathPropertyDescriptor(prop_id, description, fileExtensions);
	}

	@Override
	public Object readValueFromXML(Element propElement) {
		return Path.fromPortableString(propElement.getText());
	}

	@Override
	public void writeToXML(Element propElement) {
		propElement.setText(((IPath)getPropertyValue()).toPortableString());
	}

}
