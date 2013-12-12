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
import org.csstudio.opibuilder.properties.ComboProperty;
import org.csstudio.opibuilder.properties.FilePathProperty;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.MatrixProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.swt.widgets.util.PermutationMatrix;
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
	 * File path of the image.
	 */
	public static final String PROP_IMAGE_FILE = "image_file";
	/**
	 * Crop part (in pixels) on top side of the image.
	 */
	public static final String PROP_TOPCROP = "crop_top";
	/**
	 * Crop part (in pixels) on bottom side of the image.
	 */
	public static final String PROP_BOTTOMCROP = "crop_bottom";
	/**
	 * Crop part (in pixels) on left side of the image.
	 */
	public static final String PROP_LEFTCROP = "crop_left";
	/**
	 * Crop part (in pixels) on right side of the image.
	 */
	public static final String PROP_RIGHTCROP = "crop_right";
	/**
	 * True if the image should be stretched to the widget size.
	 */
	public static final String PROP_STRETCH = "stretch_to_fit";	
	/**
	 * True if the widget size is automatically adjusted to the size of the image.
	 */
	public static final String PROP_AUTOSIZE= "auto_size";	
	
	/**
	 * True if the widget doesn't show animation even it is a animated image file.
	 */
	public static final String PROP_NO_ANIMATION= "no_animation";	
	
	/**
	 * The default value for the file extensions.
	 */
	private static final String[] FILE_EXTENSIONS = new String[] {"jpg", "jpeg", "gif", "bmp", "png", "svg"};
	
	/**
	 * Degree value of the image.
	 */
	public static final String PROP_DEGREE = "degree";
	/**
	 * Horizontal flip applied on the image.
	 */
	public static final String PROP_FLIP_HORIZONTAL = "flip_horizontal";
	/**
	 * Vertical flip applied on the image.
	 */
	public static final String PROP_FLIP_VERTICAL = "flip_vertical";
	
	/**
	 * Image disposition (permutation matrix)
	 */
	public static final String PERMUTATION_MATRIX = "permutation_matrix";
	
	private static final String[] allowedDegrees = new String[] { "0", "90", "180", "270" };
	
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
		addProperty(new ComboProperty(PROP_DEGREE, "Rotation Angle",
				WidgetPropertyCategory.Image, allowedDegrees, 0));
		addProperty(new BooleanProperty(PROP_FLIP_HORIZONTAL,
				"Flip Horizontal", WidgetPropertyCategory.Image, false));
		addProperty(new BooleanProperty(PROP_FLIP_VERTICAL, "Flip Vertical",
				WidgetPropertyCategory.Image, false));
		addProperty(new MatrixProperty(PERMUTATION_MATRIX,
				"Permutation Matrix", WidgetPropertyCategory.Image,
				PermutationMatrix.generateIdentityMatrix().getMatrix()));
		setPropertyVisibleAndSavable(PERMUTATION_MATRIX, false, true);
	}
	
	/**
	 * Returns the path to the specified file.
	 * @return The path to the specified file
	 */
	public IPath getFilename() {
		IPath absolutePath = (IPath) getProperty(PROP_IMAGE_FILE).getPropertyValue();
		if(!absolutePath.isAbsolute())
			absolutePath = ResourceUtil.buildAbsolutePath(this, absolutePath);
		return absolutePath;
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
	
	/**
	 * @return The permutation matrix
	 */
	public PermutationMatrix getPermutationMatrix() {
		return new PermutationMatrix((double[][]) getProperty(
				PERMUTATION_MATRIX).getPropertyValue());
	}
	
	public int getDegree(int index) {
		return Integer.valueOf(allowedDegrees[index]);
	}
	
	@Override
	public void rotate90(boolean clockwise) {
		int index = (Integer) getPropertyValue(ImageModel.PROP_DEGREE);
		if (clockwise) {
			if (index == allowedDegrees.length - 1) index = 0;
			else index++;
		} else {
			if (index == 0) index = allowedDegrees.length - 1;
			else index--;
		}
		setPropertyValue(ImageModel.PROP_DEGREE, index);
	}

	@Override
	public void flipHorizontally() {
		boolean oldValue = (Boolean) getPropertyValue(ImageModel.PROP_FLIP_HORIZONTAL);
		setPropertyValue(ImageModel.PROP_FLIP_HORIZONTAL, !oldValue);
	}

	@Override
	public void flipVertically() {
		boolean oldValue = (Boolean) getPropertyValue(ImageModel.PROP_FLIP_VERTICAL);
		setPropertyValue(ImageModel.PROP_FLIP_VERTICAL, !oldValue);
	}
}