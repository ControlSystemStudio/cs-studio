/*******************************************************************************
 * Copyright (c) 2010 Oak Ridge National Laboratory.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 ******************************************************************************/
package org.csstudio.opibuilder.widgets.model;

import java.util.LinkedList;
import java.util.List;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.visualparts.BorderStyle;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**The model for linking container widget.
 * @author Xihui Chen
 *
 */
public class LinkingContainerModel extends AbstractContainerModel {

	/**
	 * The ID of this widget model.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.linkingContainer"; //$NON-NLS-1$	
	
	/**
	 * The ID of the resource property.
	 */
	public static final String PROP_OPI_FILE = "opi_file"; //$NON-NLS-1$

	/**
	 * The name of the group container widget in the OPI file, which
	 * will be loaded if it is specified. If it is not specified, the whole
	 * OPI file will be loaded.
	 */
	public static final String PROP_GROUP_NAME = "group_name"; //$NON-NLS-1$
	
	/**
	 * The ID of the auto zoom property.
	 */
	public static final String PROP_ZOOMTOFITALL = "zoom_to_fit"; //$NON-NLS-1$
	
	public static final String PROP_AUTO_SIZE = "auto_size"; //$NON-NLS-1$
	
	
	/**
	 * The default value of the height property.
	 */
	private static final int DEFAULT_HEIGHT = 200;

	/**
	 * The default value of the width property.
	 */
	private static final int DEFAULT_WIDTH = 200;


	
	public LinkingContainerModel() {
		setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
		setBorderStyle(BorderStyle.LOWERED);
	}
	
	@Override
	protected void configureProperties() {
		
		addProperty(new FilePathProperty(PROP_OPI_FILE, "OPI File",
				WidgetPropertyCategory.Behavior, new Path(""), //$NON-NLS-1$
				new String[] { OPIBuilderPlugin.OPI_FILE_EXTENSION}));
		
		addProperty(new StringProperty(PROP_GROUP_NAME, "Group Name",
				WidgetPropertyCategory.Behavior, "")); //$NON-NLS-1$
		
		addProperty(new BooleanProperty(PROP_ZOOMTOFITALL, "Zoom to Fit", WidgetPropertyCategory.Display, true));
		addProperty(new BooleanProperty(PROP_AUTO_SIZE, "Auto Size", WidgetPropertyCategory.Display, false));
	}

	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * Return the target resource.
	 * 
	 * @return The target resource.
	 */
	public IPath getOPIFilePath() {
		IPath absolutePath = (IPath) getProperty(PROP_OPI_FILE).getPropertyValue();
		if(absolutePath != null && !absolutePath.isEmpty() && !absolutePath.isAbsolute())
			absolutePath = ResourceUtil.buildAbsolutePath(this, absolutePath);
		return absolutePath;
	}
	
	public void setOPIFilePath(String path){
		setPropertyValue(PROP_OPI_FILE, new Path(path));
	}

	/**
	 * Returns the auto zoom state.
	 * @return the auto zoom state
	 */
	public boolean isAutoFit() {
		return (Boolean) getProperty(PROP_ZOOMTOFITALL).getPropertyValue();
	}
	
	public boolean isAutoSize() {
		return (Boolean) getProperty(PROP_AUTO_SIZE).getPropertyValue();
	}
	
	public String getGroupName(){
		return (String)getPropertyValue(PROP_GROUP_NAME);
	}
	
	
	@Override
	public List<AbstractWidgetModel> getChildren() {
		//Linking container should have "no" children. 
		//Its children should be dynamically loaded from opi file.
		return new LinkedList<AbstractWidgetModel>();
	}
	
	@Override
	public boolean isChildrenOperationAllowable() {
		return false;
	}
}
