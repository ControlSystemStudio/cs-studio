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
		StringBuffer buffer = new StringBuffer();
		buffer.append(createParameter(PROP_NAME)+"\n");
		buffer.append("Image:\t");
		buffer.append(createParameter(PROP_FILENAME));
		return buffer.toString();
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
