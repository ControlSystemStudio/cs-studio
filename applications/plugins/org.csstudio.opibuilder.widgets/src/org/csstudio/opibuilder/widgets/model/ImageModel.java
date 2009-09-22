package org.csstudio.opibuilder.widgets.model;

import org.csstudio.opibuilder.model.AbstractWidgetModel;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 * An image widget model.
 * 
 * @author Xihui Chen
 * 
 */
public final class ImageModel extends AbstractWidgetModel {
	/**
	 * Unique identifier.
	 */
	public static final String ID = "org.csstudio.opibuilder.widgets.Image";
	
	/**
	 * The ID of the <i>filename</i> property.
	 */
	public static final String PROP_IMAGE_FILE = "image_file";
	/**
	 * The ID of the <i>topcrop</i> property.
	 */
	public static final String PROP_TOPCROP = "crop_top";
	/**
	 * The ID of the <i>bottomcrop</i> property.
	 */
	public static final String PROP_BOTTOMCROP = "crop_bottom";
	/**
	 * The ID of the <i>leftcrop</i> property.
	 */
	public static final String PROP_LEFTCROP = "crop_left";
	/**
	 * The ID of the <i>rightcrop</i> property.
	 */
	public static final String PROP_RIGHTCROP = "crop_right";
	/**
	 * The ID of the <i>stretch</i> property.
	 */
	public static final String PROP_STRETCH = "stretch_to_fit";	
	/**
	 * The ID of the <i>autosize</i> property.
	 */
	public static final String PROP_AUTOSIZE= "auto_size";	
	
	/**
	 * The ID of the <i>stop animation</i> property.
	 */
	public static final String PROP_NO_ANIMATION= "no_animation";	
	
	/**
	 * The default value for the file extensions.
	 */
	private static final String[] FILE_EXTENSIONS = new String[] {"jpg", "jpeg", "gif", "bmp", "png"};
	
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
		addProperty( new FilePathProperty(PROP_IMAGE_FILE,"Image File", 
				WidgetPropertyCategory.Image, new Path(""), FILE_EXTENSIONS));
		addProperty(new IntegerProperty(PROP_TOPCROP, "Crop Top",
				WidgetPropertyCategory.Image,0));
		addProperty(new IntegerProperty(PROP_BOTTOMCROP, "Crop Bottom",
				WidgetPropertyCategory.Image,0));
		addProperty(new IntegerProperty(PROP_LEFTCROP, "Crop Left",
				WidgetPropertyCategory.Image,0));
		addProperty(new IntegerProperty(PROP_RIGHTCROP, "Crop Right",
				WidgetPropertyCategory.Image,0));
		addProperty(new BooleanProperty(PROP_STRETCH, "Stretch to Fit",
				WidgetPropertyCategory.Image,false));
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size",
				WidgetPropertyCategory.Image,true));
		addProperty(new BooleanProperty(PROP_NO_ANIMATION, "No Animation",
				WidgetPropertyCategory.Image,false));
	}
	
	/**
	 * Returns the path to the specified file.
	 * @return The path to the specified file
	 */
	public IPath getFilename() {
		return (IPath) getProperty(PROP_IMAGE_FILE).getPropertyValue();
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
	
	/**
	 *  @return True if the widget should be auto sized according the image size.
	 */
	public boolean isAutoSize() {
		return (Boolean) getProperty(PROP_AUTOSIZE).getPropertyValue();
	}
	
	/**
	 *  @return True if the animation is stopped.
	 */
	public boolean isStopAnimation() {
		return (Boolean) getProperty(PROP_NO_ANIMATION).getPropertyValue();
	}
}
