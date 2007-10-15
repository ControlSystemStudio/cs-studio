package org.csstudio.sds.components.model;

import org.csstudio.sds.model.AbstractWidgetModel;
import org.csstudio.sds.model.WidgetPropertyCategory;
import org.csstudio.sds.model.properties.BooleanProperty;
import org.csstudio.sds.model.properties.IntegerProperty;
import org.csstudio.sds.model.properties.ResourceProperty;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

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
	 * The ID of the <i>filename</i> property.
	 */
	public static final String PROP_FILENAME = "filename";
	/**
	 * The ID of the <i>topcrop</i> property.
	 */
	public static final String PROP_TOPCROP = "crop.top";
	/**
	 * The ID of the <i>bottomcrop</i> property.
	 */
	public static final String PROP_BOTTOMCROP = "crop.bottom";
	/**
	 * The ID of the <i>leftcrop</i> property.
	 */
	public static final String PROP_LEFTCROP = "crop.left";
	/**
	 * The ID of the <i>rightcrop</i> property.
	 */
	public static final String PROP_RIGHTCROP = "crop.right";
	/**
	 * The ID of the <i>stretch</i> property.
	 */
	public static final String PROP_STRETCH = "stretch";	
	/**
	 * The default value for the file extensions.
	 */
	private static final String[] FILE_EXTENSIONS = new String[] {"*", "jpg", "jepg", "gif", "bmp", "png"};
	
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
		//addProperty(PROP_FILENAME, new StringProperty("File Name",WidgetPropertyCategory.Image,""));
		addProperty(PROP_FILENAME, new ResourceProperty("File", 
				WidgetPropertyCategory.Image, new Path(""), FILE_EXTENSIONS));
		addProperty(PROP_TOPCROP, new IntegerProperty("Crop Top",
				WidgetPropertyCategory.Image,0));
		addProperty(PROP_BOTTOMCROP, new IntegerProperty("Crop Bottom",
				WidgetPropertyCategory.Image,0));
		addProperty(PROP_LEFTCROP, new IntegerProperty("Crop Left",
				WidgetPropertyCategory.Image,0));
		addProperty(PROP_RIGHTCROP, new IntegerProperty("Crop Right",
				WidgetPropertyCategory.Image,0));
		addProperty(PROP_STRETCH, new BooleanProperty("Stretch to Fit",
				WidgetPropertyCategory.Image,true));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getDefaultToolTip() {
		return "##Name##\nImage:\t##File##";
	}

	/**
	 * Returns the path to the specified file.
	 * @return The path to the specified file
	 */
	public IPath getFilename() {
		return (IPath) getProperty(PROP_FILENAME).getPropertyValue();
	}
	
	/**
	 * Returns the amount of pixels, which should be cropped from the top edge of the image.
	 * @return The amount of pixels
	 */
	public int getTopCrop() {
		return (Integer) getProperty(PROP_TOPCROP).getPropertyValue();
	}
	
	/**
	 * Returns the amount of pixels, which should be cropped from the bottom edge of the image.
	 * @return The amount of pixels
	 */
	public int getBottomCrop() {
		return (Integer) getProperty(PROP_BOTTOMCROP).getPropertyValue();
	}
	
	/**
	 * Returns the amount of pixels, which should be cropped from the left edge of the image.
	 * @return The amount of pixels
	 */
	public int getLeftCrop() {
		return (Integer) getProperty(PROP_LEFTCROP).getPropertyValue();
	}
	
	/**
	 * Returns the amount of pixels, which should be cropped from the right edge of the image.
	 * @return The amount of pixels
	 */
	public int getRightCrop() {
		return (Integer) getProperty(PROP_RIGHTCROP).getPropertyValue();
	}
	
	/**
	 * Returns if the image should be stretched.
	 * @return True is it should be stretched, false otherwise
	 */
	public boolean getStretch() {
		return (Boolean) getProperty(PROP_STRETCH).getPropertyValue();
	}
}
