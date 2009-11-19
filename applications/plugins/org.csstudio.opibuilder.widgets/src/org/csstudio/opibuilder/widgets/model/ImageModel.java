/* 
 * Copyright (c) 2008 Stiftung Deutsches Elektronen-Synchrotron, 
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY.
 *
 * THIS SOFTWARE IS PROVIDED UNDER THIS LICENSE ON AN "../AS IS" BASIS. 
 * WITHOUT WARRANTY OF ANY KIND, EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED 
 * TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR PARTICULAR PURPOSE AND 
 * NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE 
 * FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
 * TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR 
 * THE USE OR OTHER DEALINGS IN THE SOFTWARE. SHOULD THE SOFTWARE PROVE DEFECTIVE 
 * IN ANY RESPECT, THE USER ASSUMES THE COST OF ANY NECESSARY SERVICING, REPAIR OR 
 * CORRECTION. THIS DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE. 
 * NO USE OF ANY SOFTWARE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 * DESY HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES, ENHANCEMENTS, 
 * OR MODIFICATIONS.
 * THE FULL LICENSE SPECIFYING FOR THE SOFTWARE THE REDISTRIBUTION, MODIFICATION, 
 * USAGE AND OTHER RIGHTS AND OBLIGATIONS IS INCLUDED WITH THE DISTRIBUTION OF THIS 
 * PROJECT IN THE FILE LICENSE.HTML. IF THE LICENSE IS NOT INCLUDED YOU MAY FIND A COPY 
 * AT HTTP://WWW.DESY.DE/LEGAL/LICENSE.HTM
 */

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
 * @author jbercic (original author)
 * @author Xihui Chen (import from SDS since 2009/09)
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
				WidgetPropertyCategory.Basic, new Path(""), FILE_EXTENSIONS));
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
