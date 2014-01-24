/*******************************************************************************
* Copyright (c) 2010-2014 ITER Organization.
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
******************************************************************************/
package org.csstudio.opibuilder.widgets.symbol.bool;

import java.util.logging.Level;

import org.csstudio.opibuilder.properties.ActionsProperty;
import org.csstudio.opibuilder.properties.BooleanProperty;
import org.csstudio.opibuilder.properties.ColorProperty;
import org.csstudio.opibuilder.properties.FilePathPropertyWithFilter;
import org.csstudio.opibuilder.properties.IntegerProperty;
import org.csstudio.opibuilder.properties.MatrixProperty;
import org.csstudio.opibuilder.properties.StringProperty;
import org.csstudio.opibuilder.properties.WidgetPropertyCategory;
import org.csstudio.opibuilder.util.ResourceUtil;
import org.csstudio.opibuilder.widgets.model.AbstractBoolControlModel;
import org.csstudio.opibuilder.widgets.symbol.Activator;
import org.csstudio.opibuilder.widgets.symbol.util.ImagePermuter;
import org.csstudio.opibuilder.widgets.symbol.util.PermutationMatrix;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.graphics.RGB;

/**
 * Control model for Boolean Symbol Image widget.
 * 
 * @author SOPRA Group
 * 
 */
public class ControlBoolSymbolModel extends AbstractBoolControlModel {

	/**
	 * Type ID for Boolean Symbol Image Control widget
	 */
	private static final String ID = "org.csstudio.opibuilder.widgets.symbol.bool.BoolControlWidget";

	/**
	 * File path of the image.
	 */
	public static final String PROP_SYMBOL_IMAGE_FILE = "image_file";
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
	 * True if the widget size is automatically adjusted to the size of the
	 * image.
	 */
	public static final String PROP_AUTOSIZE = "auto_size";
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
	// Obsolete property
	public static final String IMAGE_DISPOSITION = "image_disposition";

	/** The default string of the on label property. */
	private static final String DEFAULT_ON_LABEL = "ON";
	/** The default string of the off label property. */
	private static final String DEFAULT_OFF_LABEL = "OFF";
	/** The default color of the on color property. */
	private static final RGB DEFAULT_ON_COLOR = new RGB(0, 255, 0);
	/** The default color of the off color property. */
	private static final RGB DEFAULT_OFF_COLOR = new RGB(255, 0, 0);

	private static final String[] IMAGE_EXTENSIONS = new String[] { "gif", "png", "svg", "GIF", "PNG", "SVG" };

	/**
	 * Initialize the properties when the widget is first created.
	 */
	public ControlBoolSymbolModel() {
	}

	@Override
	public String getTypeID() {
		return ID;
	}

	@Override
	public void configureProperties() {
		super.configureProperties();

		// Override On/Off label and color default values
		addProperty(new StringProperty(PROP_ON_LABEL, "On Label",
				WidgetPropertyCategory.Display, DEFAULT_ON_LABEL));
		addProperty(new StringProperty(PROP_OFF_LABEL, "Off Label",
				WidgetPropertyCategory.Display, DEFAULT_OFF_LABEL));
		addProperty(new ColorProperty(PROP_ON_COLOR, "On Color",
				WidgetPropertyCategory.Display, DEFAULT_ON_COLOR));
		addProperty(new ColorProperty(PROP_OFF_COLOR, "Off Color",
				WidgetPropertyCategory.Display, DEFAULT_OFF_COLOR));

		// Override actions default value
		addProperty(new ActionsProperty(PROP_ACTIONS, "Actions",
				WidgetPropertyCategory.Behavior, false));

		addProperty(new FilePathPropertyWithFilter(PROP_SYMBOL_IMAGE_FILE,
				"Symbol Image", WidgetPropertyCategory.Display, new Path(""),
				IMAGE_EXTENSIONS));
		
		addProperty(new IntegerProperty(PROP_TOPCROP, "Crop Top",
				WidgetPropertyCategory.Image, 0));
		addProperty(new IntegerProperty(PROP_BOTTOMCROP, "Crop Bottom",
				WidgetPropertyCategory.Image, 0));
		addProperty(new IntegerProperty(PROP_LEFTCROP, "Crop Left",
				WidgetPropertyCategory.Image, 0));
		addProperty(new IntegerProperty(PROP_RIGHTCROP, "Crop Right",
				WidgetPropertyCategory.Image, 0));
		addProperty(new BooleanProperty(PROP_STRETCH, "Stretch to Fit",
				WidgetPropertyCategory.Image, false));
		addProperty(new BooleanProperty(PROP_AUTOSIZE, "Auto Size",
				WidgetPropertyCategory.Image, true));

		addProperty(new IntegerProperty(PROP_DEGREE, "Rotation Angle",
				WidgetPropertyCategory.Image, 0));
		addProperty(new BooleanProperty(PROP_FLIP_HORIZONTAL,
				"Flip Horizontal", WidgetPropertyCategory.Image, false));
		addProperty(new BooleanProperty(PROP_FLIP_VERTICAL, "Flip Vertical",
				WidgetPropertyCategory.Image, false));
//		setPropertyVisibleAndSavable(PROP_DEGREE, false, true);
//		setPropertyVisibleAndSavable(PROP_FLIP_HORIZONTAL, false, true);
//		setPropertyVisibleAndSavable(PROP_FLIP_VERTICAL, false, true);
		
		addProperty(new MatrixProperty(PERMUTATION_MATRIX,
				"Permutation Matrix", WidgetPropertyCategory.Image,
				PermutationMatrix.generateIdentityMatrix().getMatrix()));
		setPropertyVisibleAndSavable(PERMUTATION_MATRIX, false, true);
		// Obsolete property
		addProperty(new StringProperty(IMAGE_DISPOSITION, "Image Disposition",
				WidgetPropertyCategory.Image, "1234"));
		setPropertyVisibleAndSavable(IMAGE_DISPOSITION, false, false);
	}

