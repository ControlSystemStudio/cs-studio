package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.StringProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.BooleanProperty;

/**
 * An image widget model.
 * 
 * @author jbercic
 * 
 */
public final class ImageModel extends AbstractWidgetModel {
	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.sds.components.Image";
	
	/**
	 * The IDs of the properties.
	 */
	public static final String PROP_FILENAME = "filename";
	public static final String PROP_TOPCROP = "crop.top";
	public static final String PROP_BOTTOMCROP = "crop.bottom";
	public static final String PROP_LEFTCROP = "crop.left";
	public static final String PROP_RIGHTCROP = "crop.right";
	public static final String PROP_STRETCH = "stretch";
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getTypeID() {
		return ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureProperties() {
		addProperty(PROP_FILENAME, new StringProperty("File Name",WidgetPropertyCategory.Image,""));
		addProperty(PROP_TOPCROP, new IntegerProperty("Crop Top",WidgetPropertyCategory.Image,0));
		addProperty(PROP_BOTTOMCROP, new IntegerProperty("Crop Bottom",WidgetPropertyCategory.Image,0));
		addProperty(PROP_LEFTCROP, new IntegerProperty("Crop Left",WidgetPropertyCategory.Image,0));
		addProperty(PROP_RIGHTCROP, new IntegerProperty("Crop Right",WidgetPropertyCategory.Image,0));
		addProperty(PROP_STRETCH, new BooleanProperty("Stretch to Fit",WidgetPropertyCategory.Image,true));
	}

	public String getFilename() {
		return (String) getProperty(PROP_FILENAME).getPropertyValue();
	}
	
	public int getTopCrop() {
		return (Integer) getProperty(PROP_TOPCROP).getPropertyValue();
	}
	
	public int getBottomCrop() {
		return (Integer) getProperty(PROP_BOTTOMCROP).getPropertyValue();
	}
	
	public int getLeftCrop() {
		return (Integer) getProperty(PROP_LEFTCROP).getPropertyValue();
	}
	
	public int getRightCrop() {
		return (Integer) getProperty(PROP_RIGHTCROP).getPropertyValue();
	}
	
	public boolean getStretch() {
		return (Boolean) getProperty(PROP_STRETCH).getPropertyValue();
	}
}
