package org.csstudio.opibuilder.util;

import org.csstudio.opibuilder.editparts.AbstractWidgetEditPart;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;

/**This class help to hold the related information for a widget which are 
 * acquired from the extension. The widget model and editpart will be loaded lazily, 
 * which also make the widget plugin loaded lazily.
 * @author Xihui Chen
 *
 */
public class WidgetDescriptor {

	/**
	 * The configurationElement which hold the 
	 */
	private IConfigurationElement element;
	
	/**
	 * The typeID of the widget.
	 */
	private String typeID;
	
	/**
	 * The name of the widget
	 */
	private String name;
	
	/**
	 * The description of the widget
	 */
	private String description;
	
	
	/**
	 * The relative icon path of the widget relative to its plugin
	 */
	private String iconPath;
	
	/**
	 * The category of the widget
	 */
	private String category;
	
	/**
	 * The pluginID where the widget belongs to.
	 */
	private String pluginId;

	/**
	 * @param element The configurationElement which hold the 
	 * @param typeID The typeID of the widget.
	 * @param name The name of the widget
	 * @param iconPath The relative icon path of the widget relative to its plugin
	 * @param category The category of the widget
	 * @param pluginId The pluginID where the widget belongs to.
	 */
	public WidgetDescriptor(IConfigurationElement element, String typeID,
			String name, String description, String iconPath, String category, String pluginId) {
		this.element = element;
		this.typeID = typeID;
		this.name = name;
		this.description = description;
		this.iconPath = iconPath;
		this.category = category;
		this.pluginId = pluginId;
	}

	/**
	 * @return the model of the widget.
	 */
	public final AbstractWidgetModel getWidgetModel(){
		try {
			return (AbstractWidgetModel) element.createExecutableExtension("model_class");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @return the editpart of the widget.
	 */
	public final AbstractWidgetEditPart getWidgetEditpart(){
		try {
			return (AbstractWidgetEditPart) element.createExecutableExtension("editpart_class");
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * @return the typeID
	 */
	public final String getTypeID() {
		return typeID;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @return the iconPath
	 */
	public final String getIconPath() {
		return iconPath;
	}

	/**
	 * @return the category
	 */
	public final String getCategory() {
		return category;
	}

	/**
	 * @return the pluginId
	 */
	public final String getPluginId() {
		return pluginId;
	}

	
	public String getDescription() {
		return description;
	}
	
	
	
	
	
}
