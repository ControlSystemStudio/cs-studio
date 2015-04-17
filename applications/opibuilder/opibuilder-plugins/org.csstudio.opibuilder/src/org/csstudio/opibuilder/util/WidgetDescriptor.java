/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.util;

import java.util.logging.Level;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.editparts.AbstractBaseEditPart;
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

	private String onlineHelpHtml;

	/**
	 * @param element The configurationElement which hold the
	 * @param typeID The typeID of the widget.
	 * @param name The name of the widget
	 * @param iconPath The relative icon path of the widget relative to its plugin
	 * @param category The category of the widget
	 * @param pluginId The pluginID where the widget belongs to.
	 */
	public WidgetDescriptor(IConfigurationElement element, String typeID,
			String name, String description, String iconPath, String category, String pluginId,
			String onlineHelpHtml) {
		this.element = element;
		this.typeID = typeID;
		this.name = name;
		this.description = description;
		this.iconPath = iconPath;
		this.category = category;
		this.pluginId = pluginId;
		this.onlineHelpHtml = onlineHelpHtml;
	}

	/**
	 * @return the model of the widget.
	 */
	@SuppressWarnings("nls")
    public final AbstractWidgetModel getWidgetModel(){
		try {
			return (AbstractWidgetModel) element.createExecutableExtension("model_class"); //$NON-NLS-1$
		} catch (CoreException e) {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Cannot create widget model", e);
		}
		return null;
	}

	/**
	 * @return the editpart of the widget.
	 */
    @SuppressWarnings("nls")
	public final AbstractBaseEditPart getWidgetEditpart(){
		try {
			return (AbstractBaseEditPart) element.createExecutableExtension("editpart_class"); //$NON-NLS-1$
		} catch (CoreException e) {
            OPIBuilderPlugin.getLogger().log(Level.WARNING, "Cannot create edit part", e);
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

	public String getOnlineHelpHtml() {
		return onlineHelpHtml;
	}





}