	/* ************************* */
	/* Common code with monitor */
	/* ************************* */

	/**
	 * Get the path to the specified file.
	 * 
	 * @return The path to the specified file
	 */
	public IPath getSymbolImagePath() {
		IPath absolutePath = (IPath) getProperty(PROP_SYMBOL_IMAGE_FILE)
				.getPropertyValue();
		if (!absolutePath.isAbsolute()) {
			absolutePath = ResourceUtil.buildAbsolutePath(this, absolutePath);
		}
		return absolutePath;
	}

	/**
	 * Get the amount of pixels, which should be cropped from the top edge of
	 * the image.
	 * 
	 * @return The amount of pixels
	 */
	public int getTopCrop() {
		return (Integer) getProperty(PROP_TOPCROP).getPropertyValue();
	}

	/**
	 * Get the amount of pixels, which should be cropped from the bottom edge of
	 * the image.
	 * 
	 * @return The amount of pixels
	 */
	public int getBottomCrop() {
		return (Integer) getProperty(PROP_BOTTOMCROP).getPropertyValue();
	}

	/**
	 * Get the amount of pixels, which should be cropped from the left edge of
	 * the image.
	 * 
	 * @return The amount of pixels
	 */
	public int getLeftCrop() {
		return (Integer) getProperty(PROP_LEFTCROP).getPropertyValue();
	}

	/**
	 * Get the amount of pixels, which should be cropped from the right edge of
	 * the image.
	 * 
	 * @return The amount of pixels
	 */
	public int getRightCrop() {
		return (Integer) getProperty(PROP_RIGHTCROP).getPropertyValue();
	}

	/**
	 * Check if the image should be stretched.
	 * 
	 * @return True if stretched, false otherwise
	 */
	public boolean getStretch() {
		return (Boolean) getProperty(PROP_STRETCH).getPropertyValue();
	}

	/**
	 * Check if the widget should be auto sized according the image size.
	 * 
	 * @return True if auto sized, false otherwise
	 */
	public boolean isAutoSize() {
		return (Boolean) getProperty(PROP_AUTOSIZE).getPropertyValue();
	}

	/**
	 * Get the current degree of the image.
	 * 
	 * @return The degree value
	 */
	public int getDegree() {
		return (Integer) getProperty(PROP_DEGREE).getPropertyValue();
	}

	/**
	 * Check if an horizontal flip was applied.
	 * 
	 * @return True if horizontal flip, false otherwise
	 */
	public boolean isFlipHorizontal() {
		return (Boolean) getProperty(PROP_FLIP_HORIZONTAL).getPropertyValue();
	}

	/**
	 * Check if an vertical flip was applied.
	 * 
	 * @return True if vertical flip, false otherwise
	 */
	public boolean isFlipVertical() {
		return (Boolean) getProperty(PROP_FLIP_VERTICAL).getPropertyValue();
	}
	
	/**
	 * Get the current disposition of the image.
	 * 
	 * @return The permutation matrix
	 */
	public PermutationMatrix getPermutationMatrix() {
		return new PermutationMatrix((double[][]) getProperty(
				PERMUTATION_MATRIX).getPropertyValue());
	}

	@Override
	public void rotate90(boolean clockwise) {
		int degree = (Integer) getPropertyValue(MonitorBoolSymbolModel.PROP_DEGREE);
		switch (degree) {
		case 0:
			degree = clockwise ? 90 : 270;
			break;
		case 90:
			degree = clockwise ? 180 : 0;
			break;
		case 180:
			degree = clockwise ? 270 : 90;
			break;
		case 270:
			degree = clockwise ? 0 : 180;
			break;
		default:
			Activator.getLogger().log(Level.WARNING,
					"ERROR in value of degree " + degree);
		}
		setPropertyValue(MonitorBoolSymbolModel.PROP_DEGREE, degree);
	}

	@Override
	public void flipHorizontally() {
		boolean oldValue = (Boolean) getPropertyValue(MonitorBoolSymbolModel.PROP_FLIP_HORIZONTAL);
		setPropertyValue(MonitorBoolSymbolModel.PROP_FLIP_HORIZONTAL, !oldValue);
	}

	@Override
	public void flipVertically() {
		boolean oldValue = (Boolean) getPropertyValue(MonitorBoolSymbolModel.PROP_FLIP_VERTICAL);
		setPropertyValue(MonitorBoolSymbolModel.PROP_FLIP_VERTICAL, !oldValue);
	}
	
	@Override
	public void setPropertyValue(Object id, Object value) {
		// Override obsolete properties
		if (id != null && id instanceof String) {
			if (((String) id).equals(IMAGE_DISPOSITION)) {
				id = PERMUTATION_MATRIX;
				value = ImagePermuter.getMatrix((String) value);
			}
		}
		super.setPropertyValue(id, value);
	}

}
