package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.OPIBuilderPlugin;
import org.csstudio.opibuilder.model.AbstractContainerModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
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
	 * The ID of the auto zoom property.
	 */
	public static final String PROP_ZOOMTOFITALL = "zoom_to_fit"; //$NON-NLS-1$
	
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
				WidgetPropertyCategory.Behavior, new Path(""),
				new String[] { OPIBuilderPlugin.OPI_FILE_EXTENSION}));
		
		addProperty(new BooleanProperty(PROP_ZOOMTOFITALL, "Zoom to Fit", WidgetPropertyCategory.Display, true));
	
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
	
}
